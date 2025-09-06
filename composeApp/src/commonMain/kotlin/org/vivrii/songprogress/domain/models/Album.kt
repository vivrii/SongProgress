package org.vivrii.songprogress.domain.models

data class Album(
    val title: String
) {
    val tracks = HashMap<String, Track>()
}
