package cat.copernic.appvehicles.reserva.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.appvehicles.reserva.data.model.CreateReservaRequest
import cat.copernic.appvehicles.reserva.data.model.ReservaResponse
import cat.copernic.appvehicles.reserva.data.repository.ReservaRepository
import cat.copernic.appvehicles.reserva.data.model.CancelReservaResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservaViewModel(private val repo: ReservaRepository) : ViewModel() {

    private val _reserves = MutableStateFlow<List<ReservaResponse>>(emptyList())
    val reserves = _reserves.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _asc = MutableStateFlow(false)
    val asc = _asc.asStateFlow()

    // 🔥 NOU: Emmagatzema els errors del servidor per poder-los ensenyar
    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg = _errorMsg.asStateFlow()

    private val _creationResult = MutableStateFlow<Result<ReservaResponse>?>(null)
    val creationResult = _creationResult.asStateFlow()

    private val _reservaDetail = MutableStateFlow<ReservaResponse?>(null)
    val reservaDetail = _reservaDetail.asStateFlow()

    private val _cancelResult = MutableStateFlow<Result<CancelReservaResponse>?>(null)
    val cancelResult = _cancelResult.asStateFlow()

    fun clearCreationResult() {
        _creationResult.value = null
    }

    // 🔥 NOU: Neteja la llista quan fem logout
    fun clearReserves() {
        _reserves.value = emptyList()
        _errorMsg.value = null
    }

    fun cancelReserva(id: Long, userName: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repo.cancelReserva(id, userName)
                _cancelResult.value = Result.success(response)
                // Canviem l'estat localment al instant
                _reservaDetail.value = _reservaDetail.value?.copy(estat = "CANCELADA")
            } catch (e: Exception) {
                _cancelResult.value = Result.failure(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearCancelResult() {
        _cancelResult.value = null
    }

    fun loadReservaDetalle(id: Long) {
        viewModelScope.launch {
            try {
                _reservaDetail.value = repo.getReservaById(id)
            } catch (e: Exception) {
                _reservaDetail.value = null
            }
        }
    }

    fun toggleOrder(email: String) {
        _asc.value = !_asc.value
        load(email)
    }

    fun load(email: String) {
        viewModelScope.launch {
            _loading.value = true
            _errorMsg.value = null // Netejem errors antics
            try {
                _reserves.value = repo.getReservesClient(email, _asc.value)
            } catch (e: Exception) {
                _reserves.value = emptyList()
                // 🔥 NOU: Si peta, guardem l'error en comptes d'amagar-lo!
                _errorMsg.value = "Error de connexió: " + e.message
            }
            _loading.value = false
        }
    }

    fun crearReserva(request: CreateReservaRequest) {
        viewModelScope.launch {
            _loading.value = true
            _creationResult.value = try {
                val result = repo.crearReserva(request)
                Result.success(result)
            } catch (e: Exception) {
                Result.failure(e)
            }
            _loading.value = false
        }
    }
}