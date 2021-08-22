package com.aptive.mediaplayer.model

object MediaModel {
    // Convert milliseconds to to time
     fun milliSecondsToTimer(milliseconds: Int): String {
        var finalTimerString = ""
        var secondsString = ""
        val hours = (milliseconds / (1000 * 60 * 60))
        val minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000)
        if (hours > 0) finalTimerString = "$hours:"
        secondsString = if (seconds < 10) "0$seconds" else "" + seconds
        finalTimerString = "$finalTimerString$minutes:$secondsString"
        return finalTimerString
    }
}