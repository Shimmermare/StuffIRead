package com.shimmermare.stuffiread.importer.archiveofourown

import com.shimmermare.stuffiread.importer.ImportedStory
import com.shimmermare.stuffiread.importer.StoryImporter

expect object ArchiveOfOurOwnImporter : StoryImporter<ArchiveOfOurOwnImportSettings> {
    override suspend fun import(settings: ArchiveOfOurOwnImportSettings): ImportedStory
}