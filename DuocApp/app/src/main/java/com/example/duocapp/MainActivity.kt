package com.example.duocapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.duocapp.screens.HomeScreen
import com.example.duocapp.screens.LoginScreen
import com.example.duocapp.screens.RegistrarseScreen
import com.example.duocapp.screens.VozATexto

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Routes.LoginScreen, builder = {
                composable(Routes.LoginScreen){
                    LoginScreen(navController)
                }
                composable(Routes.RegistrarScreen,){
                    RegistrarseScreen(navController)
                }
                composable(Routes.HomeScreen,){
                    HomeScreen(navController)
                }
                composable(Routes.VozATexto,){
                    VozATexto(navController)
                }
            } )
        }
    }
}

