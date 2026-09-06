package me.alexy.hipipl.feature.menu.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.feature.menu.R

@Composable
fun LeaveReviewSearchScreen(onNavigateBack: () -> Unit) {
    ComingSoonScreen(
        title = stringResource(R.string.leave_review_title),
        subtitle = stringResource(R.string.leave_review_coming_soon_subtitle),
        onNavigateBack = onNavigateBack,
    )
}

@Preview
@Composable
private fun LeaveReviewSearchScreenPreview() {
    HiPeopleTheme {
        LeaveReviewSearchScreen(onNavigateBack = {})
    }
}
