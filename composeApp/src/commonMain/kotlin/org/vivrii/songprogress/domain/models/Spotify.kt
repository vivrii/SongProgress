package org.vivrii.songprogress.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpotifyArtist(
    val id: String,
    val name: String,
)

@Serializable
data class SpotifyAlbum(
    val id: String,
    val name: String,
    val type: String,
)

@Serializable
data class SpotifyTrack(
    val id: String,
    val name: String,
    val type: String,
    @SerialName("track_number") val trackNumber: Int,
)
