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
import androidx.compose.ui.res.painterResource
import com.example.duocapp.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VozATexto(navController: NavController) {
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
            }
        }
    }
}
