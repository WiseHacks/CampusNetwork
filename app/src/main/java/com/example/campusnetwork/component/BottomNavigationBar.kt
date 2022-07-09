package com.example.campusnetwork

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.campusnetwork.helper.NavigationItem
import com.example.campusnetwork.helper.Screen

@Composable
fun BottomNavigationBar(context : Context, navController: NavController){
    val items = listOf(
        NavigationItem.GlobalBlogs,
        NavigationItem.InterviewExps,
        NavigationItem.Notices
    )
    BottomNavigation(
        Modifier.background(color = Color.White)
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                modifier = Modifier
                    .background(color = Color.White)
                    .padding(5.dp),
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title, modifier = Modifier.size(20.dp)) },
                label = { Text(text = item.title) },
                selectedContentColor = Color.Transparent,
                unselectedContentColor = Color.Blue.copy(0.7f),
                alwaysShowLabel = true,
                selected = false,
                onClick = {
                    if(item.title == "Blogs"){
                        navController.popBackStack()
                        navController.navigate(Screen.UserDashBoardScreen.route)
                    }
                    if(item.title == "Interview Exp"){
                        navController.popBackStack()
                        navController.navigate(Screen.IEScreen.route)
                    }
                    if(item.title == "Notices"){
                        navController.popBackStack()
                        navController.navigate(Screen.NoticeScreen.route)
                    }
                    /* Add code later */
                }
            )
        }
    }
}
