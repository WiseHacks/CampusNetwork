package com.example.campusnetwork

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.contracts.Effect

@Composable
fun dummy(context : Context, navController: NavController){
    val uid = FirebaseAuth.getInstance().uid
    val db = FirebaseFirestore.getInstance()
    val name = remember {
        mutableStateOf("")
    }
    val age = remember {
        mutableStateOf("")
    }
    val email = remember {
        mutableStateOf("")
    }
    val phone = remember {
        mutableStateOf("")
    }
    val bio = remember {
        mutableStateOf("")
    }
    val college = remember {
        mutableStateOf("")
    }
    val branch = remember {
        mutableStateOf("")
    }
    val skills = remember {
        mutableStateOf(ArrayList<String> ())
    }
    val show_phone = remember{
        mutableStateOf(false)
    }
    val image_uri = remember {
        mutableStateOf("")
    }
    val upload_image = remember{
        mutableStateOf("")
    }
    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        upload_image.value = uri.toString() // UPDATE
    }
    LaunchedEffect(key1 = true){
        db.collection("Users").document(uid.toString()).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document.exists()) {
                        name.value = document["name"].toString()
                        age.value = document["age"].toString()
                        email.value = document["email"].toString()
                        phone.value = document["phone"].toString()
                        college.value = document["college"].toString()
                        branch.value = document["branch"].toString()
                        bio.value = document["bio"].toString()
                        show_phone.value = document["show_phone"] as Boolean
                        skills.value = document["skills"] as ArrayList<String>
                        image_uri.value = document["image_uri"].toString()
                    }

                }
            }
    }
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
                            3.dp,
                            Color.Blue
                        )
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(image_uri.value),
                            contentDescription = null,
                            modifier = Modifier
                                .wrapContentSize()
                                .clickable { },
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(text = "change profile picture", color = Color.Blue,  modifier = Modifier.clickable {
                        launcher.launch("image/*")

                    })
                    if(!upload_image.value.isEmpty()) {
                        Text(
                            text = "upload image",
                            color = Color.Blue,
                            modifier = Modifier.clickable {
                                val uid = FirebaseAuth.getInstance().uid
                                var storageRef = FirebaseStorage.getInstance().reference
                                val pfp = storageRef.child("pfps/" + uid.toString() + ".jpg")
                                println(pfp)
                                println(upload_image.value.toUri())
                                image_uri.value = upload_image.value
                                pfp.putFile(upload_image.value.toUri())
                                    .continueWithTask {task->
                                        if(!task.isSuccessful){
                                            task.exception?.let{
                                                throw it
                                            }
                                        }
                                        pfp.downloadUrl
                                    }
                                    .addOnCompleteListener{task->
                                        if(task.isSuccessful){
                                            val db = FirebaseFirestore.getInstance();
                                            db.collection("Users").document(uid.toString()).update("image_uri", task.result.toString()).addOnCompleteListener{it_->
                                                if(it_.isSuccessful){
                                                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                                                    upload_image.value = ""
                                                }
                                            }
                                        }
                                        else{
                                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            })
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
                    Text(text = "Name", modifier = Modifier.width(100.dp))
                    Text(text = name.value)
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
                    Text(text = age.value)
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
                    Text(text = email.value)
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
                    Text(text = phone.value)
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
                    Text(text = bio.value)
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
                    Text(text = college.value)
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
                    Text(text = branch.value)
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
                    skills.value.forEach {
                        Text(text = it)
                    }
                }
            }
        }

    }

}
