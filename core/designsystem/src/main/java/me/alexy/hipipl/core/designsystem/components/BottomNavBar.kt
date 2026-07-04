package me.alexy.hipipl.core.designsystem.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import me.alexy.hipipl.core.designsystem.HiPeopleTheme
import me.alexy.hipipl.core.designsystem.components.preview.BottomNavBarPreviewParameterProvider

data class BottomNavItemUi(
    val icon: ImageVector,
    val label: String,
)

/**
 * Index-based bottom navigation bar. Route resolution stays with the caller
 * so this component has no dependency on any navigation-graph types.
 */
@Composable
fun HiPeopleBottomNavigationBar(
    items: List<BottomNavItemUi>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = { onItemSelected(index) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
            )
        }
    }
}

@Preview
@Composable
private fun HiPeopleBottomNavigationBarPreview(
    @PreviewParameter(BottomNavBarPreviewParameterProvider::class) selectedIndex: Int,
) {
    HiPeopleTheme {
        HiPeopleBottomNavigationBar(
            items = listOf(
                BottomNavItemUi(Icons.Default.Search, "Search"),
                BottomNavItemUi(Icons.Default.Favorite, "Favorites"),
                BottomNavItemUi(Icons.Default.Add, "Add"),
                BottomNavItemUi(Icons.AutoMirrored.Filled.Message, "Messages"),
                BottomNavItemUi(Icons.Default.Menu, "Menu"),
            ),
            selectedIndex = selectedIndex,
            onItemSelected = {},
        )
    }
}
