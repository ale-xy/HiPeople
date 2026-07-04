package me.alexy.hipipl.core.designsystem.components.preview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import me.alexy.hipipl.core.designsystem.GenderAccent
import me.alexy.hipipl.core.designsystem.components.EmptyStatePreviewState
import me.alexy.hipipl.core.designsystem.components.SuggestionItemUi
import me.alexy.hipipl.core.designsystem.components.TriState

data class ResultCardPreviewState(
    val accentColor: Color?,
    val name: String,
    val subtitle: String,
)

class ResultCardPreviewParameterProvider : PreviewParameterProvider<ResultCardPreviewState> {
    override val values = sequenceOf(
        ResultCardPreviewState(accentColor = Color(0xFF2196F3), name = "Anna (28)", subtitle = "Moscow"),
        ResultCardPreviewState(accentColor = null, name = "Ivan Petrov (36)", subtitle = "57 references"),
    )
}

class GenderAccentPreviewParameterProvider : PreviewParameterProvider<GenderAccent> {
    override val values = sequenceOf(
        GenderAccent.UNSPECIFIED,
        GenderAccent.MALE,
        GenderAccent.FEMALE,
        GenderAccent.SEVERAL,
    )
}

data class AmenityChipRowPreviewState(
    val separateRoom: Boolean,
    val kidsAllowed: Boolean,
    val petsAtHome: Boolean,
)

class AmenityChipRowPreviewParameterProvider : PreviewParameterProvider<AmenityChipRowPreviewState> {
    override val values = sequenceOf(
        AmenityChipRowPreviewState(separateRoom = true, kidsAllowed = true, petsAtHome = true),
        AmenityChipRowPreviewState(separateRoom = true, kidsAllowed = false, petsAtHome = false),
        AmenityChipRowPreviewState(separateRoom = false, kidsAllowed = true, petsAtHome = false),
        AmenityChipRowPreviewState(separateRoom = false, kidsAllowed = false, petsAtHome = true),
        AmenityChipRowPreviewState(separateRoom = false, kidsAllowed = false, petsAtHome = false),
    )
}

data class ReferencesTextPreviewState(
    val referenceCount: Int,
    val scoreText: String?,
)

class ReferencesTextPreviewParameterProvider : PreviewParameterProvider<ReferencesTextPreviewState> {
    override val values = sequenceOf(
        ReferencesTextPreviewState(referenceCount = 57, scoreText = "9.4"),
        ReferencesTextPreviewState(referenceCount = 86, scoreText = null),
        ReferencesTextPreviewState(referenceCount = 3, scoreText = "9.4"),
        ReferencesTextPreviewState(referenceCount = 0, scoreText = null),
    )
}

data class SearchTextFieldPreviewState(
    val query: String,
    val placeholder: String,
    val showTrailingIcon: Boolean,
)

class SearchTextFieldPreviewParameterProvider : PreviewParameterProvider<SearchTextFieldPreviewState> {
    override val values = sequenceOf(
        SearchTextFieldPreviewState(query = "", placeholder = "City, country or coordinates", showTrailingIcon = true),
        SearchTextFieldPreviewState(query = "Moscow", placeholder = "City, country or coordinates", showTrailingIcon = true),
        SearchTextFieldPreviewState(query = "", placeholder = "Social network link or phone number", showTrailingIcon = false),
    )
}

data class SuggestionsDropdownPreviewState(
    val suggestions: List<SuggestionItemUi>,
    val isLoading: Boolean,
)

class SuggestionsDropdownPreviewParameterProvider : PreviewParameterProvider<SuggestionsDropdownPreviewState> {
    override val values = sequenceOf(
        SuggestionsDropdownPreviewState(
            suggestions = listOf(
                SuggestionItemUi(id = "1", primaryText = "Moscow", secondaryText = "Russia"),
                SuggestionItemUi(id = "2", primaryText = "Vidnoye", secondaryText = "Russia, Moscow Oblast"),
                SuggestionItemUi(id = "add", primaryText = "Add \"Mos\"", secondaryText = ""),
            ),
            isLoading = false,
        ),
        SuggestionsDropdownPreviewState(suggestions = emptyList(), isLoading = false),
        SuggestionsDropdownPreviewState(suggestions = emptyList(), isLoading = true),
    )
}

class ThreeWayTogglePreviewParameterProvider : PreviewParameterProvider<TriState> {
    override val values = sequenceOf(
        TriState.YES,
        TriState.UNSPECIFIED,
        TriState.NO,
    )
}

class CompassArrowIconPreviewParameterProvider : PreviewParameterProvider<Float> {
    override val values = sequenceOf(0f, 45f, 90f, 180f, 270f)
}

data class LoadingErrorEmptyContentPreviewState(
    val isLoading: Boolean,
    val error: String?,
    val isEmpty: Boolean,
)

class LoadingErrorEmptyContentPreviewParameterProvider :
    PreviewParameterProvider<LoadingErrorEmptyContentPreviewState> {
    override val values = sequenceOf(
        LoadingErrorEmptyContentPreviewState(isLoading = true, error = null, isEmpty = false),
        LoadingErrorEmptyContentPreviewState(isLoading = false, error = "Something went wrong", isEmpty = false),
        LoadingErrorEmptyContentPreviewState(isLoading = false, error = null, isEmpty = true),
        LoadingErrorEmptyContentPreviewState(isLoading = false, error = null, isEmpty = false),
    )
}

class BottomNavBarPreviewParameterProvider : PreviewParameterProvider<Int> {
    override val values = sequenceOf(0, 2, 4)
}

class EmptyStateIllustrationPreviewParameterProvider : PreviewParameterProvider<EmptyStatePreviewState> {
    override val values = sequenceOf(
        EmptyStatePreviewState(
            icon = Icons.Default.Home,
            title = "Find hosts near you",
            subtitle = "Enter a city, country or coordinates above to see hosts you can stay with.",
            iconTint = Color(0xFFC13D14), // primary color
        ),
        EmptyStatePreviewState(
            icon = Icons.Default.SearchOff,
            title = "No hosts found",
            subtitle = "Try a different city or adjust your filters.",
            iconTint = Color(0xFF87736B), // outline color
            action = @Composable { OutlinedButton(onClick = {}) { Text("Edit filters") } },
        ),
        EmptyStatePreviewState(
            icon = Icons.Default.SearchOff,
            title = "No user found",
            subtitle = "Check the link or phone number and try again.",
            iconTint = Color(0xFF87736B), // outline color
        ),
    )
}
