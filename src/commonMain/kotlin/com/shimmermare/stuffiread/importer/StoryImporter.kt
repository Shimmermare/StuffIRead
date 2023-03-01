package com.shimmermare.stuffiread.importer

interface StoryImporter<Settings : ImportSettings> {
    suspend fun import(settings: Settings): ImportedStory
}