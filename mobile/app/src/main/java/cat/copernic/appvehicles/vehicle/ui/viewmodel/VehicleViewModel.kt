package cat.copernic.appvehicles.vehicle.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.appvehicles.model.Vehicle
import cat.copernic.appvehicles.vehicle.data.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VehicleViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadVehicles() {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.getVehicles()

            result.fold(
                onSuccess = { vehicleResponseList ->
                    _vehicles.value = vehicleResponseList.map { response ->

                        // Protecció contra nuls (si el backend no envia marca, posem text per defecte)
                        val marcaSafe = response.marca ?: "Sense marca"
                        val modelSafe = response.model ?: "Sense model"
                        val variantSafe = response.variant ?: ""
                        val preuHoraSafe = response.preuHora ?: 0.0

                        Vehicle(
                            id = response.matricula,
                            marca = marcaSafe,
                            model = modelSafe,
                            variant = variantSafe,
                            preuHora = preuHoraSafe
                        )
                    }
                },
                onFailure = {
                    _vehicles.value = emptyList()
                }
            )

            _isLoading.value = false
        }
    }
}