package com.example.campusnetwork

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.campusnetwork.model.User
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewProfileScreen(
    context: Context,
    navController: NavController,
    uid: String
) {
    val db = FirebaseFirestore.getInstance()
    val data_loaded = remember {
        mutableStateOf(false)
    }
    val user = remember {
        mutableStateOf(User())
    }
    LaunchedEffect(key1 = !data_loaded.value) {
        db.collection("Users").document(uid.toString()).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document.exists()) {
                        user.value = User(
                            name = document["name"].toString(),
                            age = document["age"].toString(),
                            email = document["email"].toString(),
                            phone = document["phone"].toString(),
                            college = document["college"].toString(),
                            branch = document["branch"].toString(),
                            bio = document["bio"].toString(),
                            show_phone = document["show_phone"] as Boolean,
                            skills = document["skills"].toString(),
                            image_uri = document["image_uri"].toString(),
                            blog_ids = document["blog_ids"] as ArrayList<String>,
                            iv_ids = document["iv_ids"] as ArrayList<String>,
                            notice_ids = document["notice_ids"] as ArrayList<String>,
                            comment_ids = document["comment_ids"] as ArrayList<String>
                        )
                        data_loaded.value = true
                    }

                }
            }
    }
    if (!data_loaded.value) {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(60.dp))
        }
    } else {
        Drawer(context = context, navController = navController) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {

                item {

                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(15.dp)
                                .size(200.dp),
                            border = BorderStroke(
                                2.dp,
                                Color.Blue
                            )
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    if (user.value.image_uri.isEmpty()) {
                                        R.drawable.ic_pfp
                                    } else {
                                        try {
                                            user.value.image_uri
                                        }catch(e:Exception){
                                            R.drawable.ic_pfp
                                        }
                                    }
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var edit by remember{mutableStateOf(false)}
                        Text(text = "Name", modifier = Modifier.width(100.dp))
                        Text(text = user.value.name)
                    }
                }
                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Age", modifier = Modifier.width(100.dp))
                        Text(text = user.value.age)
                    }
                }
                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Email", modifier = Modifier.width(100.dp))
                        Text(text = user.value.email)
                    }
                }
                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Phone", modifier = Modifier.width(100.dp))
                        Text(text = if(user.value.show_phone) user.value.phone else "NA")
                    }
                }
                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Bio", modifier = Modifier.width(100.dp))
                        Text(text = user.value.bio)
                    }
                }
                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "College", modifier = Modifier.width(100.dp))
                        Text(text = user.value.college)
                    }
                }
                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Branch", modifier = Modifier.width(100.dp))
                        Text(text = user.value.branch)
                    }
                }
                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Skills", modifier = Modifier.width(100.dp))
                        Text(text = user.value.skills)
                    }
                }
            }
        }
    }
}
