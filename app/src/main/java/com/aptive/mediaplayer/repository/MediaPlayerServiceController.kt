package com.aptive.mediaplayer.repository

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.aptive.mediaplayer.R
import com.aptive.mediaplayer.model.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class MediaPlayerServiceController() : Service(), KoinComponent {
    private val TAG = MediaPlayerServiceController::class.java.simpleName
    private var isStarted: Boolean = false
    private val mPlayPauseMutableLiveData = MutableLiveData<PlayPauseData>()
    private val mMediaMutableLiveData = MutableLiveData<MediaInitialData>()
    private val mSongInfoMutableLiveData = MutableLiveData<SongInfo>()
    private val compositeDisposable = CompositeDisposable()
    private var mSongList = arrayListOf<SongPath>()
    private var mCurrentSongIndex = 0
    private val mSeekBarMutableLiveData = MutableLiveData<SeekBarData>()
    private val mediaRepository: MediaRepository by inject()


    lateinit var mediaPlayer: MediaPlayer

    // Binder given to clients
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): MediaPlayerServiceController = this@MediaPlayerServiceController
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind")
        playSong()
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        initMediaPlayer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startMyOwnForeground()
        readFile()
        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    // Play and pause Media Player
    fun playPauseMedia() {
        if (mSongList.size > 0) {
            if (mediaPlayer.isPlaying) mediaPlayer.pause() else startMediaPlayer()
        } else {
            Toast.makeText(this, "file not found", Toast.LENGTH_LONG)
                .show()
        }
    }

    // Pause Media Player
    fun pauseMedia() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            val playPauseData = PlayPauseData(false)
            mPlayPauseMutableLiveData.postValue(playPauseData)
        }
    }

    // Update Media Seek bar and also media player timer
    fun updateMediaSeekBar() {
        if (mSongList.isEmpty()) return
        val mediaInitialData = MediaInitialData(
            mediaRepository.milliSecondsToTimer(mediaPlayer.duration),
            (mediaPlayer.duration) / 1000
        )
        mMediaMutableLiveData.postValue(mediaInitialData)

        Observable.interval(0, 1, TimeUnit.MILLISECONDS)
            .flatMap {
                return@flatMap Observable.create<String> { emitter ->
                    emitter.onNext(emitter.toString())
                    emitter.onComplete()
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val seekValue = (mediaPlayer.currentPosition) / 1000
                val progressData =
                    SeekBarData(
                        mediaRepository.milliSecondsToTimer(mediaPlayer.currentPosition),
                        seekValue
                    )
                mSeekBarMutableLiveData.postValue(progressData)
            }.run(compositeDisposable::add)


    }

    // Initiate  Next Song
    fun forwardSong() {
        if (mSongList.size > 0) {
            val playPauseData = PlayPauseData(false)
            mPlayPauseMutableLiveData.postValue(playPauseData)
            mediaPlayer.reset()
            isStarted = true
            if (mCurrentSongIndex < (mSongList.size - 1)) {
                mCurrentSongIndex += 1
                playSong()
            } else {
                mCurrentSongIndex = 0
                playSong()
            }
        } else {
            Toast.makeText(this, "file not found", Toast.LENGTH_LONG)
                .show()
        }
    }

    // Initiate Previous song
    fun backwardSong() {
        if (mSongList.size > 0) {
            val playPauseData = PlayPauseData(false)
            mPlayPauseMutableLiveData.postValue(playPauseData)
            mediaPlayer.reset()
            isStarted = true
            if (mCurrentSongIndex == 0) {
                mCurrentSongIndex = 0
                playSong()
            } else {
                mCurrentSongIndex -= 1
                playSong()
            }
        } else {
            Toast.makeText(this, "file not found", Toast.LENGTH_LONG)
                .show()
        }
    }

    fun getSeekBarValue(): MutableLiveData<SeekBarData> {
        return mSeekBarMutableLiveData
    }

    fun getInitialData(): MutableLiveData<MediaInitialData> {
        return mMediaMutableLiveData
    }

    fun getPlayPauseData(): MutableLiveData<PlayPauseData> {
        return mPlayPauseMutableLiveData
    }

    fun getSongInfo(): MutableLiveData<SongInfo> {
        return mSongInfoMutableLiveData
    }

    // Clear Disposal which updating Seek bar
    fun clearDisposal() {
        compositeDisposable.clear()
    }

    // Set seek bar position on drag seek bar
    fun setSeekPosition(seekPosition: Int) {
        mediaPlayer.seekTo(seekPosition * 1000)
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
    }

    // Play song
    private fun startMediaPlayer() {
        Single.fromCallable {
            Log.d(TAG, "fromCallablestartMediaPlayer")
            mediaPlayer.start()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

    }

    private fun playSong() {
        Single.fromCallable {
            mediaPlayer.setOnPreparedListener {
                Log.d(TAG, "setOnPreparedListener")
                if (isStarted) {
                    mediaPlayer = it
                    startMediaPlayer()
                    val playPauseData = PlayPauseData(true)
                    mPlayPauseMutableLiveData.postValue(playPauseData)
                }
                updateMediaSeekBar()

            }

            mediaPlayer.setOnCompletionListener {
                Log.d(TAG, "Completed")
                forwardSong()
                val playPauseData = PlayPauseData(false)
                mPlayPauseMutableLiveData.postValue(playPauseData)
            }

            mediaPlayer.setOnErrorListener(MediaPlayer.OnErrorListener { mp, what, extra ->
                Log.d(TAG, "setOnErrorListener" + what)
                true
            })

            try {
                setSongInfoData(mSongList[mCurrentSongIndex])
                val uri = Uri.parse(mSongList[mCurrentSongIndex].song_path)
                Log.d(TAG, "songPath:: " + mSongList[mCurrentSongIndex].song_path)
                Log.d(TAG, "uri:: " + uri)
                mediaPlayer.setDataSource(this, uri)
                mediaPlayer.prepareAsync()
            } catch (exception: Exception) {
                Log.d(TAG, "exception:: " + exception.message)
            } catch (illegalStateException: IllegalStateException) {
                Log.d(TAG, "IllegalStateException:: " + illegalStateException.message)
            } catch (ioException: IOException) {
                Log.d(TAG, "IOException:: " + ioException.message)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }


    //Read files from Phone storage
    private fun readFile() {
        mSongList = mediaRepository.readSongsOffline()
    }

    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "com.example.simpleapp"
        val channelName = "My Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("App is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }

    // set Song info like name, image, description
    private fun setSongInfoData(songPath: SongPath) {
        val songFile = File(songPath.song_path)
        val bitmap = getSongThumbNail(songPath.song_path)
        val imageDrawable: Drawable = if (bitmap != null) BitmapDrawable(
            applicationContext.resources,
            bitmap
        ) else applicationContext.resources.getDrawable(R.drawable.no_image, null)
        Log.d("file path ", "song path:" + songFile.absolutePath)
        val songInfo = SongInfo(songPath.song_name, "", imageDrawable)
        mSongInfoMutableLiveData.postValue(songInfo)
    }

    // Get song thumbnail form song file
    private fun getSongThumbNail(path: String?): Bitmap? {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(applicationContext, Uri.parse(path))
        val byte1 = mmr.embeddedPicture
        mmr.release()
        return if (byte1 != null) BitmapFactory.decodeByteArray(
            byte1,
            0,
            byte1.size
        ) else null
    }
}
