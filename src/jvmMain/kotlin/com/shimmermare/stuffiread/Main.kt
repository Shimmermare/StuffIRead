package com.shimmermare.stuffiread

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.shimmermare.stuffiread.ui.App
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import java.awt.Dimension
import java.nio.file.Path
import javax.swing.UIManager

fun main(args: Array<String>) {
    Napier.base(DebugAntilog())

    val storiesFile = args.firstOrNull()?.let { Path.of(it) }

    Napier.i("Started app with args: storiesFile=$storiesFile")

    // Some swing elements are unfortunately used, e.g. JFileChooser
    // Replace with native Compose ones when available
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    application {
        val onCloseRequest = {
            Napier.i("Invoked close application request")
            exitApplication()
        }
        Window(
            title = "Stuff I Read",
            onCloseRequest = onCloseRequest
        ) {
            window.minimumSize = Dimension(1280, 800)
            App(storiesFile)
        }
    }
}