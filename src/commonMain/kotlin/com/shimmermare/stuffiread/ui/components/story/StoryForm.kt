package com.shimmermare.stuffiread.ui.components.story

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.stories.Score
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryAuthor
import com.shimmermare.stuffiread.stories.StoryDescription
import com.shimmermare.stuffiread.stories.StoryName
import com.shimmermare.stuffiread.stories.StoryReview
import com.shimmermare.stuffiread.stories.StoryURL
import com.shimmermare.stuffiread.stories.file.StoryFile
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.form.FormField
import com.shimmermare.stuffiread.ui.components.form.IntFormField
import com.shimmermare.stuffiread.ui.components.form.OptionalFormField
import com.shimmermare.stuffiread.ui.components.form.OptionalInstantFormField
import com.shimmermare.stuffiread.ui.components.form.SubmittableInputForm
import com.shimmermare.stuffiread.ui.components.form.TextFormField
import com.shimmermare.stuffiread.ui.components.form.ValidationResult
import com.shimmermare.stuffiread.ui.components.tag.MultiTagSelector

@Composable
fun StoryForm(
    app: AppState,
    prefillData: StoryFormData,
    onSubmit: (StoryFormData) -> Unit,
    onBack: () -> Unit,
    submitButtonText: String = "Submit",
    canSubmitWithoutChanges: Boolean = false,
) {
    val scrollState = rememberScrollState()
    Box {
        Column(
            modifier = Modifier.verticalScroll(scrollState).width(1000.dp).padding(end = 12.dp)
        ) {
            FormContainer(
                app = app,
                prefillData = prefillData,
                onSubmit = onSubmit,
                onBack = onBack,
                submitButtonText = submitButtonText,
                canSubmitWithoutChanges = canSubmitWithoutChanges,
            )
        }
        Spacer(modifier = Modifier.fillMaxWidth())
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }
}

@Composable
private fun FormContainer(
    app: AppState,
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
                tagService = app.storyArchive!!.tagService,
                selectedIds = value,
                onSelect = onValueChange
            )
        }
        // TODO: Story picker sequels
        Text("TODO: Sequels", style = MaterialTheme.typography.h6, color = MaterialTheme.colors.error)
        // TODO: Story picker prequels
        Text("TODO: Prequels", style = MaterialTheme.typography.h6, color = MaterialTheme.colors.error)
        FormField(
            id = "files",
            state = state,
            name = "Files",
            description = "Files with story content.",
            getter = { it.files },
            setter = { data, value -> data.copy(files = value) }
        ) { value, _, onValueChange ->
            StoryFiles(value, onValueChange)
        }
        OptionalFormField(
            id = "score",
            state = state,
            name = "Score",
            defaultValue = { Score(0F) },
            getter = { it.story.score },
            setter = { data, value -> data.copy(story = data.story.copy(score = value)) },
        ) { value, _, onValueChange ->
            StoryScoreInput(app, value, onValueChange)
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
        IntFormField(
            id = "timesRead",
            state = state,
            name = "Times read",
            getter = { it.story.timesRead },
            setter = { data, value -> data.copy(story = data.story.copy(timesRead = value)) },
            inputModifier = Modifier.width(100.dp).height(36.dp),
            range = 0..Int.MAX_VALUE
        )
    }
}

data class StoryFormData(
    val story: Story,
    val files: List<StoryFile> = emptyList()
)