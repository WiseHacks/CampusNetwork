package com.example.campusnetwork.helper

sealed class Screen(val route:String){
    object MainAuthScreen : Screen("main_auth_screen")
    object ForgotPasswordScreen : Screen("forgot_password_screen")
    object RegisterUserScreen : Screen("register_user_screen")
    object UserDashBoardScreen : Screen("user_dashboard_screen")
    object IEScreen : Screen("ie_screen")
    object NoticeScreen : Screen("notice_screen")
    object MyProfileScreen : Screen("my_profile_screen")
    object AccountSettingScreen : Screen("account_setting_screen")
    object ViewProfileScreen : Screen("view_profile_screen")
    object WriteBlogScreen : Screen("write_blog_screen")
    object DetailedBlogScreen : Screen("detailed_blog_screen")
    object WriteCommentScreen : Screen("write_comment_screen")
    object WriteExpScreen : Screen("write_exp_screen")
    object DetailedExpScreen : Screen("detailed_exp_screen")
    object WriteNoticeScreen : Screen("write_exp_screen")
}
