package com.example.campusnetwork

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.campusnetwork.helper.Screen
import com.google.firebase.auth.FirebaseAuth


@Composable
fun ForgotPasswordScreen(context: Context, navController: NavController) {
    var email by remember {
        mutableStateOf("")
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    )
    {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = {
                    Text(text = "Enter Email", color = Color.Gray)
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
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                try {
                    val auth = FirebaseAuth.getInstance()
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Reset link has been sent",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate(Screen.MainAuthScreen.route)
                            } else {
                                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                } catch (e: Exception) {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }

            }) {
                Text(text = "SEND RESET LINK")
            }

        }
    }
}
