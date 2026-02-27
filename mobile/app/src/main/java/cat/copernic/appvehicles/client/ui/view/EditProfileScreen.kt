package cat.copernic.appvehicles.client.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit = {}
) {
    // Datos del perfil (mock/temporal para UI)
    var name by remember { mutableStateOf("Anna") }
    var surname by remember { mutableStateOf("Serra") }
    var email by remember { mutableStateOf("anna@correu.com") }
    var phone by remember { mutableStateOf("600123456") }
    var address by remember { mutableStateOf("C/ Exemple, 123") }

    // Foto y documentación (simulados)
    var selectedPhotoName by remember { mutableStateOf<String?>(null) }
    var selectedDocName by remember { mutableStateOf<String?>(null) }

    // Mensajes UI
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar perfil") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // ---------- FOTO ----------
                    Text(
                        text = "Foto de perfil",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Placeholder de foto
                        Surface(
                            modifier = Modifier.size(80.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("Foto")
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Button(
                                onClick = {
                                    // Simulación: "seleccionamos" una foto
                                    selectedPhotoName = "foto_perfil.jpg"
                                    successMessage = null
                                    errorMessage = null
                                }
                            ) {
                                Text("Cambiar foto")
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = selectedPhotoName ?: "Ninguna foto seleccionada",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ---------- DATOS ----------
                    Text(
                        text = "Dades personals",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nom") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = surname,
                        onValueChange = { surname = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Cognoms") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Correu electrònic") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Telèfon") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Adreça") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ---------- DOCUMENTACIÓN ----------
                    Text(
                        text = "Documentació",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Adjunta la documentació necessària (exemple: DNI, carnet, etc.).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            // Simulación: "seleccionamos" un documento
                            selectedDocName = "documentacio.pdf"
                            successMessage = null
                            errorMessage = null
                        }
                    ) {
                        Text("Adjuntar document")
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = selectedDocName ?: "Cap document adjuntat",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ---------- MENSAJES ----------
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (successMessage != null) {
                        Text(
                            text = successMessage!!,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // ---------- GUARDAR ----------
                    Button(
                        onClick = {
                            // Validación mínima (sin complicar)
                            val trimmedName = name.trim()
                            val trimmedEmail = email.trim()

                            if (trimmedName.isBlank()) {
                                successMessage = null
                                errorMessage = "El nom és obligatori"
                                return@Button
                            }

                            val looksLikeEmail = trimmedEmail.contains("@") && trimmedEmail.contains(".")
                            if (trimmedEmail.isBlank() || !looksLikeEmail) {
                                successMessage = null
                                errorMessage = "Correu no vàlid"
                                return@Button
                            }

                            // Simulación de guardado OK
                            errorMessage = null
                            successMessage = "Canvis desats correctament"
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Desar canvis")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun EditProfileScreenPreview() {
    MaterialTheme {
        EditProfileScreen()
    }
}