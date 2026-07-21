package me.alexy.hipipl.feature.hostme.ui.usersearch

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import me.alexy.hipipl.core.presentation.UiText

class UsersTabContentPreviewParameterProvider : PreviewParameterProvider<UserSearchState> {
    override val values = sequenceOf(
        UserSearchState(userQuery = ""),
        UserSearchState(userQuery = "https://vk.com/id1", isSearchingUser = true),
        UserSearchState(
            userQuery = "https://vk.com/id1",
            userResult = UserCardUi(userId = 1, photoUrl = null, name = "Anna", ageText = " (28 лет)"),
        ),
        UserSearchState(
            userQuery = "https://vk.com/id1",
            userSearchError = UiText.DynamicString("User not found"),
        ),
    )
}
