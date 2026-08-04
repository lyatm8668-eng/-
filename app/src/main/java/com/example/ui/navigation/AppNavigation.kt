package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.AppDao
import com.example.nearby.NearbyManager
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.MainScreen
import com.example.ui.screens.SplashScreen

@Composable
fun AppNavigation(nearbyManager: NearbyManager, appDao: AppDao) {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        NavHost(navController = navController, startDestination = "splash") {
            composable("splash") {
                SplashScreen(onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                })
            }
            composable("main") {
                MainScreen(navController = navController, nearbyManager = nearbyManager)
            }
            composable(
                route = "chat/{endpointId}/{deviceName}",
                arguments = listOf(
                    navArgument("endpointId") { type = NavType.StringType },
                    navArgument("deviceName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val endpointId = backStackEntry.arguments?.getString("endpointId") ?: ""
                val deviceName = backStackEntry.arguments?.getString("deviceName") ?: ""
                ChatDetailScreen(
                    navController = navController,
                    endpointId = endpointId,
                    deviceName = deviceName,
                    nearbyManager = nearbyManager,
                    appDao = appDao
                )
            }
        }
    }
}
