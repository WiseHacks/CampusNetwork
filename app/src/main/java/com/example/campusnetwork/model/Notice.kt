package com.example.campusnetwork.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notice(
    var id : String = "",
    var title : String = "",
    var content : String = "",
    var author_id : String = "",
    var author_image_uri : String = "",
    var author_name : String = "",
    var date : String = "",
    var time : String = "",
    var likes : Int = 0,
    var dislikes : Int = 0,
    var timestamp: Timestamp = Timestamp.now()
):Parcelable
