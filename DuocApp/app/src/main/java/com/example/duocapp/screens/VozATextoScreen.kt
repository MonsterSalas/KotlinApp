package com.example.duocapp.screens

import android.content.Intent
import android.speech.RecognizerIntent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import android.app.Activity
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.ui.res.painterResource
import com.example.duocapp.AuthViewModel
import com.example.duocapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VozATexto(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current

    // Variable para almacenar el texto reconocido por voz
    var recognizedText by remember { mutableStateOf("") }

    // Intent para el reconocimiento de voz
    val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    // Lanzador para la actividad de reconocimiento de voz
    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                recognizedText = results[0] // Almacena el texto reconocido
            }
        }
    }

    // Referencia al usuario autenticado
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voz a Texto") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    speechLauncher.launch(speechRecognizerIntent) // Lanza el reconocedor de voz
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_mic_24),
                    contentDescription = "Microfono",
                    modifier = Modifier.size(24.dp) // Ajusta el tamaño
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Muestra el texto reconocido por voz
                Text(
                    text = if (recognizedText.isEmpty()) "Presiona el micrófono y habla y muestra el texto" else recognizedText,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(16.dp)
                )

                // Botón para guardar el texto en Firebase
                Button(
                    onClick = {
                        if (currentUser != null) {
                            val userId = currentUser.uid
                            val database = FirebaseDatabase.getInstance()
                            val ref = database.getReference("users").child(userId).child("descriptions")
                            val key = ref.push().key // Genera una nueva clave para la descripción

                            key?.let {
                                ref.child(it).setValue(recognizedText)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Descripción guardada correctamente", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Guardar Descripción")
                }
            }
        }
    }
}