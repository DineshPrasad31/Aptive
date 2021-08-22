package com.aptive.mediaplayer.repository

import com.aptive.mediaplayer.R
import com.aptive.mediaplayer.model.MediaModel
import com.aptive.mediaplayer.model.SongPath
import com.aptive.mediaplayer.util.Constants

class MediaRepository {
    private val mSongList = arrayListOf<SongPath>()

    fun readSongsOffline(): ArrayList<SongPath> {
        val songPath2 =
            SongPath(Constants.PREFIX_RAW + R.raw.song_one, "song_one")
        mSongList.add(songPath2)
        val songPath3 =
            SongPath(Constants.PREFIX_RAW + R.raw.terimitti, "terimitti")
        mSongList.add(songPath3)
        val songPath4 =
            SongPath(Constants.PREFIX_RAW + R.raw.song_two, "song_two")
        mSongList.add(songPath4)
        val songPath5 =
            SongPath(Constants.PREFIX_RAW + R.raw.haridware, "haridware")
        mSongList.add(songPath5)
        val songPath6 =
            SongPath(Constants.PREFIX_RAW + R.raw.song_three, "song_three")
        mSongList.add(songPath6)
        return mSongList
    }

    // Convert milliseconds to to time
     fun milliSecondsToTimer(milliseconds: Int): String {
        return MediaModel.milliSecondsToTimer(milliseconds)
    }
}