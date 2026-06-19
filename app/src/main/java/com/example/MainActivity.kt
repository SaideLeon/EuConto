package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.theme.MyApplicationTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.viewmodel.AccountingViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val navController = rememberNavController()
        val viewModel: AccountingViewModel = viewModel()

        Scaffold(
          modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
          NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
          ) {
            composable("dashboard") {
              DashboardScreen(
                viewModel = viewModel,
                onNavigateToEmpresa = { id ->
                  navController.navigate("empresa_detail")
                }
              )
            }
            composable("empresa_detail") {
              EmpresaDetailScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddElemento = { navController.navigate("add_elemento") },
                onNavigateToInventario = { navController.navigate("inventario") },
                onNavigateToBalanco = { navController.navigate("balanco") }
              )
            }
            composable("add_elemento") {
              AddElementoScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
              )
            }
            composable("inventario") {
              InventarioScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
              )
            }
            composable("balanco") {
              BalancoScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
              )
            }
          }
        }
      }
    }
  }
}
