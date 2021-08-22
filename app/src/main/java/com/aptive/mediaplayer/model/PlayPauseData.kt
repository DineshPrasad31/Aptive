package com.aptive.mediaplayer.model

import com.google.gson.annotations.SerializedName

data class PlayPauseData(
    @SerializedName("isChecked")
    var isChecked: Boolean
)