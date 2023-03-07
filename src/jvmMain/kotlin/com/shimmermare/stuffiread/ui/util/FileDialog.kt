package com.shimmermare.stuffiread.ui.util

import io.github.aakira.napier.Napier
import java.io.File
import java.nio.file.Path
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView
import kotlin.io.path.extension
import javax.swing.filechooser.FileFilter as SwingFileFilter

/**
 * JVM implementation using Swing [JFileChooser].
 */
actual object FileDialog {
    actual fun showOpenDialog(
        title: String?,
        currentDirectory: Path?,
        selectedFile: Path?,
        selectionMode: SelectionMode,
        fileFilter: FileFilter?
    ): Path? {
        val fileChooser = createFileChooser(title, currentDirectory, selectedFile, selectionMode, fileFilter)

        return if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            fileChooser.selectedFile.toPathOrNull()
        } else {
            null
        }
    }

    /**
     * [defaultExtension] - extension will be added to resulting file if none is present.
     *     E.g. selected "./file", default ext is "txt" - dialog will return "./file.txt".
     */
    actual fun showSaveDialog(
        title: String?,
        currentDirectory: Path?,
        selectedFile: Path?,
        selectionMode: SelectionMode,
        fileFilter: FileFilter?,
        defaultExtension: String?
    ): Path? {
        val fileChooser = createFileChooser(title, currentDirectory, selectedFile, selectionMode, fileFilter)

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            val result = fileChooser.selectedFile.toPathOrNull() ?: return null
            return if (defaultExtension != null && result.extension != defaultExtension) {
                result.resolveSibling("${result.fileName}.$defaultExtension")
            } else {
                result
            }
        }
        return null
    }

    private fun createFileChooser(
        title: String?,
        currentDirectory: Path?,
        selectedFile: Path?,
        selectionMode: SelectionMode,
        fileFilter: FileFilter?,
    ): JFileChooser {
        return JFileChooser(FileSystemView.getFileSystemView()).apply {
            this.dialogTitle = title
            this.currentDirectory = currentDirectory?.toFile()
            this.selectedFile = selectedFile?.toFile()
            this.fileSelectionMode = selectionMode.toSwing()
            this.isAcceptAllFileFilterUsed = fileFilter == null
            this.fileFilter = fileFilter?.let { FileFilterWrapper(it) }
        }
    }

    private fun SelectionMode.toSwing(): Int {
        return when (this) {
            SelectionMode.FILES_ONLY -> JFileChooser.FILES_ONLY
            SelectionMode.DIRECTORIES_ONLY -> JFileChooser.DIRECTORIES_ONLY
            SelectionMode.FILES_AND_DIRECTORIES -> JFileChooser.FILES_AND_DIRECTORIES
        }
    }

    private fun File?.toPathOrNull(): Path? {
        return try {
            this?.toPath()
        } catch (e: Exception) {
            Napier.e("Failed to convert file to path", e)
            null
        }
    }

    private class FileFilterWrapper(
        private val filter: FileFilter
    ) : SwingFileFilter() {
        override fun accept(f: File): Boolean {
            return try {
                filter.filter(f.toPath())
            } catch (e: Exception) {
                // Some File(s) can't be represented as Path(s), e.g. Windows virtual folders ('This PC' and so on)
                false
            }
        }

        override fun getDescription(): String? {
            return filter.description
        }
    }
}