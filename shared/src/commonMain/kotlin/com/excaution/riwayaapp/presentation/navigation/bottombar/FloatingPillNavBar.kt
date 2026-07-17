package com.excaution.riwayaapp.presentation.navigation.bottombar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.excaution.riwayaapp.presentation.theme.InkTheme

data class PillNavItem(
    val label: String,
    val icon: ImageVector
)

private val defaultItems = listOf(
    PillNavItem("Home", Icons.Filled.Home),
    PillNavItem("Books", Icons.Filled.Book),
    PillNavItem("Saved", Icons.Filled.Bookmark),
    PillNavItem("Profile", Icons.Filled.Person)
)

@Composable
fun FloatingPillNavBar(
    items: List<PillNavItem> = defaultItems,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit,
    onBooksClick: () -> Unit,
    onSavedClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(50),
            color = InkTheme.colors.bgDeep,
            shadowElevation = 6.dp,
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    PillNavItemView(
                        item = item,
                        selected = index == selectedIndex,
                        onClick = { onItemSelected(index)
                            when(index) {
                                0 -> {onHomeClick()}
                                1 -> {onBooksClick()}
                                2 -> {onSavedClick()}
                                3 -> {onProfileClick()}
                            }}
                    )
                }
            }
        }

        // Detached circular Search / Ask button
        Surface(
            shape = CircleShape,
            color = InkTheme.colors.accentPrimary,
            shadowElevation = 6.dp,
            tonalElevation = 3.dp,
            modifier = Modifier
                .size(52.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSearchClick
                )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun PillNavItemView(
    item: PillNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Icon only appears for the selected tab, animating in/out
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn(spring(stiffness = 300f)) + expandHorizontally(spring(stiffness = 300f)),
            exit = fadeOut(spring(stiffness = 300f)) + shrinkHorizontally(spring(stiffness = 300f))
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = item.label,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
