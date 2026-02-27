package cat.copernic.appvehicles.reserva.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.appvehicles.reserva.data.model.ReservaResponse
import cat.copernic.appvehicles.reserva.data.repository.ReservaRepository
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

    fun toggleOrder(email: String) {
        _asc.value = !_asc.value
        load(email)
    }

    fun load(email: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _reserves.value = repo.getReservesClient(email, _asc.value)
            } catch (e: Exception) {
                _reserves.value = emptyList()
            }
            _loading.value = false
        }
    }
}