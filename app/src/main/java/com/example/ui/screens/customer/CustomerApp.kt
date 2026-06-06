package com.example.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.viewmodel.GheychiViewModel

@Composable
fun CustomerApp(viewModel: GheychiViewModel) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val navController = rememberNavController()
        
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Store, contentDescription = "Salons") },
                        label = { Text("سالن‌ها") },
                        selected = currentRoute == "salon_list",
                        onClick = {
                            navController.navigate("salon_list") {
                                popUpTo("salon_list") { inclusive = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Bookings") },
                        label = { Text("رزروهای من") },
                        selected = currentRoute == "my_bookings",
                        onClick = {
                            navController.navigate("my_bookings")
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "salon_list",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("salon_list") {
                    SalonListScreen(viewModel, onSalonSelected = { salonId -> 
                        navController.navigate("salon_detail/$salonId")
                    })
                }
                composable("salon_detail/{salonId}") { backStackEntry ->
                    val salonId = backStackEntry.arguments?.getString("salonId")?.toIntOrNull()
                    if (salonId != null) {
                        SalonDetailScreen(viewModel, salonId, onBookingRequested = {
                            navController.popBackStack()
                            navController.navigate("my_bookings")
                        })
                    }
                }
                composable("my_bookings") {
                    MyBookingsScreen(viewModel)
                }
            }
        }
    }
}
