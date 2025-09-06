package org.vivrii.songprogress

actual fun loadResourceFile(path: String): String {
    val stream = Thread.currentThread().contextClassLoader
        .getResourceAsStream(path)
        ?: error("Resource $path not found")
    return stream.bufferedReader().use { it.readText() }
}
