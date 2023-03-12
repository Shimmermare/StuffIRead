package com.shimmermare.stuffiread.util

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.relativeTo


object FileUtils {
    fun copyFolderRecursiveFromClasspath(from: String, to: Path) {
        val fromResource = FileUtils::class.java.classLoader.getResource(from)
            ?: throw IllegalArgumentException("Resource '$from' doesn't exist")

        // If running from IDE resource will be a regular file, not classpath
        if (fromResource.protocol == "file") {
            copyFolderRecursive(Path.of(fromResource.toURI()), to)
        } else {
            FileSystems.newFileSystem(
                fromResource.toURI(),
                emptyMap<String, Any>(),
                FileUtils::class.java.classLoader
            ).use { fs ->
                copyFolderRecursive(fs.getPath(from), to)
            }
        }
    }

    private fun copyFolderRecursive(from: Path, to: Path) {
        Files.walk(from).forEach { path ->
            val relativePath = path.relativeTo(from)
            val target = to.resolve(relativePath.toString())
            Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING)
        }
    }
}