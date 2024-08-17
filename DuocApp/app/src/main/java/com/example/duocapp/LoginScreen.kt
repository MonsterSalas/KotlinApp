package com.example.duocapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.provider.FontsContractCompat.Columns
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController){
    var pass by remember {
        mutableStateOf("")
    }
    var correo by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.speak), contentDescription ="Imagen login",
            modifier = Modifier.size(200.dp))
        Text(text = "Bienvenido" , fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = correo, onValueChange = {
            correo =  it
        },label = {
            Text(text = "Correo")
        } )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = pass, onValueChange = {
            pass = it
        },label = {
            Text(text = "Contrasennia")
        }, visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Iniciar Sesion")

        }
        Spacer(modifier = Modifier.height(32.dp))
        TextButton(onClick = { /*TODO*/ }) {
            Text(text = "Olvidaste tu contrasennia?")
        }
        TextButton(onClick = {
            navController.navigate(Routes.RegistrarScreen)
        }) {
            Text(text = "Quiero registrarme!")
        }

    }

}