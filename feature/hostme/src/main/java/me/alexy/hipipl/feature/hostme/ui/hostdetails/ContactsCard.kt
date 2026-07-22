package me.alexy.hipipl.feature.hostme.ui.hostdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import me.alexy.hipipl.feature.hostitem.R

@Composable
fun ContactsCard(
    contacts: Map<ContactType, String>,
    isRevealed: Boolean,
    messageDraft: String,
    onRevealContacts: () -> Unit,
    onMessageDraftChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (contacts.isEmpty()) return

    ResultCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.user_contacts),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        if (!isRevealed) {
            Button(
                onClick = onRevealContacts,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            ) {
                Text(stringResource(R.string.button_get_contacts))
            }
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                contacts.forEach { (type, value) ->
                    PillChip(
                        text = stringResource(R.string.contact_chip_format, stringResource(type.labelRes()), value),
                        containerColor = type.chipColor(),
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
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = onSendMessage,
                    enabled = messageDraft.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                ) {
                    Text(stringResource(R.string.messages_send))
                }
            }
        }
    }
}

private fun ContactType.labelRes(): Int = when (this) {
    ContactType.VK -> R.string.contact_type_vk
    ContactType.TELEGRAM -> R.string.contact_type_telegram
    ContactType.FACEBOOK -> R.string.contact_type_facebook
    ContactType.PHONE -> R.string.contact_type_phone
    ContactType.OTHER -> R.string.contact_type_other
}

private fun ContactType.chipColor(): Color = when (this) {
    ContactType.VK -> AppColors.VkBrand
    ContactType.TELEGRAM, ContactType.PHONE -> AppColors.Success
    ContactType.FACEBOOK -> AppColors.InfoCta
    ContactType.OTHER -> AppColors.Success
}

@Preview
@Composable
private fun ContactsCardRevealedPreview() {
    HiPeopleTheme {
        ContactsCard(
            contacts = mapOf(ContactType.TELEGRAM to "annahost", ContactType.PHONE to "+79001234567"),
            isRevealed = true,
            messageDraft = "",
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
            contacts = mapOf(ContactType.TELEGRAM to "annahost"),
            isRevealed = false,
            messageDraft = "",
            onRevealContacts = {},
            onMessageDraftChange = {},
            onSendMessage = {},
        )
    }
}
