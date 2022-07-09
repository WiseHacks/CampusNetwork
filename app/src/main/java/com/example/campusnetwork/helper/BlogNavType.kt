package com.example.campusnetwork.helper

import android.os.Bundle
import androidx.navigation.NavType
import com.example.campusnetwork.model.Blog
import com.example.campusnetwork.model.User
import com.google.gson.Gson

class BlogNavType : NavType<Blog>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Blog? {
        return bundle.getParcelable(key)
    }
    override fun parseValue(value: String): Blog {
        return Gson().fromJson(value, Blog::class.java)
    }
    override fun put(bundle: Bundle, key: String, value: Blog) {
        bundle.putParcelable(key, value)
    }
}