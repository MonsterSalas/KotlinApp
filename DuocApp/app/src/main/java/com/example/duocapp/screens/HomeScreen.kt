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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.duocapp.AuthViewModel
import com.example.duocapp.R
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

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var userTexts by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedText by remember { mutableStateOf<String?>(null) }
    var editingIndex by remember { mutableStateOf(-1) }
    var editingText by remember { mutableStateOf("") }

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

    val defaultTexts = listOf(
        "Hola, ¿cómo estás?",
        "Necesito ayuda",
        "Gracias",
        "Por favor",
        "Adiós"
    )

    val currentUser = FirebaseAuth.getInstance().currentUser

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
                    navController.navigate(Routes.VozATexto)
                },
                modifier = Modifier
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_mic_24),
                    contentDescription = "Microfono",
                    modifier = Modifier.size(24.dp) // Ajusta el tamaño
                )
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
            val allTexts = defaultTexts + userTexts

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(allTexts) { index, text ->
                    TextCard(
                        text = text,
                        isSelected = selectedText == text,
                        isEditing = index == editingIndex,
                        editingText = if (index == editingIndex) editingText else text,
                        onClick = {
                            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                            selectedText = if (selectedText == text) null else text
                        },
                        onDelete = {
                            if (index >= defaultTexts.size) {
                                val textToDelete = userTexts[index - defaultTexts.size]
                                val userId = currentUser?.uid
                                val database = FirebaseDatabase.getInstance()
                                val ref = database.getReference("users").child(userId!!).child("descriptions")

                                ref.orderByValue().equalTo(textToDelete).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (childSnapshot in snapshot.children) {
                                            childSnapshot.ref.removeValue()
                                        }
                                        userTexts = userTexts.toMutableList().apply {
                                            removeAt(index - defaultTexts.size)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        // Manejo de errores
                                    }
                                })
                            }
                        },
                        onEdit = {
                            if (index >= defaultTexts.size) {
                                editingIndex = index
                                editingText = text
                            }
                        },
                        onEditingTextChange = { newText ->
                            editingText = newText
                        },
                        onSaveEdit = {
                            if (index >= defaultTexts.size) {
                                val userId = currentUser?.uid
                                val database = FirebaseDatabase.getInstance()
                                val ref = database.getReference("users").child(userId!!).child("descriptions")

                                ref.orderByValue().equalTo(text).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (childSnapshot in snapshot.children) {
                                            childSnapshot.ref.setValue(editingText)
                                        }
                                        userTexts = userTexts.toMutableList().apply {
                                            set(index - defaultTexts.size, editingText)
                                        }
                                        editingIndex = -1
                                        editingText = ""
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        // Manejo de errores
                                    }
                                })
                            }
                        },
                        onCancelEdit = {
                            editingIndex = -1
                            editingText = ""
                        }
                    )
                }
                // Añadir un elemento adicional al final de la lista
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun TextCard(
    text: String,
    isSelected: Boolean,
    isEditing: Boolean,
    editingText: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onEditingTextChange: (String) -> Unit,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit
) {
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
            if (isEditing) {
                TextField(
                    value = editingText,
                    onValueChange = onEditingTextChange,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onCancelEdit) {
                        Text("Cancelar")
                    }
                    TextButton(onClick = onSaveEdit) {
                        Text("Guardar")
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar texto"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar texto"
                        )
                    }
                }
            }
        }
    }
}