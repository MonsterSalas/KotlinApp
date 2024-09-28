package com.example.duocapp.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.duocapp.AuthState
import com.example.duocapp.AuthViewModel
import com.example.duocapp.R
import com.example.duocapp.Routes
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    val context = LocalContext.current

    val authState by authViewModel.authState.observeAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.HomeScreen) {
                    popUpTo(Routes.LoginScreen) { inclusive = true }
                }
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.speak),
            contentDescription = "Imagen login",
            modifier = Modifier.size(200.dp)
        )
        Text(text = "Speak For Me", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text(text = "Correo") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text(text = "Contraseña") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (correo.isNotEmpty() && pass.isNotEmpty()) {
                    scope.launch {
                        authViewModel.login(correo, pass)
                    }
                } else {
                    Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp)
        ) {
            Text(text = "Iniciar Sesión")
        }
        Spacer(modifier = Modifier.height(32.dp))
        TextButton(
            onClick = { /* TODO: Implementar recuperación de contraseña */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text(text = "¿Olvidaste tu contraseña?", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = {
                navController.navigate(Routes.RegistrarScreen)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text(text = "¡Quiero registrarme!", fontSize = 18.sp)
        }
    }
}