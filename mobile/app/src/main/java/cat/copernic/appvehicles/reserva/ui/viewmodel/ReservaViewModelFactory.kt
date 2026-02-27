package cat.copernic.appvehicles.reserva.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cat.copernic.appvehicles.reserva.data.repository.ReservaRepository

class ReservaViewModelFactory(
    private val repo: ReservaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReservaViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}