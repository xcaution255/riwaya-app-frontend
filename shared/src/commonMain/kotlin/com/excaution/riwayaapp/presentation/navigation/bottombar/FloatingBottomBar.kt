package com.excaution.riwayaapp.presentation.navigation.bottombar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.excaution.riwayaapp.presentation.theme.AccentPrimary
import com.excaution.riwayaapp.presentation.theme.BgDeep
import com.excaution.riwayaapp.presentation.theme.InkTheme
import com.excaution.riwayaapp.presentation.theme.TextFaint

@Composable
fun FloatingBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    onHomeClick: () -> Unit,
    onBookShopClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSavedClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .height(78.dp),

            shape = RoundedCornerShape(32.dp),
            shadowElevation = 18.dp,
            color = InkTheme.colors.bgDeep
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItems.items.forEachIndexed { index, item ->
                    when(index) {
                        2 -> {Spacer(modifier = Modifier.width(60.dp))}
                    }
                    val selected = selectedIndex == index
                    val scale = animateFloatAsState(
                        if (selected) 1.15f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        ),
                        label = ""
                    )
                    val tint = animateColorAsState(
                        if (selected)
                            InkTheme.colors.accentPrimary
                        else
                            InkTheme.colors.textFaint,
                        label = ""
                    )
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clickable {
                                onItemSelected(index)
                                // Directs navigation based on which item was clicked
                                when (index) {
                                    0 -> onHomeClick()
                                    1 -> onBookShopClick()
                                    2 -> onSavedClick()
                                    3 -> onProfileClick()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = tint.value,
                            modifier = Modifier.scale(scale.value)
                        )

                    }

                }

            }

        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.Center)
                .size(68.dp),
            containerColor = InkTheme.colors.accentPrimary,
            shape = CircleShape,
            onClick = onSearchClick
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = Color.White
            )

        }

    }

}

