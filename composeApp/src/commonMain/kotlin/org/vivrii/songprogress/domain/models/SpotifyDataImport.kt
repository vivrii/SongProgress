package org.vivrii.songprogress.domain.models

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class SpotifyDataImport(
    var start: Instant? = null,
    var end: Instant? = null,
) {
    val artists = HashMap<String, Artist>()
}
