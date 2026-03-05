package cat.copernic.appvehicles.client.ui.view

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.core.auth.SessionStore
import cat.copernic.appvehicles.usuariAnonim.data.repository.AuthRepository
import cat.copernic.appvehicles.usuariAnonim.ui.view.RegisterScreen
import cat.copernic.appvehicles.usuariAnonim.ui.viewmodel.RegisterViewModel
import cat.copernic.appvehicles.usuariAnonim.ui.viewmodel.RegisterViewModelFactory
import kotlinx.coroutines.flow.first

private enum class ProfileMode { LOGIN, RECOVER, REGISTER }

@Composable
fun ProfileEntryScreen(
    authRepository: AuthRepository
) {
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

    // No hay sesión -> pantallas auth
    when (mode) {
        ProfileMode.LOGIN -> LoginScreen(
            onLoginSuccess = {
                // TODO: cuando tengas login real, guarda dni en SessionStore:
                // sessionStore.saveDni(dniFromBackend)
            },
            onNavigateToRecover = { mode = ProfileMode.RECOVER },
            onNavigateToRegister = { mode = ProfileMode.REGISTER }
        )

        ProfileMode.RECOVER -> RecoverPasswordScreen(
            onBackClick = { mode = ProfileMode.LOGIN }
        )

        ProfileMode.REGISTER -> {
            val registerViewModel: RegisterViewModel = viewModel(
                factory = RegisterViewModelFactory(authRepository)
            )

            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateBack = { mode = ProfileMode.LOGIN },
                onRegisterSuccess = {
                    // Si tras registrar guardas DNI en SessionStore, aquí ya entrarías al perfil.
                    mode = ProfileMode.LOGIN
                }
            )
        }
    }
}