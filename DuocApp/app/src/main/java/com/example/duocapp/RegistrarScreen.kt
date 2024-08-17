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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegistrarseScreen() {

    var pass by remember {
        mutableStateOf("")
    }
    var correo by remember {
        mutableStateOf("")
    }
    var nombre by remember {
        mutableStateOf("")
    }
    var apellido by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Registrate" , fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = nombre, onValueChange = {
            nombre =  it
        },label = {
            Text(text = "Nombre")
        } )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = apellido, onValueChange = {
            apellido =  it
        },label = {
            Text(text = "Apellido")
        } )
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
            Text(text = "Registrarse")

        }

    }
}