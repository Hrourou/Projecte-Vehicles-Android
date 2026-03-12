package cat.copernic.appvehicles.reserva.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.reserva.data.model.ReservaResponse
import cat.copernic.appvehicles.reserva.viewmodel.ReservaViewModel

/**
 * Pantalla per listar l'historial de reserves del client.
 * Implementa control d'accés i gestió d'errors de xarxa per assegurar l'estabilitat visual.
 *
 * @param userEmail Adreça de correu de l'usuari actual. Serveix per autenticar la petició a l'API.
 * @param viewModel ViewModel de la vista que proveeix l'estat i les llistes de dades.
 * @param onBackClick Acció de navegació (tornar enrere).
 * @param onReservaSelected Callback executat en fer clic sobre un element de la llista.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReserveListScreen(
    userEmail: String,
    viewModel: ReservaViewModel,
    onBackClick: () -> Unit = {},
    onReservaSelected: (Long) -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current

    val loading by viewModel.loading.collectAsState()
    val reserves by viewModel.reserves.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Cicle de vida: Refresc automàtic de dades en retornar a la pantalla
    DisposableEffect(lifecycleOwner, userEmail) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME && !isPreview) {
                if (userEmail.isNotBlank()) {
                    viewModel.load(userEmail)
                } else {
                    viewModel.clearReserves()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val statusActive = stringResource(R.string.status_active)
    val statusCancelled = stringResource(R.string.status_cancelled)
    val statusFinished = stringResource(R.string.status_finished)

    fun localizeStatus(raw: String): String {
        return when (raw.trim().uppercase()) {
            "ACTIVA", "ACTIVE" -> statusActive
            "CANCELADA", "CANCELLED", "CANCELED", "CANCEL·LADA" -> statusCancelled
            "FINALITZADA", "FINALIZADA", "FINISHED" -> statusFinished
            else -> raw
        }
    }

    val listToShow: List<ReserveMock> = if (isPreview || userEmail.isBlank()) {
        emptyList()
    } else {
        reserves.map { it.toReserveMock(localizeStatus(it.estat ?: "ACTIVA")) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reservations_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back)) }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (!isPreview && userEmail.isNotBlank()) viewModel.toggleOrder(userEmail)
                        }
                    ) {
                        Icon(Icons.Default.SwapVert, contentDescription = stringResource(R.string.sort_list))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            if (!isPreview && loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (userEmail.isBlank()) {
                // Control d'accés: l'usuari no ha iniciat sessió
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Rounded.Info, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.login_required_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.login_required_desc),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (errorMsg != null) {
                // Control d'errors: fallada de xarxa o de servidor
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Rounded.Warning, contentDescription = "Error", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.error_generic_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${stringResource(R.string.error_load_reservations)}\n$errorMsg",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (listToShow.isEmpty()) {
                // Cas d'ús: el client no disposa de reserves
                Text(
                    text = stringResource(R.string.no_reservations_found),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Llistat de reserves existents
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = listToShow, key = { it.id }) { reserva ->
                        ReserveCard(
                            reserve = reserva,
                            onClick = { onReservaSelected(reserva.id.toLong()) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Adaptador del model de dades per transformar la resposta del backend a la classe de vista.
 */
private fun ReservaResponse.toReserveMock(localizedStatus: String): ReserveMock {
    return ReserveMock(
        id = idReserva.toInt(),
        codi = idReserva.toString(),
        dataInici = dataInici,
        dataFi = dataFi,
        preuTotal = importTotal.toDoubleOrNull() ?: 0.0,
        estat = localizedStatus
    )
}