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
import com.example.campusnetwork.model.IEexp
import com.example.campusnetwork.model.User
import com.example.campusnetwork.pagination.IEScreenState
import com.example.campusnetwork.pagination.IEexpViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.awaitAll

@Composable
fun IEScreen(context: Context, navController: NavController) {
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
                            type = "exp"
                        )
                    }
                }
                var expViewModel = viewModel<IEexpViewModel>()
                var state = expViewModel.state
                LaunchedEffect(key1 = Unit) {
//                    println("test")
                    expViewModel.reset()
                    expViewModel.state.items.clear()
                    expViewModel.loadNextItems()
                }
                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    items(state.items.size) { i ->
                        val item = state.items[i]
                        if (i >= state.items.size - 1 && !state.endReached && !state.isLoading) {
                            expViewModel.loadNextItems()
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(15.dp)
                                .clickable {
                                    val json =
                                        Uri.encode(Gson().toJson(item.toObject(IEexp::class.java)))
                                    navController.navigate("${Screen.DetailedExpScreen.route}/$json")
                                },
                            shape = RoundedCornerShape(corner = CornerSize(20.dp)),

                            ) {
                            Column(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                val exp = item.toObject(IEexp::class.java)
                                if (exp != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            exp.author_name,
                                            color = Color.Blue,
                                            modifier = Modifier.clickable {
                                                navController.navigate("${Screen.ViewProfileScreen.route}/${exp.author_id}")
                                            })
                                        Text(
                                            exp.timestamp.toDate().toString(),
                                            color = Color.Gray
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(exp.title, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(exp.company)
                                        Text(exp.type)
                                        Text(exp.date)
                                    }
                                    Text(
                                        exp.content,
                                        fontSize = 16.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Justify,
                                        maxLines = 5
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            exp.likes.toString() + " likes/ " + exp.dislikes.toString() + " dislikes",
                                            color = Color.Gray,
                                            modifier = Modifier.clickable {
//                                                navController.navigate("${Screen.ViewProfileScreen.route}/${exp.author_id}")
                                            })
                                        Text(
                                            exp.comment_ids.size.toString() + " comments",
                                            color = Color.Gray
                                        )
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
