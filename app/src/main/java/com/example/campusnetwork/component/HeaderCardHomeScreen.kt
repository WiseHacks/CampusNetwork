package com.example.campusnetwork

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.campusnetwork.model.User
import com.example.campusnetwork.helper.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import java.lang.Exception

@Composable
fun HeaderCardHomeScreen(
    context:Context,
    navController:NavController,
    cur_user: User,
    type:String
){
    val uid = FirebaseAuth.getInstance().uid.toString()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(60.dp)
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .size(50.dp)
                    .background(color = Color.Transparent)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        if (cur_user.image_uri.isEmpty()) {
                            R.drawable.ic_pfp
                        } else {
                            try {
                                cur_user.image_uri
                            } catch (e: Exception) {
                                R.drawable.ic_pfp
                            }
                        }
                    ),
                    contentDescription = "pfp",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            navController.navigate("${Screen.ViewProfileScreen.route}/$uid")
                        },
                    contentScale = ContentScale.Crop
                )
            }
            Text(text = cur_user.name,
                color = Color.Blue,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    navController.navigate("${Screen.ViewProfileScreen.route}/$uid")
                }
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(10.dp)
                .clickable {
                    val json = Uri.encode(Gson().toJson(cur_user))
                    if(type == "blog"){
                        navController.navigate("${Screen.WriteBlogScreen.route}/$json")
                    }
                    if(type == "exp"){
                        navController.navigate("${Screen.WriteExpScreen.route}/$json")
                    }
                    if(type == "notice"){
                        navController.navigate("${Screen.WriteNoticeScreen.route}/$json")
                    }
                },
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Color(0.89f, 0.961f, 0.973f, 1.0f)
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Write a $type...",
                    color = Color.Blue,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(10.dp)
                )
//                            Icon(
//                                painter = painterResource(id = R.drawable.ic_write),
//                                contentDescription = "write icon",
//                                modifier = Modifier.size(20.dp)
//                                )
            }
        }
    }

}