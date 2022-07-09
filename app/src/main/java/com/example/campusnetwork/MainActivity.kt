package com.example.campusnetwork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.campusnetwork.helper.BlogNavType
import com.example.campusnetwork.helper.IEexpNavType
import com.example.campusnetwork.model.User
import com.example.campusnetwork.helper.Screen
import com.example.campusnetwork.helper.UserNavType
import com.example.campusnetwork.model.Blog
import com.example.campusnetwork.model.IEexp
import com.example.campusnetwork.screen.DetailedBlogScreen
import com.example.campusnetwork.screen.DetailedExpScreen
import com.example.campusnetwork.screen.WriteCommentScreen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance();
        var startDestination = Screen.MainAuthScreen.route
        if (auth.currentUser != null) {
            startDestination = Screen.UserDashBoardScreen.route
        }
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = startDestination) {
                composable(route = Screen.MainAuthScreen.route) {
                    MainAuthScreen(context = this@MainActivity, navController)
                }
                composable(route = Screen.ForgotPasswordScreen.route) {
                    ForgotPasswordScreen(context = this@MainActivity, navController = navController)
                }
                composable(route = Screen.RegisterUserScreen.route) {
                    RegisterUserScreen(context = this@MainActivity, navController = navController)
                }
                composable(route = Screen.UserDashBoardScreen.route) {
                    UserDashBoardScreen(context = this@MainActivity, navController = navController)
                }
                composable(route = Screen.MyProfileScreen.route) {
                    MyProfileScreen(context = this@MainActivity, navController = navController)
                }
                composable(route = Screen.AccountSettingScreen.route) {
                    AccountSettingScreen(context = this@MainActivity, navController = navController)
                }
                composable(route = Screen.IEScreen.route) {
                    IEScreen(context = this@MainActivity, navController = navController)
                }
                composable(route = Screen.NoticeScreen.route) {
                    NoticeScreen(context = this@MainActivity, navController = navController)
                }
                composable(
                    "${Screen.ViewProfileScreen.route}/{uid}",
                    arguments = listOf(navArgument("uid") {
                        type = NavType.StringType
                    })
                ) {
                    ViewProfileScreen(
                        context = this@MainActivity,
                        navController = navController,
                        uid = it.arguments?.getString("uid").toString()
                    )
                }
                composable(
                    "${Screen.WriteBlogScreen.route}/{user}",
                    arguments = listOf(navArgument("user") {
                        type = UserNavType()
                    })
                ) {
                    val user = it.arguments?.getParcelable<User>("user")
                    if (user != null) {
                        println(user.toString())
                        WriteBlogScreen(
                            context = this@MainActivity,
                            navController = navController,
                            cur_user = user
                        )
                    }
                }
                composable(
                    "${Screen.DetailedBlogScreen.route}/{blog}",
                    arguments = listOf(navArgument("blog") {
                        type = BlogNavType()
                    })
                ) {
                    val blog = it.arguments?.getParcelable<Blog>("blog")
                    if (blog != null) {
                        DetailedBlogScreen(
                            context = this@MainActivity,
                            navController = navController,
                            blog = blog
                        )
                    }
                }

                composable(
                    "${Screen.WriteCommentScreen.route}/{blog}/{ie_exp}",
                    arguments = listOf(navArgument("blog") {
                        type = BlogNavType()
                    }, navArgument("ie_exp"){
                        type = IEexpNavType()
                    })
                ) {
                    val blog = it.arguments?.getParcelable<Blog>("blog")
                    val ie_exp = it.arguments?.getParcelable<IEexp>("ie_exp")
                    if (blog != null) {
                        if (ie_exp != null) {
                            WriteCommentScreen(
                                context = this@MainActivity,
                                navController = navController,
                                blog = blog,
                                ie_exp = ie_exp
                            )
                        }
                    }
                }

                composable(
                    "${Screen.WriteExpScreen.route}/{user}",
                    arguments = listOf(navArgument("user") {
                        type = UserNavType()
                    })
                ) {
                    val user = it.arguments?.getParcelable<User>("user")
                    if (user != null) {
                        println(user.toString())
                        WriteExpScreen(
                            context = this@MainActivity,
                            navController = navController,
                            cur_user = user
                        )
                    }
                }
                composable(
                    "${Screen.DetailedExpScreen.route}/{exp}",
                    arguments = listOf(navArgument("exp") {
                        type = IEexpNavType()
                    })
                ) {
                    val exp = it.arguments?.getParcelable<IEexp>("exp")
                    if (exp != null) {
                        DetailedExpScreen(
                            context = this@MainActivity,
                            navController = navController,
                            exp = exp
                        )
                    }
                }
                composable(
                    "${Screen.WriteNoticeScreen.route}/{user}",
                    arguments = listOf(navArgument("user") {
                        type = UserNavType()
                    })
                ) {
                    val user = it.arguments?.getParcelable<User>("user")
                    if (user != null) {
                        println(user.toString())
                        WriteNoticeScreen(
                            context = this@MainActivity,
                            navController = navController,
                            cur_user = user
                        )
                    }
                }
            }
            // profile, dashboard, blog, crud, msg --

        }
    }
}
