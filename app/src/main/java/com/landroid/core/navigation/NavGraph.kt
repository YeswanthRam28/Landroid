// app/src/main/java/com/landroid/core/navigation/NavGraph.kt
package com.landroid.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.landroid.features.alerts.presentation.AlertsScreen
import com.landroid.features.auth.presentation.OnboardingScreen
import com.landroid.features.auth.presentation.OtpScreen
import com.landroid.features.dashboard.presentation.DashboardScreen
import com.landroid.features.documents.presentation.DocumentVaultScreen
import com.landroid.features.map.presentation.GisMapScreen
import com.landroid.features.parcels.presentation.ParcelCreateScreen
import com.landroid.features.parcels.presentation.ParcelListScreen
import com.landroid.features.plant_zones.presentation.PlantZonesScreen
import com.landroid.features.settings.presentation.SettingsScreen
import com.landroid.features.tree_count.presentation.TreeCountScreen
import com.landroid.features.valuation.presentation.ValuationScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Otp : Screen("otp/{role}") {
        fun createRoute(role: String) = "otp/$role"
    }
    object LandownerDashboard : Screen("landowner_dashboard")
    object Parcels : Screen("parcels")
    object ParcelCreate : Screen("parcel_create")
    object Map : Screen("map/{parcelId}") {
        fun createRoute(parcelId: String) = "map/${parcelId.ifEmpty { "demo" }}"
    }
    object Dashboard : Screen("dashboard/{parcelId}") {
        fun createRoute(parcelId: String) = "dashboard/${parcelId.ifEmpty { "demo" }}"
    }
    object PlantZones : Screen("plant_zones/{parcelId}") {
        fun createRoute(parcelId: String) = "plant_zones/${parcelId.ifEmpty { "demo" }}"
    }
    object TreeCount : Screen("tree_count/{parcelId}") {
        fun createRoute(parcelId: String) = "tree_count/${parcelId.ifEmpty { "demo" }}"
    }
    object Valuation : Screen("valuation/{parcelId}") {
        fun createRoute(parcelId: String) = "valuation/${parcelId.ifEmpty { "demo" }}"
    }
    object Alerts : Screen("alerts")
    object Documents : Screen("documents/{parcelId}") {
        fun createRoute(parcelId: String) = "documents/${parcelId.ifEmpty { "demo" }}"
    }
    object Settings : Screen("settings")
}

@Composable
fun LandroidNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }

        composable(
            route = Screen.Otp.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            OtpScreen(
                role = backStackEntry.arguments?.getString("role") ?: "landowner",
                navController = navController
            )
        }

        composable(Screen.LandownerDashboard.route) {
            com.landroid.features.dashboard.presentation.LandownerDashboardScreen(navController = navController)
        }

        composable(Screen.Parcels.route) {
            ParcelListScreen(navController = navController)
        }

        composable(Screen.ParcelCreate.route) {
            ParcelCreateScreen(navController = navController)
        }

        composable(
            route = Screen.Map.route,
            arguments = listOf(navArgument("parcelId") { type = NavType.StringType })
        ) { backStackEntry ->
            GisMapScreen(
                parcelId = backStackEntry.arguments?.getString("parcelId") ?: "",
                navController = navController
            )
        }

        composable(
            route = Screen.Dashboard.route,
            arguments = listOf(navArgument("parcelId") { type = NavType.StringType })
        ) { backStackEntry ->
            DashboardScreen(
                parcelId = backStackEntry.arguments?.getString("parcelId") ?: "",
                navController = navController
            )
        }

        composable(
            route = Screen.PlantZones.route,
            arguments = listOf(navArgument("parcelId") { type = NavType.StringType })
        ) { backStackEntry ->
            PlantZonesScreen(
                parcelId = backStackEntry.arguments?.getString("parcelId") ?: "",
                navController = navController
            )
        }

        composable(
            route = Screen.TreeCount.route,
            arguments = listOf(navArgument("parcelId") { type = NavType.StringType })
        ) { backStackEntry ->
            TreeCountScreen(
                parcelId = backStackEntry.arguments?.getString("parcelId") ?: "",
                navController = navController
            )
        }

        composable(
            route = Screen.Valuation.route,
            arguments = listOf(navArgument("parcelId") { type = NavType.StringType })
        ) { backStackEntry ->
            ValuationScreen(
                parcelId = backStackEntry.arguments?.getString("parcelId") ?: "",
                navController = navController
            )
        }

        composable(Screen.Alerts.route) {
            AlertsScreen(navController = navController)
        }

        composable(
            route = Screen.Documents.route,
            arguments = listOf(navArgument("parcelId") { type = NavType.StringType })
        ) { backStackEntry ->
            DocumentVaultScreen(
                parcelId = backStackEntry.arguments?.getString("parcelId") ?: "",
                navController = navController
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
