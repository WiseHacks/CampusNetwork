package com.example.campusnetwork.helper

import com.example.campusnetwork.R

sealed class NavigationItem(val route : String, val icon:Int, val title:String){
    object GlobalBlogs : NavigationItem("global_exps", R.drawable.ic_blog, "Blogs")
    object InterviewExps : NavigationItem("interview_exps", R.drawable.ic_interview_exp, "Interview Exp")
    object Notices : NavigationItem("notices", R.drawable.ic_notice, "Notices")
}
