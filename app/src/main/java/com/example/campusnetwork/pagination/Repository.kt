package com.example.campusnetwork.pagination


import com.example.campusnetwork.model.Blog
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firestore.v1.Cursor
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class Repository {


    suspend fun getItems(page: Query, pageSize: Long): Result<ArrayList<DocumentSnapshot>> {
        delay(2000L)
        var result: ArrayList<DocumentSnapshot> = ArrayList()
        page.limit(pageSize).get()
            .addOnSuccessListener { documentSnapshots ->
                documentSnapshots.documents.forEach {
                    result.add(it)
                }
            }.await()
        println("Fetched " + result.size + " " + page.toString())
        return Result.success(result)
    }
}