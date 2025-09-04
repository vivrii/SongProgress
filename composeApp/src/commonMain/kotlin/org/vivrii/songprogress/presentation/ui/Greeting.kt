package org.vivrii.songprogress.presentation.ui

import org.vivrii.songprogress.getPlatform

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}