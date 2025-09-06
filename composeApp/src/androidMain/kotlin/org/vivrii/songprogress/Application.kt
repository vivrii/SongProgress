package org.vivrii.songprogress

import android.app.Application
import org.vivrii.songprogress.util.KmpFile

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        KmpFile.init(this)
    }
}
