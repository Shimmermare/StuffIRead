package com.shimmermare.stuffiread

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.shimmermare.stuffiread.ui.App
import java.awt.Dimension
import java.nio.file.Path
import javax.swing.UIManager

fun main(args: Array<String>) {
    val storiesFile = args.firstOrNull()?.let { Path.of(it) }

    // Some swing elements are unfortunately used, e.g. JFileChooser
    // Replace with native Compose ones when available
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    application {
        Window(onCloseRequest = ::exitApplication) {
            window.minimumSize = Dimension(800, 600)
            App(storiesFile)
        }
    }
}