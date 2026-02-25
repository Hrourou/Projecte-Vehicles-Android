package cat.copernic.appvehicles.usuariAnonim.ui.view

import androidx.compose.animation.ExperimentalAnimationApi
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var uiState by remember { mutableStateOf(RegisterUiState()) }
    val scrollState = rememberScrollState()

    // Controlamos en qué paso estamos (1, 2 o 3)
    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 3

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registre - Pas $currentStep de $totalSteps") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep-- else onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tornar enrere")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        // Mover los botones a la parte inferior de la pantalla fija
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(onClick = { currentStep-- }) {
                            Text("Enrere")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp)) // Espaciador para equilibrar si no hay botón
                    }

                    if (currentStep < totalSteps) {
                        Button(onClick = { currentStep++ }) {
                            Text("Següent")
                        }
                    } else {
                        Button(onClick = onRegisterSuccess) {
                            Text("Finalitzar")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // El contenido principal sigue teniendo scroll por si el teclado se abre
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Usamos un simple 'when' para mostrar un bloque de campos u otro
            when (currentStep) {
                1 -> Pas1DadesPersonals(uiState) { uiState = it }
                2 -> Pas3DadesContacte(uiState) { uiState = it }
                3 -> Pas2DadesConduccio(uiState) { uiState = it }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- BLOQUES DE CONTENIDO DIVIDIDOS ---

@Composable
fun Pas1DadesPersonals(state: RegisterUiState, onStateChange: (RegisterUiState) -> Unit) {
    Text("Dades Personals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    ReusableTextField(value = state.nomComplet, onValueChange = { onStateChange(state.copy(nomComplet = it)) }, label = "Nom complet")
    ReusableTextField(value = state.numeroIdentificacio, onValueChange = { onStateChange(state.copy(numeroIdentificacio = it)) }, label = "Número d'identificació")
    OutlinedTextField(
        value = state.dataCaducitatId, onValueChange = { }, label = { Text("Data caducitat") },
        modifier = Modifier.fillMaxWidth(), readOnly = true, trailingIcon = { Icon(Icons.Default.DateRange, "Seleccionar data") }
    )
    ImageUploadButton(label = "Pujar foto identificació") { /* TODO */ }
}

@Composable
fun Pas2DadesConduccio(state: RegisterUiState, onStateChange: (RegisterUiState) -> Unit) {
    Text("Dades de Conducció i Pagament", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    ReusableTextField(value = state.tipusLlicencia, onValueChange = { onStateChange(state.copy(tipusLlicencia = it)) }, label = "Tipus de llicència")
    OutlinedTextField(
        value = state.dataCaducitatLlicencia, onValueChange = { }, label = { Text("Data caducitat llicència") },
        modifier = Modifier.fillMaxWidth(), readOnly = true, trailingIcon = { Icon(Icons.Default.DateRange, "Seleccionar data") }
    )
    ImageUploadButton(label = "Pujar foto llicència") { /* TODO */ }
    ReusableTextField(value = state.numeroTargetaCredit, onValueChange = { onStateChange(state.copy(numeroTargetaCredit = it)) }, label = "Targeta de crèdit")
}

@Composable
fun Pas3DadesContacte(state: RegisterUiState, onStateChange: (RegisterUiState) -> Unit) {
    Text("Dades de Contacte i Accés", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    ReusableTextField(value = state.adreca, onValueChange = { onStateChange(state.copy(adreca = it)) }, label = "Adreça")
    ReusableTextField(value = state.nacionalitat, onValueChange = { onStateChange(state.copy(nacionalitat = it)) }, label = "Nacionalitat")
    ReusableTextField(value = state.email, onValueChange = { onStateChange(state.copy(email = it)) }, label = "Email (Usuari)")
    ReusableTextField(value = state.password, onValueChange = { onStateChange(state.copy(password = it)) }, label = "Contrasenya", isPassword = true)
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