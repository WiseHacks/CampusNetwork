package com.example.campusnetwork

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.campusnetwork.model.User
import com.example.campusnetwork.helper.Screen
import com.example.campusnetwork.model.IEexp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Composable
fun WriteExpScreen(
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
        val company = remember{
            mutableStateOf("")
        }
        val type = remember{
            mutableStateOf(false)
        }
        val mYear: Int
        val mMonth: Int
        val mDay: Int

        // Initializing a Calendar
        val mCalendar = Calendar.getInstance()

        // Fetching current year, month and day
        mYear = mCalendar.get(Calendar.YEAR)
        mMonth = mCalendar.get(Calendar.MONTH)
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

        mCalendar.time = Date()

        // Declaring a string value to
        // store date in string format
        val mDate = remember { mutableStateOf("") }
        val mDatePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                mDate.value = "$mDayOfMonth/${mMonth+1}/$mYear"
            }, mYear, mMonth, mDay
        )
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
                            Text(text = "Enter title", color = Color.Gray)
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
                        value = company.value,
                        onValueChange = {
                            company.value = it
                        },
                        label = {
                            Text(text = "Company", color = Color.Gray)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(horizontal = 10.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = Color.Blue,
                            unfocusedLabelColor = Color.Gray,
                            textColor = Color.Black,
                            focusedBorderColor = Color.Blue,
                        ),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Type", modifier = Modifier.width(50.dp))
                        RadioButton(selected = !type.value, onClick = {
                            type.value = false
                        })
                        Text("Off-Campus")
                        Spacer(modifier = Modifier.width(30.dp))
                        RadioButton(selected = type.value, onClick = {
                            type.value = true
                        })
                        Text("On-Campus")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(onClick = {
                        mDatePickerDialog.show()
                    }) {
                        Text("Select Date")
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Selected Date: ${mDate.value}", fontSize = 16.sp, textAlign = TextAlign.Center)
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
                        PostExp(context, navController, title.value, content.value, company.value, type.value, mDate.value, cur_user)
                    }) {
                        Text("Post")
                    }
                }

            }
        }

    }
}

fun PostExp(
    context: Context,
    navController: NavController,
    title: String,
    content: String,
    company: String,
    type: Boolean,
    date: String,
    cur_user: User
) {
    val uid = FirebaseAuth.getInstance().uid
    val exp = IEexp(
        title = title,
        content = content,
        company = company,
        date = date,
        type = if(!type) "Off-Campus" else "On-Campus",
        author_id = uid.toString(),
        author_image_uri = cur_user.image_uri,
        author_name = cur_user.name,
        likes = 0,
        dislikes = 0,
        comment_ids = ArrayList(),
        timestamp = Timestamp.now()
    )
    var exp_data = HashMap<String, Any>()
    exp_data.put("title", exp.title)
    exp_data.put("content", exp.content)
    exp_data.put("author_id", exp.author_id)
    exp_data.put("author_image_uri", exp.author_image_uri)
    exp_data.put("author_name", exp.author_name)
    exp_data.put("date", exp.date)
    exp_data.put("likes", exp.likes)
    exp_data.put("dislikes", exp.dislikes)
    exp_data.put("comment_ids", exp.comment_ids)
    exp_data.put("timestamp", exp.timestamp)
    exp_data.put("company", exp.company)
    exp_data.put("type", exp.type)
    val db = FirebaseFirestore.getInstance()
    val ref = db.collection("IEexps").document()
    exp.id = ref.id.toString()
    exp_data.put("id", exp.id)
    ref.set(exp_data).addOnCompleteListener {
        if (it.isSuccessful) {
            db.collection("Users").document(uid.toString())
                .update("iv_ids", FieldValue.arrayUnion(ref.id)).addOnCompleteListener { it_ ->
                    if (it_.isSuccessful) {
                        val ref = db.collection("LikeDislikes").document(exp.id)
                        ref.collection("uid").document().set(HashMap<String, Any>())
                        Toast.makeText(context, "Posted", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                        navController.navigate(Screen.IEScreen.route)

                    } else {
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
    }
}