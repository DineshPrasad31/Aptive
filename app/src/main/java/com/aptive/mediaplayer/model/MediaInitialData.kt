package com.aptive.mediaplayer.model

import com.google.gson.annotations.SerializedName

data class MediaInitialData(
    @SerializedName("song_duration")
    var song_duration: String,
    @SerializedName("seekbar_max_value")
    var seekbar_max_value: Int
)