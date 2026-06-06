package com.example.ui.screens.salon

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
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
fun SalonApp(viewModel: GheychiViewModel) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val isApproved by viewModel.isSalonApproved.collectAsState()

        if (isApproved == true) {
            val navController = rememberNavController()
            
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        
                        NavigationBarItem(
                            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Requests") },
                            label = { Text("درخواست‌ها") },
                            selected = currentRoute == "requests",
                            onClick = {
                                navController.navigate("requests") {
                                    popUpTo("requests") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "requests",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("requests") {
                        SalonRequestsScreen(viewModel)
                    }
                }
            }
        } else {
            SalonOnboardingScreen(viewModel)
        }
    }
}
