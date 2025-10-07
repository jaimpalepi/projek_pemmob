package com.example.fesnuk.navigation

sealed class NavigationRoutes(val route: String) {
    object Home : NavigationRoutes("home")
    object Nooks : NavigationRoutes("nooks")
    object NookDetail : NavigationRoutes("nook_detail/{id}") {
        fun createRoute(id: String) = "nook_detail/$id"
    }
    object PostDetail : NavigationRoutes("post_detail/{id}") {
        fun createRoute(id: String) = "post_detail/$id"
    }
    object CreatePost : NavigationRoutes("create_post")
    object CreatePostFromNook : NavigationRoutes("create_post_from_nook/{nookId}") {
        fun createRoute(nookId: String) = "create_post_from_nook/$nookId"
    }
}