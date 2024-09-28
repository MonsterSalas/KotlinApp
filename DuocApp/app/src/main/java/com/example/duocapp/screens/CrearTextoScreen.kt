package com.example.duocapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.duocapp.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTextScreen(navController: NavController, authViewModel: AuthViewModel) {
    var textInput by remember { mutableStateOf(TextFieldValue("")) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Crear Texto")
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    label = { Text("Ingrese el texto") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (currentUser != null) {
                            val userId = currentUser.uid
                            val database = FirebaseDatabase.getInstance()
                            val ref = database.getReference("users").child(userId).child("descriptions")

                            val newTextKey = ref.push().key
                            if (newTextKey != null) {
                                ref.child(newTextKey).setValue(textInput.text)
                                    .addOnSuccessListener {
                                        // Mostrar un mensaje de éxito y navegar de regreso
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Texto guardado exitosamente")
                                            navController.popBackStack()
                                        }
                                    }
                                    .addOnFailureListener { error ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Error al guardar el texto: ${error.message}")
                                        }
                                    }
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}
