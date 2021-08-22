package com.aptive.mediaplayer.model

import com.google.gson.annotations.SerializedName

data class SongPath(
    @SerializedName("song_path")
    var song_path: String,
    @SerializedName("song_name")
    var song_name: String
)