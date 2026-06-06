package com.example.ui.screens.superadmin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Approval
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
fun SuperAdminApp(viewModel: GheychiViewModel) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val navController = rememberNavController()
        
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Approval, contentDescription = "Salons") },
                        label = { Text("تایید سالن‌ها") },
                        selected = currentRoute == "salons",
                        onClick = {
                            navController.navigate("salons") {
                                popUpTo("salons") { inclusive = true }
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "salons",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("salons") {
                    SuperAdminScreens(viewModel)
                }
            }
        }
    }
}
