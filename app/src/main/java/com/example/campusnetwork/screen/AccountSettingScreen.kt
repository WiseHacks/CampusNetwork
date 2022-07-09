package com.example.campusnetwork

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.contracts.Effect

@Composable
fun AccountSettingScreen(context: Context, navController: NavController) {
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
        mutableStateOf("")
    }
    val show_phone = remember {
        mutableStateOf(false)
    }
    val image_uri = remember {
        mutableStateOf("")
    }
    val upload_image = remember {
        mutableStateOf("")
    }
    val data_loaded = remember {
        mutableStateOf(false)
    }
    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the cropped image
            upload_image.value = result.uriContent.toString()
        } else {
            // an error occurred cropping
            val exception = result.error
        }
    }
    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val cropOptions = CropImageContractOptions(uri, CropImageOptions())
        imageCropLauncher.launch(cropOptions)
    }

    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = !data_loaded.value) {
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
                        skills.value = document["skills"].toString()
                        image_uri.value = document["image_uri"].toString()
                        data_loaded.value = true
                    }

                }
            }
    }
    if (!data_loaded.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(60.dp))
        }
    } else {
        Drawer(context = context, navController = navController) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                state = listState
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
                                    if (image_uri.value.isEmpty()) {
                                        R.drawable.ic_pfp
                                    } else {
                                        try {
                                            image_uri.value
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
                        Text(
                            text = "Change profile picture",
                            color = Color.Blue,
                            modifier = Modifier.clickable {
                                launcher.launch("image/*")
                            })
                        if (!image_uri.value.isEmpty()) {
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "Remove profile picture",
                                color = Color.Blue,
                                modifier = Modifier.clickable {
                                    val builder = android.app.AlertDialog.Builder(context)
                                    builder.setTitle("Remove profile picture")
                                    builder.setMessage("Are you sure?")
                                    builder.setPositiveButton("Yes") { dialog, which ->
                                        upload_image.value = ""
                                        val uid = FirebaseAuth.getInstance().uid
                                        val storageRef = FirebaseStorage.getInstance().reference
                                        val pfp =
                                            storageRef.child("pfps/" + uid.toString() + ".jpg")
                                        image_uri.value = upload_image.value
                                        pfp.delete().addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                val db = FirebaseFirestore.getInstance();
                                                db.collection("Users").document(uid.toString())
                                                    .update("image_uri", "")
                                                    .addOnCompleteListener { it_ ->
                                                        if (it_.isSuccessful) {
                                                            Toast.makeText(
                                                                context,
                                                                "Removed",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            upload_image.value = ""
                                                        }
                                                    }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Failed",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                    builder.setNegativeButton("No") { dialog, which ->
                                    }
                                    builder.show()
                                })

                        }
                        if (!upload_image.value.isEmpty()) {
                            Spacer(Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "upload image",
                                    color = Color.Blue,
                                    modifier = Modifier.clickable {
                                        val uid = FirebaseAuth.getInstance().uid
                                        val storageRef = FirebaseStorage.getInstance().reference
                                        val pfp =
                                            storageRef.child("pfps/" + uid.toString() + ".jpg")
                                        println(pfp)
                                        println(upload_image.value.toUri())
                                        image_uri.value = upload_image.value
                                        pfp.putFile(upload_image.value.toUri())
                                            .continueWithTask { task ->
                                                if (!task.isSuccessful) {
                                                    task.exception?.let {
                                                        throw it
                                                    }
                                                }
                                                pfp.downloadUrl
                                            }
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    val db = FirebaseFirestore.getInstance();
                                                    db.collection("Users").document(uid.toString())
                                                        .update("image_uri", task.result.toString())
                                                        .addOnCompleteListener { it_ ->
                                                            if (it_.isSuccessful) {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Saved",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                                upload_image.value = ""
                                                            }
                                                        }
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Failed",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                    })
                                Spacer(modifier = Modifier.width(20.dp))
                                Text("Cancel", color = Color.Red, modifier = Modifier.clickable {
                                    upload_image.value = ""
                                })
                            }
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
                        TextField(value = name.value, onValueChange = {
                            name.value = it
                        },
                            keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(2)
                                    }
                                }
                            ))
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
                        TextField(value = age.value, onValueChange = {
                            age.value = it
                        },
                            keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(3)
                                    }
                                }
                            ))
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
                    Spacer(modifier = Modifier.height(3.dp))
                    Divider(color = Color.Blue, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(3.dp))
                }
                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Phone", modifier = Modifier.width(100.dp))
                        TextField(value = phone.value, onValueChange = {
                            phone.value = it
                        },
                            keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(4)
                                    }
                                }
                            ))
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
                        TextField(value = bio.value, onValueChange = {
                            bio.value = it
                        },
                            keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(5)
                                    }
                                }
                            ))
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
                        TextField(value = college.value, onValueChange = {
                            college.value = it
                        },
                            keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(6)
                                    }
                                }
                            ))
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
                        TextField(value = branch.value, onValueChange = {
                            branch.value = it
                        },
                            keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(7)
                                    }
                                }
                            ))
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
                        TextField(value = skills.value, onValueChange = {
                            skills.value = it
                        },
                            keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(8)
                                    }
                                }
                            ))
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Show Phone", modifier = Modifier.width(100.dp))
                        RadioButton(selected = !show_phone.value, onClick = {
                            show_phone.value = false
                        })
                        Text("No")
                        Spacer(modifier = Modifier.width(30.dp))
                        RadioButton(selected = show_phone.value, onClick = {
                            show_phone.value = true
                        })
                        Text("Yes")
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                val db = FirebaseFirestore.getInstance()
                                val new_data = HashMap<String, Any>()
                                new_data.put("name", name.value)
                                new_data.put("age", age.value.toInt())
                                new_data.put("phone", phone.value)
                                new_data.put("email", email.value)
                                new_data.put("bio", bio.value)
                                new_data.put("college", college.value)
                                new_data.put("branch", branch.value)
                                new_data.put("skills", skills.value)
                                new_data.put("show_phone", show_phone.value)
                                new_data.put("image_uri", image_uri.value)
                                db.collection(("Users")).document(uid.toString()).update(new_data)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "Updated successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            data_loaded.value = false
                                        }
                                    }

                            },
                        ) {
                            Text("Save")
                        }
                    }
                }
            }

        }
    }

}