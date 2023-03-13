package com.shimmermare.stuffiread

import com.shimmermare.stuffiread.importer.TagMappingRepositoryImpl
import com.shimmermare.stuffiread.importer.TagMappingService
import com.shimmermare.stuffiread.importer.TagMappingServiceImpl
import com.shimmermare.stuffiread.stories.CachedStoryService
import com.shimmermare.stuffiread.stories.FileBasedStoryService
import com.shimmermare.stuffiread.stories.StorySearchService
import com.shimmermare.stuffiread.stories.StorySearchServiceImpl
import com.shimmermare.stuffiread.stories.StoryService
import com.shimmermare.stuffiread.stories.cover.StoryCoverService
import com.shimmermare.stuffiread.stories.cover.StoryCoverServiceImpl
import com.shimmermare.stuffiread.stories.file.StoryFilesService
import com.shimmermare.stuffiread.stories.file.StoryFilesServiceImpl
import com.shimmermare.stuffiread.tags.FileBasedTagTreeService
import com.shimmermare.stuffiread.tags.TagService
import com.shimmermare.stuffiread.tags.TagServiceImpl
import io.github.aakira.napier.Napier
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists

/**
 * @param directory - archive directory. Will be created if not exists.
 */
class StoryArchive(val directory: Path) {

    val storyService: StoryService
    val storyCoverService: StoryCoverService
    val storyFilesService: StoryFilesService

    val tagService: TagService
    val tagMappingService: TagMappingService

    val storySearchService: StorySearchService

    init {
        Napier.i { "Opening $directory as story archive" }

        if (directory.notExists()) directory.createDirectories()

        storyService = CachedStoryService(FileBasedStoryService(directory))
        storyCoverService = StoryCoverServiceImpl(directory)
        storyFilesService = StoryFilesServiceImpl(directory)

        tagService = TagServiceImpl(FileBasedTagTreeService(directory))
        tagMappingService = TagMappingServiceImpl(TagMappingRepositoryImpl(directory), tagService)

        storySearchService = StorySearchServiceImpl(storyService, storyFilesService, tagService)
    }
}