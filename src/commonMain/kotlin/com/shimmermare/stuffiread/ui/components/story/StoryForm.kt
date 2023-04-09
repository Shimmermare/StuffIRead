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
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.stories.Score
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryAuthor
import com.shimmermare.stuffiread.stories.StoryDescription
import com.shimmermare.stuffiread.stories.StoryId
import com.shimmermare.stuffiread.stories.StoryName
import com.shimmermare.stuffiread.stories.StoryRead
import com.shimmermare.stuffiread.stories.StoryReview
import com.shimmermare.stuffiread.stories.StoryURL
import com.shimmermare.stuffiread.stories.cover.StoryCover
import com.shimmermare.stuffiread.stories.cover.StoryCoverFormat
import com.shimmermare.stuffiread.stories.file.StoryFile
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyCoverService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyFilesService
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.storyService
import com.shimmermare.stuffiread.ui.components.date.DateWithLabel
import com.shimmermare.stuffiread.ui.components.form.BackFormButton
import com.shimmermare.stuffiread.ui.components.form.FormField
import com.shimmermare.stuffiread.ui.components.form.InputFormState
import com.shimmermare.stuffiread.ui.components.form.OptionalFormField
import com.shimmermare.stuffiread.ui.components.form.OptionalInstantFormField
import com.shimmermare.stuffiread.ui.components.form.SubmittableInputForm
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.form.ValidationResult
import com.shimmermare.stuffiread.ui.components.input.SizedIconButton
import com.shimmermare.stuffiread.ui.components.input.datetime.DateTimePicker
import com.shimmermare.stuffiread.ui.components.layout.VerticalScrollColumn
import com.shimmermare.stuffiread.ui.components.tag.MultiTagPicker
import com.shimmermare.stuffiread.ui.util.ExtensionFileFilter
import com.shimmermare.stuffiread.ui.util.FileDialog
import com.shimmermare.stuffiread.ui.util.SelectionMode
import com.shimmermare.stuffiread.ui.util.TimeUtils
import com.shimmermare.stuffiread.ui.util.remember
import com.shimmermare.stuffiread.util.i18n.PluralLocalizedString
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

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

                if (formData.originalPrequels != formData.prequels) {
                    val noLongerPrequel = formData.originalPrequels - formData.prequels
                    noLongerPrequel.forEach {
                        val story = storyService.getStoryByIdOrThrow(it)
                        storyService.updateStory(story.copy(sequels = story.sequels - saved.id))
                    }

                    val newPrequel = formData.prequels - formData.originalPrequels
                    newPrequel.forEach {
                        val story = storyService.getStoryByIdOrThrow(it)
                        storyService.updateStory(story.copy(sequels = story.sequels + saved.id))
                    }
                }

                storyCoverService.updateStoryCover(saved.id, formData.cover)
                storyFilesService.updateStoryFiles(saved.id, formData.files)
                onSubmittedAndSaved(formData.copy(story = saved))
            }
        },
        onBack = onBack,
        submitButtonText = if (creationMode) {
            Strings.components_storyForm_submitButton_create.remember()
        } else {
            Strings.components_storyForm_submitButton_edit.remember()
        },
        canSubmitWithoutChanges = creationMode
    )
}

@Composable
fun StoryForm(
    prefillData: StoryFormData,
    onSubmit: (StoryFormData) -> Unit,
    onBack: () -> Unit,
    submitButtonText: String,
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
            BackFormButton(onBack)
        }
    ) { state ->
        StoryCoverFormField(state)
        TextFormField(
            id = "author",
            state = state,
            name = Strings.components_storyForm_author.remember(),
            description = Strings.components_storyForm_author_description.remember(),
            getter = { it.story.author.value ?: "" },
            setter = { data, value -> data.copy(story = data.story.copy(author = StoryAuthor.of(value))) },
            validator = { ValidationResult.fromException { StoryAuthor.of(it) } },
            maxLength = StoryAuthor.MAX_LENGTH
        )
        TextFormField(
            id = "name",
            state = state,
            name = Strings.components_storyForm_name.remember(),
            getter = { it.story.name.value },
            setter = { data, value -> data.copy(story = data.story.copy(name = StoryName(value))) },
            validator = { ValidationResult.fromException { StoryName(it) } },
            maxLength = StoryName.MAX_LENGTH
        )
        TextFormField(
            id = "url",
            state = state,
            name = Strings.components_storyForm_url.remember(),
            getter = { it.story.url.toString() },
            setter = { data, value -> data.copy(story = data.story.copy(url = StoryURL.of(value))) },
            validator = { ValidationResult.fromException { StoryURL.of(it) } },
            maxLength = StoryURL.MAX_LENGTH,
        )
        TextFormField(
            id = "description",
            state = state,
            name = Strings.components_storyForm_description.remember(),
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
            name = Strings.components_storyForm_published.remember(),
            description = Strings.components_storyForm_published_description.remember(),
            defaultValue = { TimeUtils.instantTodayAt1200() },
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
            name = Strings.components_storyForm_changed.remember(),
            description = Strings.components_storyForm_changed_description.remember(),
            defaultValue = { state.data.story.published ?: TimeUtils.instantTodayAt1200() },
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
            name = Strings.components_storyForm_tags.remember(),
            getter = { it.story.tags },
            setter = { data, value -> data.copy(story = data.story.copy(tags = value)) },
        ) { value, _, onValueChange ->
            MultiTagPicker(
                title = Strings.components_storyForm_tags_pickerTitle.remember(),
                pickedTagIds = value,
                onPick = onValueChange
            )
        }
        FormField(
            id = "sequels",
            state = state,
            name = Strings.components_storyForm_sequels.remember(),
            getter = { it.story.sequels },
            setter = { data, value -> data.copy(story = data.story.copy(sequels = value)) }
        ) { value, _, onValueChange ->
            MultiStoryPicker(
                selectedIds = value,
                filter = { it.id != state.data.story.id },
                onSelect = onValueChange
            )
        }
        FormField(
            id = "prequels",
            state = state,
            name = Strings.components_storyForm_prequels.remember(),
            getter = { it.prequels },
            setter = { data, value -> data.copy(prequels = value) }
        ) { value, _, onValueChange ->
            MultiStoryPicker(
                selectedIds = value,
                filter = { it.id != state.data.story.id },
                onSelect = onValueChange
            )
        }
        OptionalFormField(
            id = "score",
            state = state,
            name = Strings.components_storyForm_score.remember(),
            defaultValue = { Score(0F) },
            getter = { it.story.score },
            setter = { data, value -> data.copy(story = data.story.copy(score = value)) },
        ) { value, _, onValueChange ->
            StoryScoreInput(value, onValueChange)
        }
        TextFormField(
            id = "review",
            state = state,
            name = Strings.components_storyForm_review.remember(),
            getter = { it.story.review.toString() },
            setter = { data, value -> data.copy(story = data.story.copy(review = StoryReview.of(value))) },
            validator = { ValidationResult.fromException { StoryReview.of(it) } },
            singleLine = false,
            maxLength = StoryReview.MAX_LENGTH,
            textInputModifier = Modifier.fillMaxWidth().sizeIn(minHeight = 108.dp, maxHeight = 420.dp),
        )
        ReadsFormField(state)
        FormField(
            id = "files",
            state = state,
            name = Strings.components_storyForm_files.remember(),
            description = Strings.components_storyForm_files_description.remember(),
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
    FormField(
        id = "cover",
        state = state,
        name = Strings.components_storyForm_cover.remember(),
        description = Strings.components_storyForm_cover_description.remember(
            StoryCoverFormat.VALUES.joinToString(", ")
        ),
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
                    contentDescription = Strings.components_storyForm_cover_contentDescription.remember(),
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
                        Text(Strings.components_storyForm_cover_clearButton.remember())
                    }
                }
                Button(
                    onClick = {
                        val path = FileDialog.showOpenDialog(
                            title = Strings.components_storyForm_cover_picker_title(),
                            selectionMode = SelectionMode.FILES_ONLY,
                            fileFilter = ExtensionFileFilter(
                                description = Strings.components_storyForm_cover_picker_filterDescription(
                                    StoryCoverFormat.ALL_EXTENSIONS.joinToString(", ")
                                ),
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
                    Text(Strings.components_storyForm_cover_selectButton.remember())
                }
            }
        }
    }
}

@Composable
private fun ReadsFormField(
    state: InputFormState<StoryFormData>,
) {
    FormField(
        id = "reads",
        state = state,
        name = Strings.components_storyForm_reads.remember(),
        description = Strings.components_storyForm_reads_description.remember(),
        getter = { it.story.reads },
        setter = { data, value -> data.copy(story = data.story.copy(reads = value.sorted())) },
    ) { value, _, onValueChange ->
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (value.isNotEmpty()) {
                Text(components_storyForm_reads_count.remember(value.size))
                value.forEachIndexed { index, read ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DateWithLabel("#${index + 1}:", read.date)
                        SizedIconButton(onClick = { onValueChange(value - read) }, size = 30.dp) {
                            Icon(Icons.Filled.Clear, null)
                        }
                    }
                }
            } else {
                Text(Strings.components_storyForm_reads_noReads.remember())
            }

            var showAdd: Boolean by remember { mutableStateOf(false) }

            if (showAdd) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    var dateToAdd: LocalDateTime by remember { mutableStateOf(TimeUtils.todayAt1200()) }
                    Text(Strings.components_storyForm_reads_addNew_label.remember())
                    DateTimePicker(
                        value = dateToAdd,
                        onValueChange = { dateToAdd = it }
                    )
                    Button(
                        onClick = {
                            val newRead = StoryRead(dateToAdd.toInstant(TimeZone.currentSystemDefault()))
                            onValueChange(value + newRead)
                            showAdd = false
                        }
                    ) {
                        Text(Strings.components_storyForm_reads_addNew_confirmButton.remember())
                    }
                }
            } else {
                Button(
                    onClick = { showAdd = true }
                ) {
                    Text(Strings.components_storyForm_reads_addNew_button.remember())
                }
            }
        }
    }
}

data class StoryFormData(
    val story: Story,
    val originalPrequels: Set<StoryId> = emptySet(),
    val prequels: Set<StoryId> = originalPrequels,
    val cover: StoryCover? = null,
    val files: List<StoryFile> = emptyList()
)

private val components_storyForm_reads_count = PluralLocalizedString(
    Strings.components_storyForm_reads_count_other,
    Strings.components_storyForm_reads_count_one,
    Strings.components_storyForm_reads_count_two,
    Strings.components_storyForm_reads_count_few,
    Strings.components_storyForm_reads_count_many,
    Strings.components_storyForm_reads_count_other,
)