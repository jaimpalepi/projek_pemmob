package com.example.fesnuk.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fesnuk.data.NookRepository
import com.example.fesnuk.view.screens.CreateThreadScreen
import com.example.fesnuk.view.screens.HomeScreen
import com.example.fesnuk.view.screens.NookDetailScreen
import com.example.fesnuk.view.screens.NooksScreen
import com.example.fesnuk.view.screens.ThreadScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    repository: NookRepository
) {
    // State to track when home should refresh
    var shouldRefreshHome by remember { mutableStateOf(false) }
    
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Home.route
    ) {
        composable(NavigationRoutes.Home.route) {
            HomeScreen(
                onPostClick = { postId ->
                    navController.navigate(NavigationRoutes.PostDetail.createRoute(postId.toString()))
                },
                repository = repository,
                shouldRefresh = shouldRefreshHome,
                onRefreshHandled = { shouldRefreshHome = false }
            )
        }
        
        composable(NavigationRoutes.Nooks.route) {
            NooksScreen(
                onNookClick = { nookId ->
                    navController.navigate(NavigationRoutes.NookDetail.createRoute(nookId))
                }
            )
        }
        
        composable(NavigationRoutes.NookDetail.route) { backStackEntry ->
            val nookId = backStackEntry.arguments?.getString("id") ?: ""
            NookDetailScreen(
                nookId = nookId,
                onBackClick = {
                    navController.popBackStack()
                },
                onPostClick = { postId ->
                    navController.navigate(NavigationRoutes.PostDetail.createRoute(postId.toString()))
                },
                onCreateThreadClick = { nookIdForThread ->
                    navController.navigate(NavigationRoutes.CreatePostFromNook.createRoute(nookIdForThread))
                }
            )
        }
        
        composable(NavigationRoutes.PostDetail.route) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("id") ?: ""
            ThreadScreen(
                postId = postId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(NavigationRoutes.CreatePost.route) {
            val context = LocalContext.current
            CreateThreadScreen(
                onCloseClick = {
                    navController.popBackStack()
                },
                onPostCreated = { postId ->
                    // Set refresh flag and navigate back to home
                    shouldRefreshHome = true
                    navController.popBackStack()
                },
                context = context
            )
        }
        
        composable(NavigationRoutes.CreatePostFromNook.route) { backStackEntry ->
            val nookId = backStackEntry.arguments?.getString("nookId") ?: ""
            val context = LocalContext.current
            CreateThreadScreen(
                onCloseClick = {
                    navController.popBackStack()
                },
                onPostCreated = { postId ->
                    // Navigate back to the nook detail screen
                    navController.popBackStack()
                },
                context = context,
                preselectedNookId = nookId
            )
        }
    }
}