package cat.copernic.appvehicles.client.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoverPasswordScreen(
    onBackClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar contraseña") },

                // 🔙 Botón atrás
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

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = "¿Has olvidado tu contraseña?",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Introduce tu correo y te enviaremos un enlace para restablecerla.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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

                    Button(
                        onClick = {
                            focusManager.clearFocus()

                            val trimmed = email.trim()

                            if (trimmed.isBlank()) {
                                successMessage = null
                                errorMessage = "Introduce tu correo"
                                return@Button
                            }

                            val looksLikeEmail =
                                trimmed.contains("@") && trimmed.contains(".")

                            if (!looksLikeEmail) {
                                successMessage = null
                                errorMessage = "Correo no válido"
                                return@Button
                            }

                            errorMessage = null
                            successMessage =
                                "Si el correo existe, recibirás un enlace en unos minutos."
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = email.isNotBlank()
                    ) {
                        Text("Enviar enlace")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun RecoverPasswordScreenPreview() {
    MaterialTheme {
        RecoverPasswordScreen()
    }
}