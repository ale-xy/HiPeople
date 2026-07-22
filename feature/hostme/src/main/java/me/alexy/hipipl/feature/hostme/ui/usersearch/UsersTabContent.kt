package me.alexy.hipipl.feature.hostme.ui.usersearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.EmptyStateIllustration
import me.alexy.hipipl.core.designsystem.components.NameWithAgeText
import me.alexy.hipipl.core.designsystem.components.NetworkImage
import me.alexy.hipipl.core.designsystem.components.ResultCard
import me.alexy.hipipl.core.designsystem.components.SearchTextField
import me.alexy.hipipl.core.presentation.asString
import me.alexy.hipipl.feature.hostitem.R
import me.alexy.hipipl.feature.hostme.ui.hostsearch.HostsMapCard

@Composable
internal fun UsersTabContent(
    state: UserSearchState,
    onAction: (UserSearchAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboard.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            HostsMapCard(
                onClick = { onAction(UserSearchAction.OnUsersOnMapClick) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        item {
            SearchTextField(
                query = state.userQuery,
                onQueryChange = { onAction(UserSearchAction.OnUserQueryChange(it)) },
                placeholder = stringResource(R.string.phone_link),
                modifier = Modifier.padding(horizontal = 16.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = stringResource(R.string.cd_paste_button)
                    )
                },
                onTrailingIconClick = {
                    coroutineScope.launch {
                        val pasted = clipboard.getClipEntry()
                            ?.clipData
                            ?.getItemAt(0)
                            ?.text
                            ?.toString()
                        pasted?.let { onAction(UserSearchAction.OnPasteFromClipboard(it)) }
                    }
                }
            )
        }

        item {
            Text(
                text = stringResource(R.string.search_user),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            Button(
                onClick = {
                    keyboardController?.hide()
                    onAction(UserSearchAction.OnSearchUserClick)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(stringResource(R.string.search_user_btn))
            }
        }

        if (state.isSearchingUser) {
            item {
                LinearProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Text(
                    text = stringResource(R.string.searching_user),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        when {
            state.isSearchingUser -> Unit

            state.userSearchError != null -> {
                item {
                    EmptyStateIllustration(
                        icon = Icons.Default.SearchOff,
                        title = state.userSearchError.asString(),
                        subtitle = stringResource(R.string.user_not_found_hint),
                        iconTint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            state.userResult != null -> {
                item {
                    Text(
                        text = stringResource(R.string.found_user),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                item {
                    UserResultCard(
                        user = state.userResult,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun UsersTabContentPreview(
    @PreviewParameter(UsersTabContentPreviewParameterProvider::class) state: UserSearchState,
) {
    HiPeopleTheme {
        UsersTabContent(state = state, onAction = {})
    }
}

@Composable
private fun UserResultCard(user: UserCardUi, modifier: Modifier = Modifier) {
    ResultCard(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NameWithAgeText(name = user.name, ageText = user.ageText, modifier = Modifier.weight(1f))
            if (user.photoUrl != null) {
                NetworkImage(
                    model = user.photoUrl,
                    contentDescription = stringResource(R.string.host_photo_description),
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop,
                    indicatorSize = 20.dp,
                )
            }
        }
    }
}

@Preview
@Composable
private fun UserResultCardPreview() {
    HiPeopleTheme {
        UserResultCard(user = UserCardUi(userId = 1, photoUrl = null, name = "Anna", ageText = " (28 лет)"))
    }
}
