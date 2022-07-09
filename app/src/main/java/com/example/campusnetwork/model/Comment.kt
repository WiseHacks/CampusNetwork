package com.example.campusnetwork.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment(
    var id : String = "",
    var blog_id : String = "",
    var author_id : String = "",
    var author_image_uri : String = "",
    var author_name : String = "",
    var date : String = "",
    var time : String = "",
    var content : String = "",
    var ie_id : String = "",
    var timestamp : Timestamp = Timestamp.now()
) : Parcelable
