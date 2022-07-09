package com.example.campusnetwork

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.campusnetwork.helper.Screen
import com.example.campusnetwork.model.Blog
import com.example.campusnetwork.model.IEexp
import com.example.campusnetwork.model.Notice
import com.example.campusnetwork.model.User
import com.example.campusnetwork.pagination.IEScreenState
import com.example.campusnetwork.pagination.IEexpViewModel
import com.example.campusnetwork.pagination.NoticeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.awaitAll

@Composable
fun NoticeScreen(context: Context, navController: NavController) {
    val uid = FirebaseAuth.getInstance().uid.toString()
    Drawer(context = context, navController = navController) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(context = context, navController = navController)
            }) {

            Column(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),

                    shape = RoundedCornerShape(corner = CornerSize(20.dp)),
                    backgroundColor = Color(0.945f, 0.969f, 0.973f, 1.0f)
                ) {
                    val cur_user = remember {
                        mutableStateOf(User())
                    }
                    val user_data_loaded = remember {
                        mutableStateOf(false)
                    }
                    LaunchedEffect(key1 = !user_data_loaded.value) {
                        val uid = FirebaseAuth.getInstance().uid
                        val db = FirebaseFirestore.getInstance()
                        db.collection("Users").document(uid.toString()).get()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val document = it.result
                                    cur_user.value.name = document["name"].toString()
                                    cur_user.value.age = document["age"].toString()
                                    cur_user.value.email = document["email"].toString()
                                    cur_user.value.phone = document["phone"].toString()
                                    cur_user.value.college = document["college"].toString()
                                    cur_user.value.branch = document["branch"].toString()
                                    cur_user.value.bio = document["bio"].toString()
                                    cur_user.value.show_phone = document["show_phone"] as Boolean
                                    cur_user.value.skills = document["skills"].toString()
                                    cur_user.value.image_uri = document["image_uri"].toString()

                                    user_data_loaded.value = true
                                }
                            }
                    }
                    if (!user_data_loaded.value) {
                        Box(
                            modifier = Modifier.padding(15.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(30.dp))
                        }
                    } else {
                        HeaderCardHomeScreen(
                            context = context,
                            navController = navController,
                            cur_user = cur_user.value,
                            type = "notice"
                        )
                    }
                }
                var noticeViewModel = viewModel<NoticeViewModel>()
                var state = noticeViewModel.state
                LaunchedEffect(key1 = Unit) {
//                    println("test")
                    noticeViewModel.reset()
                    noticeViewModel.state.items.clear()
                    noticeViewModel.loadNextItems()
                }
                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    items(state.items.size) { i ->
                        val item = state.items[i]
                        if (i >= state.items.size - 1 && !state.endReached && !state.isLoading) {
                            noticeViewModel.loadNextItems()
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(15.dp)
                                .clickable {

                                },
                            shape = RoundedCornerShape(corner = CornerSize(20.dp)),

                            ) {
                            Column(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                val notice = item.toObject(Notice::class.java)
                                if (notice != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            notice.author_name,
                                            color = Color.Blue,
                                            modifier = Modifier.clickable {
                                                navController.navigate("${Screen.ViewProfileScreen.route}/${notice.author_id}")
                                            })
                                        Text(
                                            notice.date + " | " + notice.time,
                                            color = Color.Gray
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(notice.title, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        notice.content,
                                        fontSize = 16.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Justify
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        var likes by remember {
                                            mutableStateOf(notice.likes)
                                        }
                                        var dislikes by remember {
                                            mutableStateOf(notice.dislikes)
                                        }
                                        Text(
                                            text = "$likes likes/ $dislikes dislikes",
                                            color = Color.Gray,
                                            modifier = Modifier.clickable {
//                                                navController.navigate("${Screen.ViewProfileScreen.route}/${notice.author_id}")
                                            })
                                        Row(){
                                            Row() {
                                                val db = FirebaseFirestore.getInstance()
                                                var pressed by remember {
                                                    mutableStateOf(false)
                                                }
                                                var like by remember {
                                                    mutableStateOf(true)
                                                }
                                                var dislike by remember {
                                                    mutableStateOf(true)
                                                }
                                                var loaded by remember {
                                                    mutableStateOf(false)
                                                }
                                                val uid = FirebaseAuth.getInstance().uid.toString()
                                                db.collection("LikeDislikes").document(notice.id).collection("uid")
                                                    .document(uid).get().addOnCompleteListener {
                                                        if (it.isSuccessful) {
                                                            loaded = true
                                                            if (it.result.exists()) {
                                                                pressed = it.result["pressed"] as Boolean
                                                                like = it.result["like"] as Boolean
                                                                dislike = it.result["dislike"] as Boolean
                                                            }
                                                        }
                                                    }
                                                if (loaded) {
                                                    Button(onClick = {
                                                        if (pressed) {
                                                            likes++
                                                            dislikes--
                                                        } else {
                                                            likes++
                                                        }
                                                        pressed = true
                                                        like = false
                                                        dislike = true
                                                        val data = HashMap<String, Any>()
                                                        data.put("pressed", pressed)
                                                        data.put("like", like)
                                                        data.put("dislike", dislike)

                                                        db.collection("LikeDislikes").document(notice.id).collection("uid")
                                                            .document(uid).set(data)

                                                        val db = FirebaseFirestore.getInstance()
                                                        db.collection("Notices").document(notice.id).update("likes", likes)
                                                        db.collection("Notices").document(notice.id)
                                                            .update("dislikes", dislikes)
                                                    }, enabled = like) {
                                                        Text("Like")
                                                    }
                                                    Spacer(modifier = Modifier.width(5.dp))
                                                    Button(onClick = {
                                                        if (pressed) {
                                                            dislikes++
                                                            likes--
                                                        } else {
                                                            dislikes++
                                                            val data = HashMap<String, Any>()
                                                            data.put("flag", 1)
                                                            db.collection("LikeDislikes").document(notice.id)
                                                                .collection("uid")
                                                                .document(uid).set(data)

                                                        }
                                                        pressed = true
                                                        dislike = false
                                                        like = true
                                                        val data = HashMap<String, Any>()
                                                        data.put("pressed", pressed)
                                                        data.put("like", like)
                                                        data.put("dislike", dislike)

                                                        db.collection("LikeDislikes").document(notice.id).collection("uid")
                                                            .document(uid).set(data)

                                                        val db = FirebaseFirestore.getInstance()
                                                        db.collection("Notices").document(notice.id).update("likes", likes)
                                                        db.collection("Notices").document(notice.id)
                                                            .update("dislikes", dislikes)
                                                    }, enabled = dislike) {
                                                        Text("Dislike")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item {
                        if (state.isLoading) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(30.dp))
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(300.dp))
                    }

                }
            }
        }
    }
}
