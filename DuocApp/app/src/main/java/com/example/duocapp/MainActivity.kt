package com.example.duocapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.duocapp.screens.CreateTextScreen
import com.example.duocapp.screens.HomeScreen
import com.example.duocapp.screens.LoginScreen
import com.example.duocapp.screens.RegistrarseScreen
import com.example.duocapp.screens.VozATexto

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AppNavigation(navController, authViewModel)
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, authViewModel: AuthViewModel) {
    val authState by authViewModel.authState.observeAsState()

    NavHost(navController = navController, startDestination = Routes.LoginScreen) {
        composable(Routes.LoginScreen) {
            LoginScreen(navController, authViewModel)
        }
        composable(Routes.RegistrarScreen) {
            RegistrarseScreen(navController, authViewModel)
        }
        composable(Routes.HomeScreen) {
            AuthenticatedRoute(authState, navController) {
                HomeScreen(navController, authViewModel)
            }
        }
        composable(Routes.VozATexto) {
            AuthenticatedRoute(authState, navController) {
                VozATexto(navController, authViewModel)
            }
        }
        composable(Routes.CreateTextScreen) {
            AuthenticatedRoute(authState, navController) {
                CreateTextScreen(navController, authViewModel)
            }
        }
    }
}

@Composable
fun AuthenticatedRoute(
    authState: AuthState?,
    navController: NavController,
    content: @Composable () -> Unit
) {
    when (authState) {
        is AuthState.Authenticated -> content()
        is AuthState.Unauthenticated -> {
            // Redirigir a la pantalla de inicio de sesión
            LaunchedEffect(Unit) {
                navController.navigate(Routes.LoginScreen) {
                    popUpTo(Routes.LoginScreen) { inclusive = true }
                }
            }
        }
        else -> {
            // Mostrar un indicador de carga o manejar otros estados
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

