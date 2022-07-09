package com.example.campusnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var name: String = "",
    var age: String = "",
    var college: String = "",
    var branch: String = "",
    var skills: String = "",
    var email: String = "",
    var phone: String = "",
    var show_phone: Boolean = false,
    var bio: String = "",
    var image_uri :String = "",
    var blog_ids : ArrayList<String> = ArrayList(),
    var comment_ids : ArrayList<String> = ArrayList(),
    var iv_ids : ArrayList<String> = ArrayList(),
    var notice_ids : ArrayList<String> = ArrayList()
) : Parcelable

