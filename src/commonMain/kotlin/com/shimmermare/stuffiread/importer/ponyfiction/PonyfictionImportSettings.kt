package com.shimmermare.stuffiread.importer.ponyfiction

import com.shimmermare.stuffiread.importer.ImportSettings

data class PonyfictionImportSettings(
    val storyId: PonyfictionStoryId,
    val downloadFb2: Boolean,
    val downloadTxt: Boolean,
) : ImportSettings {
}