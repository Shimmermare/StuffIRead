package com.shimmermare.stuffiread.ui.util

import java.nio.file.Path
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter
import javax.swing.filechooser.FileSystemView
import kotlin.io.path.extension

fun showOpenDialog(
    title: String? = null,
    currentDirectory: Path? = null,
    selectedFile: Path? = null,
    fileSelectionMode: Int = JFileChooser.FILES_ONLY,
    isAcceptAllFileFilterUsed: Boolean = false,
    fileFilter: FileFilter? = null
): Path? {
    val fileChooser = JFileChooser(FileSystemView.getFileSystemView())
    fileChooser.currentDirectory = currentDirectory?.toFile()
    fileChooser.selectedFile = selectedFile?.toFile()
    fileChooser.dialogTitle = title
    fileChooser.fileSelectionMode = fileSelectionMode
    fileChooser.isAcceptAllFileFilterUsed = isAcceptAllFileFilterUsed
    fileChooser.fileFilter = fileFilter

    return if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile?.toPath()
    } else {
        null
    }
}

fun showSaveDialog(
    title: String? = null,
    currentDirectory: Path? = null,
    selectedFile: Path? = null,
    fileSelectionMode: Int = JFileChooser.FILES_ONLY,
    isAcceptAllFileFilterUsed: Boolean = false,
    fileFilter: FileFilter? = null,
    extension: String? = null
): Path? {
    val fileChooser = JFileChooser(FileSystemView.getFileSystemView())
    fileChooser.currentDirectory = currentDirectory?.toFile()
    fileChooser.selectedFile = selectedFile?.toFile()
    fileChooser.dialogTitle = title
    fileChooser.fileSelectionMode = fileSelectionMode
    fileChooser.isAcceptAllFileFilterUsed = isAcceptAllFileFilterUsed
    fileChooser.fileFilter = fileFilter

    if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
        val result = fileChooser.selectedFile?.toPath() ?: return null
        return if (result.extension != extension && extension != null) {
            result.resolveSibling("${result.fileName}.$extension")
        } else {
            result
        }
    }

    return null
}