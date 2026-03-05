package cat.copernic.appvehicles.client.ui.view

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.core.auth.SessionStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private enum class ProfileMode { LOGIN, RECOVER, REGISTER }

@Composable
fun ProfileEntryScreen() {
    val context = LocalContext.current
    val sessionStore = remember { SessionStore(context) }

    var dni by remember { mutableStateOf<String?>(null) }
    var mode by rememberSaveable { mutableStateOf(ProfileMode.LOGIN) }

    // Cargamos sesión una vez
    LaunchedEffect(Unit) {
        dni = sessionStore.dniFlow().first()
    }

    if (!dni.isNullOrBlank()) {
        // Hay sesión -> RF04
        EditProfileScreen(
            onLoggedOut = {
                dni = null
                mode = ProfileMode.LOGIN
            }
        )
        return
    }

    //No hay sesión -> pantallas auth
    when (mode) {
        ProfileMode.LOGIN -> LoginScreen(
            onLoginSuccess = {
                // IMPORTANTE: depende de dni en sesión.
                // Si tu login real devuelve dni, guárdalo aquí.
                // Ahora mismo NO hay API de login
                //
                // TODO: login real:
                // sessionStore.saveDni(dniFromBackend)
                //
                // De momento no cambiar dni aquí para no inventarlo.
            },
            onNavigateToRecover = { mode = ProfileMode.RECOVER },
            onNavigateToRegister = { mode = ProfileMode.REGISTER }
        )

        ProfileMode.RECOVER -> RecoverPasswordScreen(
            onBackClick = { mode = ProfileMode.LOGIN }
        )

        ProfileMode.REGISTER -> {
            // En tu proyecto el composable se llama RegisterScreen (está en RegistrePantalla.kt)
            cat.copernic.appvehicles.usuariAnonim.ui.view.RegisterScreen(
                onNavigateBack = { mode = ProfileMode.LOGIN },
                onRegisterSuccess = {
                    // Si al registrar obtienes dni, guárdalo (ideal).
                    // sessionStore.saveDni(dniFromBackend)
                    mode = ProfileMode.LOGIN
                }
            )
        }
    }
}