package com.example.campusnetwork

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.campusnetwork.helper.ListOfMenuItems
import com.example.campusnetwork.helper.MenuItem
import com.example.campusnetwork.helper.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun DrawerHeader() {
//     we want image here -
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val painter = painterResource(id = R.drawable.campus_network_big)
        Image(painter = painter, contentDescription = "logo of CN", Modifier.height(300.dp))
    }
}

@Composable
fun DrawerBody(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (MenuItem) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(items) { item ->
            Row(modifier = modifier
                .fillMaxWidth()
                .clickable {
                    onItemClick(item)
                }
                .padding(16.dp)) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.contentDescription
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    style = itemTextStyle
                )
            }
        }
    }
}

@Composable
fun Drawer(
    context: Context,
    navController: NavController,
    content: @Composable() () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = true) {
        scaffoldState.drawerState.close()
    }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                onNavigationIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            DrawerHeader()
            DrawerBody(
                items = ListOfMenuItems,
                onItemClick = {
                    if(it.id == "home"){
                        navController.popBackStack()
                        navController.navigate(Screen.UserDashBoardScreen.route)
                    }
                    if (it.id == "my_profile") {
                        navController.popBackStack()
                        navController.navigate(Screen.MyProfileScreen.route)
                    }
                    if (it.id == "sign_out") {
                        val builder = android.app.AlertDialog.Builder(context)
                        builder.setTitle("confirm sign-out")
                        builder.setMessage("Are you sure?")
                        builder.setPositiveButton("Yes") { dialog, which ->
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate(Screen.MainAuthScreen.route){
                                popUpTo(0)
                            }
                        }
                        builder.setNegativeButton("No") { dialog, which ->
                        }
                        builder.show()
                    }
                    if(it.id == "account_setting"){
                        navController.popBackStack()
                        navController.navigate(Screen.AccountSettingScreen.route)
                    }
                    //Toast.makeText(context, "Clicked On " + it.title, Toast.LENGTH_SHORT).show()
                }
            )
        }
    ) {
        content()
    }
}