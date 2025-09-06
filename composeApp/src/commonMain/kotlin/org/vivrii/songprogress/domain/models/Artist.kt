package org.vivrii.songprogress.domain.models

data class Artist(
    val name: String,
) {
    val albums = HashMap<String, Album>()
}
