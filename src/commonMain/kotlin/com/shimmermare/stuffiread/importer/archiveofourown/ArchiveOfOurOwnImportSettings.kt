package com.shimmermare.stuffiread.importer.archiveofourown

import com.shimmermare.stuffiread.importer.ImportSettings

data class ArchiveOfOurOwnImportSettings(
    val workId: WorkId,
    val fileTypes: Set<FileType>,
) : ImportSettings {
    enum class FileType {
        EPUB,
        PDF,
        HTML;

        companion object {
            val values = values().toSet()
        }
    }
}
