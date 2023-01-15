package com.shimmermare.stuffiread.ui.util

import java.nio.file.Path
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter
import javax.swing.filechooser.FileSystemView

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
    fileFilter: FileFilter? = null
): Path? {
    val fileChooser = JFileChooser(FileSystemView.getFileSystemView())
    fileChooser.currentDirectory = currentDirectory?.toFile()
    fileChooser.selectedFile = selectedFile?.toFile()
    fileChooser.dialogTitle = title
    fileChooser.fileSelectionMode = fileSelectionMode
    fileChooser.isAcceptAllFileFilterUsed = isAcceptAllFileFilterUsed
    fileChooser.fileFilter = fileFilter

    return if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile?.toPath()
    } else {
        null
    }
}