package com.excaution.riwayaapp.presentation.navigation.bottombar


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Save

object BottomNavItems {
    val items = listOf(
        BottomNavItem(
            "Home",
            Icons.Outlined.Home
        ),

        BottomNavItem(
            "BookStore",
            Icons.Outlined.Book
        ),

        BottomNavItem(
            "Saved",
            Icons.Outlined.Save
        ),

        BottomNavItem(
            "Profile",
            Icons.Outlined.PersonOutline
        )

    )

}
