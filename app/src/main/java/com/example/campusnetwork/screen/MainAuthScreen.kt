package com.example.campusnetwork

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.campusnetwork.helper.Screen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainAuthScreen(context: Context, navController: NavController) {
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
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon",
                        tint = Color.Blue,
                        modifier = Modifier.size(25.dp)
                    )
                }
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
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_password),
                        contentDescription = "Email Icon",
                        tint = Color.Blue,
                        modifier = Modifier.size(25.dp)
                    )
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                val auth = FirebaseAuth.getInstance()
                try {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.UserDashBoardScreen.route)
                            } else {
                                Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                            }
                        }
                } catch (e: Exception) {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }

            }) {
                Text(text = "SIGN IN")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Forgot password?", modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("forgot_password_screen")
                    },
                textAlign = TextAlign.Center,
                color = Color.Blue,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "New user? Register here", modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("register_user_screen")
                    },
                textAlign = TextAlign.Center,
                color = Color.Blue,
                fontSize = 18.sp
            )
        }

    }
}
