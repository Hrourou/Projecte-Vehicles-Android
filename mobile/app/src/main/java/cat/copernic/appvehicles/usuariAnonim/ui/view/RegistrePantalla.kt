package cat.copernic.appvehicles.usuariAnonim.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview


data class RegisterUiState(
    val nomComplet: String = "",
    val numeroIdentificacio: String = "",
    val dataCaducitatId: String = "",
    val tipusLlicencia: String = "",
    val dataCaducitatLlicencia: String = "",
    val numeroTargetaCredit: String = "",
    val adreca: String = "",
    val nacionalitat: String = "",
    val email: String = "",
    val password: String = ""
)

// ---------------------------------------------------------------------------
// COMPOSABLES REUTILITZABLES (RN25)
// ---------------------------------------------------------------------------

@Composable
fun ReusableTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
fun ImageUploadButton(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = "Pujar $label",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// PANTALLA PRINCIPAL
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    // Simulació de l'estat que vindria del ViewModel
    var uiState by remember { mutableStateOf(RegisterUiState()) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nou Registre") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tornar enrere")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Dades Personals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            ReusableTextField(
                value = uiState.nomComplet,
                onValueChange = { uiState = uiState.copy(nomComplet = it) },
                label = "Nom complet"
            )

            ReusableTextField(
                value = uiState.numeroIdentificacio,
                onValueChange = { uiState = uiState.copy(numeroIdentificacio = it) },
                label = "Número d'identificació (DNI/Passaport)"
            )

            // Fals DatePicker (per no complicar la UI innecessàriament sense llibreries extra)
            OutlinedTextField(
                value = uiState.dataCaducitatId,
                onValueChange = { },
                label = { Text("Data caducitat identificació") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Seleccionar data") }
            )

            ImageUploadButton(label = "Pujar foto identificació") { /* TODO: Obre galeria */ }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Dades de Conducció i Pagament",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            ReusableTextField(
                value = uiState.tipusLlicencia,
                onValueChange = { uiState = uiState.copy(tipusLlicencia = it) },
                label = "Tipus de llicència de conduir"
            )

            OutlinedTextField(
                value = uiState.dataCaducitatLlicencia,
                onValueChange = { },
                label = { Text("Data caducitat llicència") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Seleccionar data") }
            )

            ImageUploadButton(label = "Pujar foto llicència") { /* TODO: Obre galeria */ }

            ReusableTextField(
                value = uiState.numeroTargetaCredit,
                onValueChange = { uiState = uiState.copy(numeroTargetaCredit = it) },
                label = "Número de targeta de crèdit"
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Dades de Contacte i Accés",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            ReusableTextField(
                value = uiState.adreca,
                onValueChange = { uiState = uiState.copy(adreca = it) },
                label = "Adreça del domicili"
            )

            ReusableTextField(
                value = uiState.nacionalitat,
                onValueChange = { uiState = uiState.copy(nacionalitat = it) },
                label = "Nacionalitat"
            )

            ReusableTextField(
                value = uiState.email,
                onValueChange = { uiState = uiState.copy(email = it) },
                label = "Correu electrònic (Usuari)"
            )

            ReusableTextField(
                value = uiState.password,
                onValueChange = { uiState = uiState.copy(password = it) },
                label = "Contrasenya",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botó principal d'acció
            Button(
                onClick = onRegisterSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Registrar-se", fontSize = MaterialTheme.typography.titleMedium.fontSize)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Register Screen Preview"
)
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen(
            onNavigateBack = {},
            onRegisterSuccess = {}
        )
    }
}