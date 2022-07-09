package com.example.campusnetwork

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.campusnetwork.model.Blog
import com.example.campusnetwork.model.User
import com.example.campusnetwork.helper.Screen
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Composable
fun WriteBlogScreen(
    context: Context,
    navController: NavController,
    cur_user: User
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        val title = remember {
            mutableStateOf("")
        }
        val content = remember {
            mutableStateOf("")
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = title.value,
                        onValueChange = {
                            title.value = it
                        },
                        label = {
                            Text(text = "Enter blog title", color = Color.Gray)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(horizontal = 10.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = Color.Blue,
                            unfocusedLabelColor = Color.Gray,
                            textColor = Color.Black,
                            focusedBorderColor = Color.Blue,
                        ),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = content.value,
                        onValueChange = {
                            content.value = it
                        },
                        label = {
                            Text(text = "Enter text", color = Color.Gray)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .padding(horizontal = 10.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = Color.Blue,
                            unfocusedLabelColor = Color.Gray,
                            textColor = Color.Black,
                            focusedBorderColor = Color.Blue,
                        ),
                    )
                    Button(onClick = {
                        Post(context, navController, title.value, content.value, cur_user)
                    }) {
                        Text("Post")
                    }
                }

            }
        }

    }
}

fun Post(
    context: Context,
    navController: NavController,
    title: String,
    content: String,
    cur_user: User
) {
    val uid = FirebaseAuth.getInstance().uid
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    val blog = Blog(
        title = title,
        content = content,
        author_id = uid.toString(),
        author_image_uri = cur_user.image_uri,
        author_name = cur_user.name,
        date = sdf.format(Date()).substringBefore(' '),
        time = sdf.format(Date()).substringAfter(' '),
        likes = 0,
        dislikes = 0,
        comment_ids = ArrayList(),
        timestamp = Timestamp.now()
    )
    var blog_data = HashMap<String, Any>()
    blog_data.put("title", blog.title)
    blog_data.put("content", blog.content)
    blog_data.put("author_id", blog.author_id)
    blog_data.put("author_image_uri", blog.author_image_uri)
    blog_data.put("author_name", blog.author_name)
    blog_data.put("date", blog.date)
    blog_data.put("time", blog.time)
    blog_data.put("likes", blog.likes)
    blog_data.put("dislikes", blog.dislikes)
    blog_data.put("comment_ids", blog.comment_ids)
    blog_data.put("timestamp", blog.timestamp)
    val db = FirebaseFirestore.getInstance()
    val ref = db.collection("Blogs").document()
    blog.id = ref.id.toString()
    blog_data.put("id", blog.id)
    ref.set(blog_data).addOnCompleteListener {
        if (it.isSuccessful) {
            db.collection("Users").document(uid.toString())
                .update("blog_ids", FieldValue.arrayUnion(ref.id)).addOnCompleteListener { it_ ->
                    if (it_.isSuccessful) {
                        val ref = db.collection("LikeDislikes").document(blog.id)
                        ref.collection("uid").document().set(HashMap<String, Any>())
                        Toast.makeText(context, "Posted", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                        navController.navigate(Screen.UserDashBoardScreen.route)

                    } else {
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
    }
}