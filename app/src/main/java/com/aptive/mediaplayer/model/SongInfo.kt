package com.aptive.mediaplayer.model

import android.graphics.drawable.Drawable
import com.google.gson.annotations.SerializedName

data class SongInfo(
    @SerializedName("song_name")
    var song_name: String,
    @SerializedName("song_description")
    var song_description: String,
    @SerializedName("song_image")
    var song_image: Drawable
)