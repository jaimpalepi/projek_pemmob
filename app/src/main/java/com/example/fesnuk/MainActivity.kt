package com.example.fesnuk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fesnuk.data.NookRepository
import com.example.fesnuk.navigation.AppNavigation
import com.example.fesnuk.navigation.NavigationRoutes
import com.example.fesnuk.view.theme.AppBarBackground
import com.example.fesnuk.view.theme.DarkBackground
import com.example.fesnuk.view.theme.FesnukTheme
import com.example.fesnuk.view.theme.OnlineGreen
import com.example.fesnuk.view.theme.TextPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FesnukTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Create repository instance
    val repository = NookRepository()

    // Define bottom navigation items
    val bottomNavItems = listOf(
        BottomNavItem("Home", Icons.Default.Home, NavigationRoutes.Home.route),
        BottomNavItem("Nooks", Icons.Default.List, NavigationRoutes.Nooks.route)
    )

    // Check if current destination should show bottom navigation
    val showBottomNav = currentDestination?.route in listOf(
        NavigationRoutes.Home.route,
        NavigationRoutes.Nooks.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (showBottomNav) {
                TopAppBar(
                    title = {
                        Text(
                            text = "fesnuk",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AppBarBackground
                    ),
                    actions = {
                        IconButton(
                            onClick = { /* Filter action */ }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Filter",
                                tint = TextPrimary
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(
                    containerColor = AppBarBackground
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = OnlineGreen,
                                selectedTextColor = OnlineGreen,
                                unselectedIconColor = TextPrimary,
                                unselectedTextColor = TextPrimary,
                                indicatorColor = DarkBackground
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomNav) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(NavigationRoutes.CreatePost.route)
                    },
                    containerColor = OnlineGreen
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Post",
                        tint = DarkBackground
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DarkBackground)
        ) {
            AppNavigation(
                navController = navController,
                repository = repository
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)
    