package com.example.campusnetwork.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.campusnetwork.Post
import com.example.campusnetwork.R
import com.example.campusnetwork.helper.Screen
import com.example.campusnetwork.model.Blog
import com.example.campusnetwork.model.Comment
import com.example.campusnetwork.model.IEexp
import com.example.campusnetwork.model.User
import com.example.campusnetwork.pagination.BlogViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.coroutineContext


@Composable
fun DetailedExpScreen(
    context: Context,
    navController: NavController,
    exp: IEexp
) {

    val db = FirebaseFirestore.getInstance()
    var cmntid_loaded by rememberSaveable {
        mutableStateOf(false)
    }
    var ids by remember {
        mutableStateOf(ArrayList<String>())
    }
    LaunchedEffect(Unit) {
//        println(cmntid_loaded)
        db.collection("IEexps").document(exp.id)
            .get().addOnSuccessListener {
                ids = it["comment_ids"] as ArrayList<String>
                ids.reverse()
                cmntid_loaded = true;
            }
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        LazyColumn(
            modifier = Modifier.padding(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Card(
                        shape = CircleShape,
                        modifier = Modifier
                            .padding(15.dp)
                            .size(80.dp),
                        border = BorderStroke(
                            2.dp,
                            Color.Blue
                        )
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                if (exp.author_image_uri.isEmpty()) {
                                    R.drawable.ic_pfp
                                } else {
                                    try {
                                        exp.author_image_uri
                                    } catch (e: Exception) {
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    ) {
                        Text(text = exp.author_name, color = Color.Blue, fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = exp.timestamp.toDate().toString(), fontSize = 18.sp)
                    }
                }
            }
            item {
                Text(text = "Title - " + exp.title, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(15.dp))
            }
            item{
                Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(exp.type, fontSize = 20.sp, color = Color.Gray)
                    Text(exp.date, fontSize = 20.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
            item {
                Text(text = exp.content, fontSize = 18.sp, textAlign = TextAlign.Justify)
                Spacer(modifier = Modifier.height(15.dp))
            }
            item {
                var likes by remember {
                    mutableStateOf(exp.likes)
                }
                var dislikes by remember {
                    mutableStateOf(exp.dislikes)
                }
                Text(
                    text = "$likes likes/ $dislikes dislikes",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
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
                        db.collection("LikeDislikes").document(exp.id).collection("uid")
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

                                db.collection("LikeDislikes").document(exp.id).collection("uid")
                                    .document(uid).set(data)

                                val db = FirebaseFirestore.getInstance()
                                db.collection("IEexps").document(exp.id).update("likes", likes)
                                db.collection("IEexps").document(exp.id)
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
                                    db.collection("LikeDislikes").document(exp.id)
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

                                db.collection("LikeDislikes").document(exp.id).collection("uid")
                                    .document(uid).set(data)

                                val db = FirebaseFirestore.getInstance()
                                db.collection("IEexps").document(exp.id).update("likes", likes)
                                db.collection("IEexps").document(exp.id)
                                    .update("dislikes", dislikes)
                            }, enabled = dislike) {
                                Text("Dislike")
                            }
                        }
                    }
                    Row() {
                        Button(onClick = {
                            val ieexpjson =
                                Uri.encode(Gson().toJson(exp))
                            val blogjson =
                                Uri.encode(Gson().toJson(Blog()))
                            navController.navigate("${Screen.WriteCommentScreen.route}/$blogjson/$ieexpjson")

                        }) {
                            Text("Comment")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
            item {
                Text("Comments", fontSize = 20.sp)
                Spacer(Modifier.height(10.dp))
            }
            item {
                if (cmntid_loaded) {
                    this@LazyColumn.items(ids) { it ->
                        var commentLoaded by remember {
                            mutableStateOf(false)
                        }
                        var comment by remember {
                            mutableStateOf(Comment())
                        }
                        db.collection("Comments").document(it).get().addOnSuccessListener {
                            comment.time = it["time"].toString()
                            comment.content = it["content"].toString()
                            comment.id = it["id"].toString()
                            comment.author_name = it["author_name"].toString()
                            comment.author_image_uri = it["author_image_uri"].toString()
                            comment.author_id = it["author_id"].toString()
                            comment.date = it["date"].toString()
                            comment.timestamp = it["timestamp"] as Timestamp
                            comment.blog_id = it["blog_id"].toString()
                            comment.ie_id = it["ie_id"].toString()
                            commentLoaded = true
                        }
                        if (commentLoaded) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp),
                                elevation = 2.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            comment.author_name,
                                            color = Color.Blue,
                                            modifier = Modifier.clickable {
                                                navController.navigate("${Screen.ViewProfileScreen.route}/${comment.author_id}")
                                            })
                                        Text(
                                            comment.date + " | " + comment.time,
                                            color = Color.Gray
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        comment.content,
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Justify
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                            }
                        }
                    }
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(30.dp))
                    }
                }
            }
        }
    }
}

