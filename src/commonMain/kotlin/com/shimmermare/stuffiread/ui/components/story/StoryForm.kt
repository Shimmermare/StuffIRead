package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.Score
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryAuthor
import com.shimmermare.stuffiread.stories.StoryDescription
import com.shimmermare.stuffiread.stories.StoryName
import com.shimmermare.stuffiread.stories.StoryReview
import com.shimmermare.stuffiread.stories.StoryURL
import com.shimmermare.stuffiread.stories.cover.StoryCover
import com.shimmermare.stuffiread.stories.cover.StoryCoverFormat
import com.shimmermare.stuffiread.stories.file.StoryFile
import com.shimmermare.stuffiread.ui.components.form.FormField
import com.shimmermare.stuffiread.ui.components.form.InputFormState
import com.shimmermare.stuffiread.ui.components.form.OptionalFormField
import com.shimmermare.stuffiread.ui.components.form.OptionalInstantFormField
import com.shimmermare.stuffiread.ui.components.form.SubmittableInputForm
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.form.UIntFormField
import com.shimmermare.stuffiread.ui.components.form.ValidationResult
import com.shimmermare.stuffiread.ui.components.layout.VerticalScrollColumn
import com.shimmermare.stuffiread.ui.components.tag.MultiTagSelector
import com.shimmermare.stuffiread.ui.storyCoverService
import com.shimmermare.stuffiread.ui.storyFilesService
import com.shimmermare.stuffiread.ui.storyService
import com.shimmermare.stuffiread.ui.util.ExtensionFileFilter
import com.shimmermare.stuffiread.ui.util.FileDialog
import com.shimmermare.stuffiread.ui.util.SelectionMode
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

/**
 * Wrapper over [StoryForm] that handles story saving. Works in creation and edit mode.
 */
@Composable
fun SavingStoryForm(
    prefillData: StoryFormData,
    onSubmittedAndSaved: (StoryFormData) -> Unit,
    onBack: () -> Unit,
    creationMode: Boolean,
) {
    val storyService = storyService
    val storyCoverService = storyCoverService
    val storyFilesService = storyFilesService

    val coroutineScope = rememberCoroutineScope()

    StoryForm(
        prefillData = prefillData,
        onSubmit = { formData ->
            coroutineScope.launch {
                val saved = if (creationMode) {
                    storyService.createStory(formData.story)
                } else {
                    storyService.updateStory(formData.story)
                }

                storyCoverService.updateStoryCover(saved.id, formData.cover)
                storyFilesService.updateStoryFiles(saved.id, formData.files)
                onSubmittedAndSaved(formData.copy(story = saved))
            }
        },
        onBack = onBack,
        submitButtonText = if (creationMode) "Create" else "Save",
        canSubmitWithoutChanges = creationMode
    )
}

@Composable
fun StoryForm(
    prefillData: StoryFormData,
    onSubmit: (StoryFormData) -> Unit,
    onBack: () -> Unit,
    submitButtonText: String = "Submit",
    canSubmitWithoutChanges: Boolean = false,
) {
    VerticalScrollColumn {
        Row {
            Box(
                modifier = Modifier.width(1000.dp)
            ) {
                FormContainer(
                    prefillData = prefillData,
                    onSubmit = onSubmit,
                    onBack = onBack,
                    submitButtonText = submitButtonText,
                    canSubmitWithoutChanges = canSubmitWithoutChanges,
                )
            }
            Spacer(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun FormContainer(
    prefillData: StoryFormData,
    onSubmit: (StoryFormData) -> Unit,
    onBack: () -> Unit,
    submitButtonText: String,
    canSubmitWithoutChanges: Boolean,
) {
    SubmittableInputForm(
        data = prefillData,
        submitButtonText = submitButtonText,
        canSubmitWithoutChanges = canSubmitWithoutChanges,
        onSubmit = onSubmit,
        actions = {
            Button(onClick = onBack) {
                Text("Back")
            }
        }
    ) { state ->
        StoryCoverFormField(state)
        TextFormField(
            id = "author",
            state = state,
            name = "Author",
            description = "Leave empty if author is not known",
            getter = { it.story.author.value ?: "" },
            setter = { data, value -> data.copy(story = data.story.copy(author = StoryAuthor.of(value))) },
            validator = { ValidationResult.fromException { StoryAuthor.of(it) } },
            maxLength = StoryAuthor.MAX_LENGTH
        )
        TextFormField(
            id = "name",
            state = state,
            name = "Name",
            getter = { it.story.name.value },
            setter = { data, value -> data.copy(story = data.story.copy(name = StoryName(value))) },
            validator = { ValidationResult.fromException { StoryName(it) } },
            maxLength = StoryName.MAX_LENGTH
        )
        TextFormField(
            id = "url",
            state = state,
            name = "URL",
            getter = { it.story.url.toString() },
            setter = { data, value -> data.copy(story = data.story.copy(url = StoryURL.of(value))) },
            validator = { ValidationResult.fromException { StoryURL.of(it) } },
            maxLength = StoryURL.MAX_LENGTH,
        )
        TextFormField(
            id = "description",
            state = state,
            name = "Description",
            getter = { it.story.description.toString() },
            setter = { data, value -> data.copy(story = data.story.copy(description = StoryDescription.of(value))) },
            validator = { ValidationResult.fromException { StoryDescription.of(it) } },
            singleLine = false,
            maxLength = StoryDescription.MAX_LENGTH,
            textInputModifier = Modifier.fillMaxWidth().sizeIn(minHeight = 108.dp, maxHeight = 420.dp),
        )
        OptionalInstantFormField(
            id = "published",
            state = state,
            name = "First published date",
            description = "Date when the story was initially published by author",
            getter = { it.story.published },
            setter = { data, value ->
                // If published is set to after changed - set changed to same as published
                val changed = if (value != null && data.story.changed != null && value > data.story.changed) {
                    value
                } else {
                    data.story.changed
                }
                data.copy(story = data.story.copy(published = value, changed = changed))
            },
        )
        OptionalInstantFormField(
            id = "changed",
            state = state,
            name = "Last change date",
            description = "Date when the story was modified by author last time",
            getter = { it.story.changed },
            setter = { data, value ->
                // If changed is set to before published - set published to same as changed
                val published = if (value != null && data.story.published != null && value < data.story.published) {
                    value
                } else {
                    data.story.published
                }
                data.copy(story = data.story.copy(published = published, changed = value))
            },
        )
        FormField(
            id = "tags",
            state = state,
            name = "Tags",
            getter = { it.story.tags },
            setter = { data, value -> data.copy(story = data.story.copy(tags = value)) },
        ) { value, _, onValueChange ->
            MultiTagSelector(
                selectedIds = value,
                onSelect = onValueChange
            )
        }
        FormField(
            id = "sequels",
            state = state,
            name = "Sequels",
            getter = { it.story.sequels },
            setter = { data, value -> data.copy(story = data.story.copy(sequels = value)) }
        ) { value, _, onValueChange ->
            MultiStorySelector(
                selectedIds = value,
                filter = { it.id != state.data.story.id },
                onSelect = onValueChange
            )
        }
        OptionalFormField(
            id = "score",
            state = state,
            name = "Score",
            defaultValue = { Score(0F) },
            getter = { it.story.score },
            setter = { data, value -> data.copy(story = data.story.copy(score = value)) },
        ) { value, _, onValueChange ->
            StoryScoreInput(value, onValueChange)
        }
        TextFormField(
            id = "review",
            state = state,
            name = "Review",
            getter = { it.story.review.toString() },
            setter = { data, value -> data.copy(story = data.story.copy(review = StoryReview.of(value))) },
            validator = { ValidationResult.fromException { StoryReview.of(it) } },
            singleLine = false,
            maxLength = StoryReview.MAX_LENGTH,
            textInputModifier = Modifier.fillMaxWidth().sizeIn(minHeight = 108.dp, maxHeight = 420.dp),
        )
        OptionalInstantFormField(
            id = "firstRead",
            state = state,
            name = "First read date",
            description = "Date when you read story the first time",
            getter = { it.story.firstRead },
            setter = { data, value -> data.copy(story = data.story.copy(firstRead = value)) },
        )
        OptionalInstantFormField(
            id = "lastRead",
            state = state,
            name = "Last read date",
            description = "Date when you read story the last time",
            getter = { it.story.lastRead },
            setter = { data, value -> data.copy(story = data.story.copy(lastRead = value)) },
        )
        UIntFormField(
            id = "timesRead",
            state = state,
            name = "Times read",
            getter = { it.story.timesRead },
            setter = { data, value -> data.copy(story = data.story.copy(timesRead = value)) },
            inputModifier = Modifier.width(100.dp).height(36.dp),
        )
        FormField(
            id = "files",
            state = state,
            name = "Files",
            description = "Files with story content.",
            getter = { it.files },
            setter = { data, value -> data.copy(files = value) }
        ) { value, _, onValueChange ->
            StoryFileListInput(value, onValueChange)
        }
    }
}

@Composable
private fun StoryCoverFormField(
    state: InputFormState<StoryFormData>,
) {
    val storyCoverService = storyCoverService
    FormField(
        id = "cover",
        state = state,
        name = "Cover",
        description = "Story cover image in one of listed formats: BMP, GIF, HEIF, ICO, JPEG, PNG, WBMP, WebP",
        getter = { it.cover },
        setter = { data, value -> data.copy(cover = value) }
    ) { value, _, onValueChange ->
        val coroutineScope = rememberCoroutineScope()
        var image: Painter? by remember { mutableStateOf(null) }

        LaunchedEffect(value) {
            image = if (value == null) {
                null
            } else {
                BitmapPainter(loadImageBitmap(value.data.inputStream()))
            }
        }

        Column {
            image?.let { image ->
                Image(
                    painter = image,
                    contentDescription = "Story cover",
                    modifier = Modifier.height(300.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (image != null) {
                    Button(
                        onClick = { onValueChange(null) }
                    ) {
                        Text("Clear")
                    }
                }
                Button(
                    onClick = {
                        val path = FileDialog.showOpenDialog(
                            title = "Select cover image",
                            selectionMode = SelectionMode.FILES_ONLY,
                            fileFilter = ExtensionFileFilter(
                                description = "Supported cover image formats (${
                                    StoryCoverFormat.values().joinToString(", ")
                                })",
                                extensions = StoryCoverFormat.ALL_EXTENSIONS.toTypedArray()
                            )
                        )
                        if (path != null) {
                            coroutineScope.launch {
                                try {
                                    val loaded = storyCoverService.loadCoverFile(path)
                                    onValueChange(loaded)
                                } catch (e: Exception) {
                                    Napier.e(e) { "Failed to load cover file" }
                                }
                            }
                        }
                    }
                ) {
                    Text("Select file")
                }
            }
        }
    }
}

data class StoryFormData(
    val story: Story,
    val cover: StoryCover? = null,
    val files: List<StoryFile> = emptyList()
)