package com.aptive.mediaplayer.viewmodel


import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.aptive.mediaplayer.application.MediaPlayerApplication
import com.aptive.mediaplayer.model.MediaInitialData
import com.aptive.mediaplayer.model.PlayPauseData
import com.aptive.mediaplayer.model.SeekBarData
import com.aptive.mediaplayer.model.SongInfo
import com.aptive.mediaplayer.repository.MediaPlayerServiceController
import com.aptive.mediaplayer.util.Constants


class MediaViewModel(application: Application) :
    BaseViewModel(application) {
    private val TAG = MediaViewModel::class.java.simpleName
    private var mBound: Boolean = false
    private lateinit var serviceConnected: ServiceConnected

    @SuppressLint("StaticFieldLeak")
    private var mContext = getApplication<MediaPlayerApplication>().baseContext
    private lateinit var mediaPlayerServiceController: MediaPlayerServiceController

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MediaPlayerServiceController.LocalBinder
            mediaPlayerServiceController = binder.getService()
            serviceConnected.isSerViceConnected()
            mBound = true
            Log.d(TAG, "onServiceConnected")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
            Log.d(TAG, "onServiceDisconnected")
        }
    }

    fun initialize(serviceConnected: ServiceConnected) {
        this.serviceConnected = serviceConnected
        val serviceIntent = Intent(mContext, MediaPlayerServiceController::class.java)
        serviceIntent.putExtra(Constants.KEY_MEDIA_FUNCTION, Constants.PLAY)
        mContext.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    fun playPauseMedia() {
        mediaPlayerServiceController.playPauseMedia()
    }

    fun pauseMedia() {
        mediaPlayerServiceController.pauseMedia()
    }

    // Update Media Seek bar and also media player timer
    fun updateMediaSeekBar() {
        mediaPlayerServiceController.updateMediaSeekBar()
    }


    // Initiate  Next Song
    fun forwardSong() {
        mediaPlayerServiceController.forwardSong()
    }

    // Initiate Previous song
    fun backwardSong() {
        mediaPlayerServiceController.backwardSong()
    }

    fun getSeekBarValue(): MutableLiveData<SeekBarData> {
        return mediaPlayerServiceController.getSeekBarValue()
    }

    fun getInitialData(): MutableLiveData<MediaInitialData> {
        return mediaPlayerServiceController.getInitialData()
    }

    fun getPlayPauseData(): MutableLiveData<PlayPauseData> {
        return mediaPlayerServiceController.getPlayPauseData()
    }

    fun getSongInfo(): MutableLiveData<SongInfo> {
        return mediaPlayerServiceController.getSongInfo()
    }

    // Clear Disposal which updating Seek bar
    fun clearDisposal() {
        mediaPlayerServiceController.clearDisposal()
    }

    fun setSeekPosition(seekPosition: Int) {
        mediaPlayerServiceController.setSeekPosition(seekPosition)
    }

    fun stopService() {
        mContext.unbindService(connection)
    }

    interface ServiceConnected {
        fun isSerViceConnected()
    }

}
