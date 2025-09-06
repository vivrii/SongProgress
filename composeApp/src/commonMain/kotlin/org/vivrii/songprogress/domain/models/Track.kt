package org.vivrii.songprogress.domain.models

data class Track(
    val title: String,
) {
    var listenCount: Int = 0
}
