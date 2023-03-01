package com.shimmermare.stuffiread.ui.util

import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isDirectory

/**
 * Platform-independent file dialog.
 */
expect object FileDialog {
    fun showOpenDialog(
        title: String? = null,
        currentDirectory: Path? = null,
        selectedFile: Path? = null,
        selectionMode: SelectionMode = SelectionMode.FILES_ONLY,
        fileFilter: FileFilter? = null,
    ): Path?

    /**
     * [defaultExtension] - extension will be added to resulting file if none is present.
     *     E.g. selected "./file", default ext is "txt" - dialog will return "./file.txt".
     */
    fun showSaveDialog(
        title: String? = null,
        currentDirectory: Path? = null,
        selectedFile: Path? = null,
        selectionMode: SelectionMode = SelectionMode.FILES_ONLY,
        fileFilter: FileFilter? = null,
        defaultExtension: String? = null,
    ): Path?
}

enum class SelectionMode {
    FILES_ONLY,
    DIRECTORIES_ONLY,
    FILES_AND_DIRECTORIES,
}

abstract class FileFilter(
    val description: String? = null
) {
    abstract fun filter(path: Path): Boolean
}

class DirectoriesOnlyFileFilter(description: String? = null) : FileFilter(description) {
    override fun filter(path: Path): Boolean {
        return path.isDirectory()
    }
}

class ExtensionFileFilter(description: String? = null, vararg extensions: String) : FileFilter(description) {
    private val extensions = extensions.toSet()

    override fun filter(path: Path): Boolean {
        return path.isDirectory() || extensions.contains(path.extension.lowercase())
    }
}