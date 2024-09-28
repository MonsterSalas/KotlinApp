package com.example.duocapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.duocapp.AuthState
import com.example.duocapp.AuthViewModel
import com.example.duocapp.Routes
import kotlinx.coroutines.launch

@Composable
fun RegistrarseScreen(navController: NavController, authViewModel: AuthViewModel) {
    var pass by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authState by authViewModel.authState.observeAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
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
        Text(text = "Regístrate", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text(text = "Nombre") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text(text = "Apellido") }
        )
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
                        authViewModel.signup(correo, pass)
                        // Aquí puedes agregar lógica adicional para guardar nombre y apellido
                        // Por ejemplo, usando Firestore o Realtime Database
                    }
                } else {
                    Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp)
        ) {
            Text(text = "Registrarse")
        }
    }
}