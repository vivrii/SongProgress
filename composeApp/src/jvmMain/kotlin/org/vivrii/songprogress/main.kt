package org.vivrii.songprogress

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.vivrii.songprogress.presentation.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SongProgress",
    ) {
        App()
    }
}