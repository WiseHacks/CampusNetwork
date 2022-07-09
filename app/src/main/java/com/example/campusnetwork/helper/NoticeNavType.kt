package com.example.campusnetwork.helper

import android.os.Bundle
import androidx.navigation.NavType
import com.example.campusnetwork.model.Notice
import com.example.campusnetwork.model.User
import com.google.gson.Gson

class NoticeNavType : NavType<Notice>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Notice? {
        return bundle.getParcelable(key)
    }
    override fun parseValue(value: String): Notice {
        return Gson().fromJson(value, Notice::class.java)
    }
    override fun put(bundle: Bundle, key: String, value: Notice) {
        bundle.putParcelable(key, value)
    }
}