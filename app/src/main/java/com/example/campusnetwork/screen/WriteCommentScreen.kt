package com.example.campusnetwork.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.campusnetwork.helper.Screen
import com.example.campusnetwork.model.Blog
import com.example.campusnetwork.model.Comment
import com.example.campusnetwork.model.IEexp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@Composable
fun WriteCommentScreen(
    context: Context,
    navController: NavController,
    blog: Blog = Blog(),
    ie_exp: IEexp = IEexp()
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(15.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            var content by remember {
                mutableStateOf("")
            }
            OutlinedTextField(
                value = content,
                onValueChange = {
                    content = it
                },
                label = {
                    Text(text = "Write a comment", color = Color.Gray)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Blue,
                    unfocusedLabelColor = Color.Gray,
                    textColor = Color.Black,
                    focusedBorderColor = Color.Blue,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("Users").document(FirebaseAuth.getInstance().uid.toString())
                        .get().addOnSuccessListener {
                            val author_name = it["name"].toString()
                            val author_image_uri = it["image_uri"].toString()
                            PostComment(
                                context,
                                navController,
                                content,
                                blog,
                                ie_exp,
                                author_name,
                                author_image_uri
                            )
                        }
                }) {
                    Text("Post")
                }
            }
        }

    }
}

fun PostComment(
    context: Context,
    navController: NavController,
    content: String,
    blog: Blog,
    ie_exp: IEexp,
    author_name: String,
    author_image_uri: String
) {
    println(author_name)
    val uid = FirebaseAuth.getInstance().uid
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    val comment = Comment(
        content = content,
        author_id = uid.toString(),
        author_image_uri = author_image_uri,
        author_name = author_name,
        date = sdf.format(Date()).substringBefore(' '),
        time = sdf.format(Date()).substringAfter(' '),
        blog_id = blog.id,
        ie_id = "",
        timestamp = Timestamp.now()
    )
    var comment_data = HashMap<String, Any>()
    comment_data.put("content", comment.content)
    comment_data.put("author_id", comment.author_id)
    comment_data.put("author_image_uri", comment.author_image_uri)
    comment_data.put("author_name", comment.author_name)
    comment_data.put("date", comment.date)
    comment_data.put("time", comment.time)
    comment_data.put("blog_id", comment.blog_id)
    comment_data.put("ie_id", comment.ie_id)
    comment_data.put("timestamp", comment.timestamp)
    val db = FirebaseFirestore.getInstance()
    val ref = db.collection("Comments").document()
    comment.id = ref.id.toString()
    comment_data.put("id", comment.id)
    ref.set(comment_data).addOnCompleteListener {
        if (it.isSuccessful) {
            if (ie_exp.id.isEmpty()) {
                db.collection("Blogs").document(blog.id)
                    .update("comment_ids", FieldValue.arrayUnion(ref.id))
                    .addOnCompleteListener { it_ ->
                        if (it_.isSuccessful) {
                            db.collection("Users").document(uid.toString())
                                .update("comment_ids", FieldValue.arrayUnion(ref.id))
                                .addOnCompleteListener { it_ ->
                                    if (it_.isSuccessful) {
                                        Toast.makeText(context, "Posted", Toast.LENGTH_SHORT)
                                            .show()
                                        navController.popBackStack()
                                        val json =
                                            Uri.encode(Gson().toJson(blog))
                                        navController.navigate("${Screen.DetailedBlogScreen.route}/$json")
                                    }
                                }

                        } else {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                db.collection("IEexps").document(ie_exp.id)
                    .update("comment_ids", FieldValue.arrayUnion(ref.id))
                    .addOnCompleteListener { it_ ->
                        if (it_.isSuccessful) {
                            db.collection("Users").document(uid.toString())
                                .update("comment_ids", FieldValue.arrayUnion(ref.id))
                                .addOnCompleteListener { it_ ->
                                    if (it_.isSuccessful) {
                                        Toast.makeText(context, "Posted", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                    val json =
                                        Uri.encode(Gson().toJson(ie_exp))
                                    navController.navigate("${Screen.DetailedExpScreen.route}/$json")
                                    }
                                }

                        } else {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
    }
}
