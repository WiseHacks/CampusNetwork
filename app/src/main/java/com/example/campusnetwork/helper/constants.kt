package com.example.campusnetwork.helper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings

val ListOfMenuItems = listOf<MenuItem>(
    MenuItem(
        id = "home",
        title = "Home",
        contentDescription = "Home",
        icon = Icons.Default.Home
    ),
    MenuItem(
        id = "my_profile",
        title = "My Profile",
        contentDescription = "My Profile",
        icon = Icons.Default.Person
    ),
    MenuItem(
        id = "account_setting",
        title = "Account Setting",
        contentDescription = "Account Setting",
        icon = Icons.Default.Settings
    ),

    MenuItem(
        id = "sign_out",
        title = "Sign Out",
        contentDescription = "Sign Out",
        icon = Icons.Default.ExitToApp
    ),
)
