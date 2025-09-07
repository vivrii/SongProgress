package org.vivrii.songprogress.domain.models

data class Artist(
    val name: String,
) {
    val tracks = HashMap<String, Track>()
    val albums = HashMap<String, Album>()
}
