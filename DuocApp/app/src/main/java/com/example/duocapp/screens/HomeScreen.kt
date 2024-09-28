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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.duocapp.AuthViewModel
import com.example.duocapp.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Variables para consumir los textos desde Firebase
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var userTexts by remember { mutableStateOf<List<String>>(emptyList()) }

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
        "Necesito ayuda",
        "Gracias",
        "Por favor",
        "Adiós"
    )

    // Obtener el usuario autenticado
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Cargar textos desde Firebase
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val userId = currentUser.uid
            val database = FirebaseDatabase.getInstance()
            val ref = database.getReference("users").child(userId).child("descriptions")

            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val texts = mutableListOf<String>()
                    for (childSnapshot in snapshot.children) {
                        val text = childSnapshot.getValue(String::class.java)
                        if (text != null) {
                            texts.add(text)
                        }
                    }
                    userTexts = texts
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    errorMessage = error.message
                    isLoading = false
                }
            })
        } else {
            isLoading = false
            errorMessage = "Usuario no autenticado"
        }
    }

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
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Navegar a la pantalla de creación de texto (CreateTextScreen)
                            navController.navigate(Routes.CreateTextScreen)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Crear Texto"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Esto me debe llevar a la pestaña de creación de texto (VozATexto)
                    navController.navigate(Routes.VozATexto)
                },
                modifier = Modifier
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = errorMessage ?: "Unknown error")
            }
        } else {
            // Combinar los textos por defecto con los textos del usuario
            val allTexts = defaultTexts + userTexts
            var editingIndex by remember { mutableStateOf(-1) }
            var editingText by remember { mutableStateOf("") }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(allTexts) { index, text ->
                    TextCard(
                        text = text,
                        isSelected = selectedText == text, // Cambia el estado si es el texto seleccionado
                        onClick = {
                            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                            selectedText = if (selectedText == text) null else text // Cambia la selección
                        },
                        onDelete = {
                            // Solo eliminar si el texto no es un texto por defecto
                            if (index >= defaultTexts.size) {
                                val textToDelete = userTexts[index - defaultTexts.size]

                                // Eliminar el texto de Firebase
                                val userId = currentUser?.uid
                                val database = FirebaseDatabase.getInstance()
                                val ref = database.getReference("users").child(userId!!).child("descriptions")

                                // Encuentra el nodo correspondiente al texto y elimínalo
                                ref.orderByValue().equalTo(textToDelete).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (childSnapshot in snapshot.children) {
                                            childSnapshot.ref.removeValue()
                                        }
                                        // Eliminar el texto de la lista local
                                        userTexts = userTexts.toMutableList().apply {
                                            removeAt(index - defaultTexts.size)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        // Manejo de errores
                                    }
                                })
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TextCard(text: String, isSelected: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f) // Asegura que el texto tenga el mismo espacio
                )
                IconButton(onClick = { onDelete() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar texto"
                    )
                }
            }
        }
    }
}