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
import com.example.campusnetwork.model.Notice
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Composable
fun WriteNoticeScreen(
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
                            Text(text = "Enter notice title", color = Color.Gray)
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
                        PostNotice(context, navController, title.value, content.value, cur_user)
                    }) {
                        Text("Post")
                    }
                }

            }
        }

    }
}

fun PostNotice(
    context: Context,
    navController: NavController,
    title: String,
    content: String,
    cur_user: User
) {
    val uid = FirebaseAuth.getInstance().uid
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    val notice = Notice(
        title = title,
        content = content,
        author_id = uid.toString(),
        author_image_uri = cur_user.image_uri,
        author_name = cur_user.name,
        date = sdf.format(Date()).substringBefore(' '),
        time = sdf.format(Date()).substringAfter(' '),
        likes = 0,
        dislikes = 0,
        timestamp = Timestamp.now()
    )
    var notice_data = HashMap<String, Any>()
    notice_data.put("title", notice.title)
    notice_data.put("content", notice.content)
    notice_data.put("author_id", notice.author_id)
    notice_data.put("author_image_uri", notice.author_image_uri)
    notice_data.put("author_name", notice.author_name)
    notice_data.put("date", notice.date)
    notice_data.put("time", notice.time)
    notice_data.put("likes", notice.likes)
    notice_data.put("dislikes", notice.dislikes)
    notice_data.put("timestamp", notice.timestamp)
    val db = FirebaseFirestore.getInstance()
    val ref = db.collection("Notices").document()
    notice.id = ref.id.toString()
    notice_data.put("id", notice.id)
    ref.set(notice_data).addOnCompleteListener {
        if (it.isSuccessful) {
            db.collection("Users").document(uid.toString())
                .update("notice_ids", FieldValue.arrayUnion(ref.id)).addOnCompleteListener { it_ ->
                    if (it_.isSuccessful) {
                        val ref = db.collection("LikeDislikes").document(notice.id)
                        ref.collection("uid").document().set(HashMap<String, Any>())
                        Toast.makeText(context, "Posted", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                        navController.navigate(Screen.NoticeScreen.route)

                    } else {
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
    }
}