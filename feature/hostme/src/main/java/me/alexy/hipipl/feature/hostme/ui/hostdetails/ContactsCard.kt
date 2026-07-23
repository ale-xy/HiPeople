package me.alexy.hipipl.feature.hostme.ui.hostdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.alexy.hipipl.core.designsystem.AppColors
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.PillChip
import me.alexy.hipipl.core.designsystem.components.ResultCard
import me.alexy.hipipl.core.domain.ContactType
import me.alexy.hipipl.core.presentation.asString
import me.alexy.hipipl.feature.hostitem.R

@Composable
fun ContactsCard(
    contactsState: ContactsUiState,
    messageDraft: String,
    isSendingMessage: Boolean,
    canSendMessage: Boolean,
    onRevealContacts: () -> Unit,
    onMessageDraftChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ResultCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.user_contacts),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        when (contactsState) {
            is ContactsUiState.Idle -> {
                Button(
                    onClick = onRevealContacts,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                ) {
                    Text(stringResource(R.string.button_get_contacts))
                }
            }

            is ContactsUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
            }

            is ContactsUiState.Blocked -> {
                Text(
                    text = contactsState.message.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            is ContactsUiState.Revealed -> {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    contactsState.chips.forEach { chip ->
                        PillChip(
                            text = stringResource(R.string.contact_chip_format, stringResource(chip.type.labelRes()), chip.value),
                            containerColor = chip.type.chipColor(),
                            contentColor = Color.White,
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = messageDraft,
                        onValueChange = onMessageDraftChange,
                        placeholder = { Text(stringResource(R.string.messages_input_placeholder)) },
                        minLines = 3,
                        enabled = canSendMessage,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (!canSendMessage) {
                        Text(
                            text = stringResource(R.string.message_send_rate_limited),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    Button(
                        onClick = onSendMessage,
                        enabled = canSendMessage && !isSendingMessage && messageDraft.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                    ) {
                        if (isSendingMessage) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text(stringResource(R.string.messages_send))
                        }
                    }
                }
            }
        }
    }
}

private fun ContactType.labelRes(): Int = when (this) {
    ContactType.VK -> R.string.contact_type_vk
    ContactType.TELEGRAM -> R.string.contact_type_telegram
    ContactType.FACEBOOK -> R.string.contact_type_facebook
    ContactType.WHATSAPP -> R.string.contact_type_whatsapp
    ContactType.PHONE -> R.string.contact_type_phone
    ContactType.OTHER -> R.string.contact_type_other
}

private fun ContactType.chipColor(): Color = when (this) {
    ContactType.VK -> AppColors.VkBrand
    ContactType.TELEGRAM, ContactType.PHONE, ContactType.WHATSAPP -> AppColors.Success
    ContactType.FACEBOOK -> AppColors.InfoCta
    ContactType.OTHER -> AppColors.Success
}

@Preview
@Composable
private fun ContactsCardRevealedPreview() {
    HiPeopleTheme {
        ContactsCard(
            contactsState = ContactsUiState.Revealed(
                chips = listOf(
                    ContactChipUi(ContactType.TELEGRAM, "annahost"),
                    ContactChipUi(ContactType.PHONE, "+79001234567"),
                )
            ),
            messageDraft = "",
            isSendingMessage = false,
            canSendMessage = true,
            onRevealContacts = {},
            onMessageDraftChange = {},
            onSendMessage = {},
        )
    }
}

@Preview
@Composable
private fun ContactsCardIdlePreview() {
    HiPeopleTheme {
        ContactsCard(
            contactsState = ContactsUiState.Idle,
            messageDraft = "",
            isSendingMessage = false,
            canSendMessage = true,
            onRevealContacts = {},
            onMessageDraftChange = {},
            onSendMessage = {},
        )
    }
}
