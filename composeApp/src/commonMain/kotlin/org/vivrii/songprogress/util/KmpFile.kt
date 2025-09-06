package org.vivrii.songprogress.util

expect class KmpFile(path: String) {
    companion object {
        val cacheDir: KmpFile
    }
    val path: String
    fun exists(): Boolean
    fun create()
    fun readText(): String
    fun writeText(content: String, append: Boolean = false)
    operator fun div(path: String): KmpFile
    operator fun div(other: KmpFile): KmpFile
    override fun toString(): String
}
