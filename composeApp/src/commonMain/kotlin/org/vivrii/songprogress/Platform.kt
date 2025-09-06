package org.vivrii.songprogress

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
