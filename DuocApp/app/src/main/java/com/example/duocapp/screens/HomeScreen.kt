package com.example.duocapp.screens


import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    //Varibales para futuro consumo de api para traer texto
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Variable para almacenar el texto seleccionado actualmente
    var selectedText by remember { mutableStateOf<String?>(null) }

    // Inicializar Text-to-Speech
    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = Locale("es", "ES")
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }
        ttsInstance
    }

    // Lista de textos por defecto
    val defaultTexts = listOf(
        "Hola, ¿cómo estás?",
        "¿Necesitas ayuda?",
        "Gracias",
        "Por favor",
        "Adiós"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Inicio")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    //Esto me debe llevar a la pestannia de creacion de texto
                },
                modifier = Modifier
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(defaultTexts.size) { index ->
                val text = defaultTexts[index]
                TextCard(
                    text = text,
                    isSelected = selectedText == text, // Cambia el estado si es el texto seleccionado
                    onClick = {
                        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                        selectedText = if (selectedText == text) null else text // Cambia la selección
                    }
                )
            }
        }
    }
}

@Composable
fun TextCard(text: String, isSelected: Boolean, onClick: () -> Unit) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.LightGray else MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f) // Asegura que el texto tenga el mismo espacio
                )
                Spacer(modifier = Modifier.width(8.dp)) // Espacio entre el texto y el círculo
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = if (isSelected) Color.Green else Color.Transparent,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}
