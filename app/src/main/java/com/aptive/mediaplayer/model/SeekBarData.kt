package com.aptive.mediaplayer.model

import com.google.gson.annotations.SerializedName

data class SeekBarData(
    @SerializedName("timer_value")
    var timer_value: String,
    @SerializedName("seek_bar_value")
    var seek_bar_value: Int
)