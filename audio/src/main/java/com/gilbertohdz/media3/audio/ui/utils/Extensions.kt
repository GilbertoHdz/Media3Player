package com.gilbertohdz.media3.audio.ui.utils

import java.util.concurrent.TimeUnit

fun Number.toFormattedDuration(timeUnit: TimeUnit = TimeUnit.SECONDS): String {
    val thisLong = toLong()
    return "%02d:%02d".format(
        timeUnit.toMinutes(thisLong),
        timeUnit.toSeconds(thisLong) % 60
    )
}