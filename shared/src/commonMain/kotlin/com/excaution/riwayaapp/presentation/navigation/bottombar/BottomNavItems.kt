package com.excaution.riwayaapp.presentation.navigation.bottombar


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person

object BottomNavItems {
    val items = listOf(
        BottomNavItem(
            "Home",
            Icons.Rounded.Home
        ),

        BottomNavItem(
            "BookStore",
            Icons.Rounded.Book
        ),

        BottomNavItem(
            "Saved",
            Icons.Rounded.Bookmark
        ),

        BottomNavItem(
            "Profile",
            Icons.Rounded.Person
        )

    )

}
