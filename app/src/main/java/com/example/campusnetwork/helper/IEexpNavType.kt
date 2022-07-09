package com.example.campusnetwork.helper

import android.os.Bundle
import androidx.navigation.NavType
import com.example.campusnetwork.model.IEexp
import com.example.campusnetwork.model.User
import com.google.gson.Gson

class IEexpNavType : NavType<IEexp>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): IEexp? {
        return bundle.getParcelable(key)
    }
    override fun parseValue(value: String): IEexp {
        return Gson().fromJson(value, IEexp::class.java)
    }
    override fun put(bundle: Bundle, key: String, value: IEexp) {
        bundle.putParcelable(key, value)
    }
}