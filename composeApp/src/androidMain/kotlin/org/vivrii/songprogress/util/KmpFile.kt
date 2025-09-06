package org.vivrii.songprogress.util

import android.content.Context
import android.os.Build
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.appendText
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

actual class KmpFile actual constructor(actual val path: String) {
    actual companion object {
        private var cacheDir: String? = null

        fun init(ctx: Context) {
            cacheDir = ctx.cacheDir.absolutePath
        }

        actual fun cacheDir(): KmpFile = KmpFile(cacheDir ?: error("cacheDir missing. must first initialise via KmpFile.init(Context)"))
    }

    private val pathObj = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Path(path).normalize()
    } else {
        Path(path)
    }

    actual fun exists(): Boolean = pathObj.exists()

    actual fun create() {
        pathObj.createParentDirectories()
    }

    actual fun readText(): String = pathObj.readText()

    actual fun writeText(content: String, append: Boolean) {
        if (append) {
            pathObj.appendText(content)
        } else {
            pathObj.writeText(content)
        }
    }

    actual operator fun div(path: String): KmpFile {
        return KmpFile(Path(this.path, path).absolutePathString())
    }

    actual operator fun div(other: KmpFile): KmpFile {
        return KmpFile(Path(this.path, other.path).absolutePathString())
    }

    actual override fun toString(): String {
        return pathObj.absolutePathString()
    }
}
