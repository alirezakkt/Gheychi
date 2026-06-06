package com.example

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.GheychiApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GheychiViewModel
import com.example.ui.screens.customer.CustomerApp
import com.example.ui.screens.salon.SalonApp
import com.example.ui.screens.superadmin.SuperAdminApp

class MainActivity : ComponentActivity() {
  private val viewModel: GheychiViewModel by viewModels()

  private val requestPermissionLauncher = registerForActivityResult(
      ActivityResultContracts.RequestPermission()
  ) {}

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val userRole by viewModel.userRole.collectAsState()
            
            when (userRole) {
                null -> GheychiApp(onRoleSelected = { viewModel.loginAs(it) })
                "customer" -> CustomerApp(viewModel)
                "salon_admin" -> SalonApp(viewModel)
                "super_admin" -> SuperAdminApp(viewModel)
            }
        }
      }
    }
  }
}
