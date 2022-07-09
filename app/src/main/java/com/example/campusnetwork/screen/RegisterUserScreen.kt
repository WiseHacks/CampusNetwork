package com.example.campusnetwork

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.campusnetwork.model.User
import com.example.campusnetwork.helper.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.HashMap


@Composable
fun RegisterUserScreen(context: Context, navController: NavController) {
    // name, age, email, phone, password
    var name by remember {
        mutableStateOf("")
    }
    var age by remember {
        mutableStateOf("")
    }
    var phone by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                label = {
                    Text(text = "Enter Full Name", color = Color.Gray)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Blue,
                    unfocusedLabelColor = Color.Gray,
                    textColor = Color.Black,
                    focusedBorderColor = Color.Blue,
                ),
                singleLine = true,
            )
            OutlinedTextField(
                value = age,
                onValueChange = {
                    age = it
                },
                label = {
                    Text(text = "Enter Age", color = Color.Gray)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Blue,
                    unfocusedLabelColor = Color.Gray,
                    textColor = Color.Black,
                    focusedBorderColor = Color.Blue,
                ),
                singleLine = true,
            )
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                },
                label = {
                    Text(text = "Enter Phone", color = Color.Gray)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Blue,
                    unfocusedLabelColor = Color.Gray,
                    textColor = Color.Black,
                    focusedBorderColor = Color.Blue,
                ),
                singleLine = true,
            )
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = {
                    Text(text = "Enter Email Address", color = Color.Gray)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Blue,
                    unfocusedLabelColor = Color.Gray,
                    textColor = Color.Black,
                    focusedBorderColor = Color.Blue,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                singleLine = true,
            )
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = {
                    Text(text = "Enter Password", color = Color.Gray)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Blue,
                    unfocusedLabelColor = Color.Gray,
                    textColor = Color.Black,
                    focusedBorderColor = Color.Blue,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                val auth = FirebaseAuth.getInstance()
                try {
                    if (name.isEmpty() || phone.isEmpty() || age.isEmpty() || age.toInt() < 0 || email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(
                            email
                        ).matches() || password.length < 6
                    ) {
                        Toast.makeText(context, "Enter Details Correctly", Toast.LENGTH_SHORT)
                            .show()
                    }
                    val user = User(name = name, age = age, phone = phone, email = email)
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {it->
                            if (it.isSuccessful) {
                                try{
                                    val db = FirebaseFirestore.getInstance()
                                    val cur_user = HashMap<String, Any>()
                                    cur_user.put("name", user.name)
                                    cur_user.put("age", user.age.toInt())
                                    cur_user.put("phone", user.phone)
                                    cur_user.put("email", user.email)
                                    cur_user.put("bio", user.bio)
                                    cur_user.put("college", user.college)
                                    cur_user.put("branch", user.branch)
                                    cur_user.put("skills", user.skills)
                                    cur_user.put("show_phone", user.show_phone)
                                    cur_user.put("image_uri", user.image_uri)
                                    cur_user.put("blog_ids", user.blog_ids)
                                    cur_user.put("iv_ids", user.iv_ids)
                                    cur_user.put("notice_ids", user.notice_ids)
                                    cur_user.put("comment_ids", user.comment_ids)

                                    db.collection("Users").document(auth.uid.toString()).set(cur_user)
                                        .addOnCompleteListener{it_ ->
                                            if(it_.isSuccessful){
                                                Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                                                navController.navigate(Screen.UserDashBoardScreen.route)
                                            }
                                            else{
                                                Toast.makeText(context, it_.exception.toString() + "Registration successful but failed to upload data!", Toast.LENGTH_SHORT)
                                                    .show()
                                            }
                                        }
                                }
                                catch (e:Exception){
                                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    /*TODO - Request Focus*/
                } catch (e: Exception) {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }

            }) {
                Text(text = "REGISTER")
            }

        }
    }
}