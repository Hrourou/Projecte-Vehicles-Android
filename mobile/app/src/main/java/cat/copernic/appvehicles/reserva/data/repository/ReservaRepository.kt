package cat.copernic.appvehicles.reserva.data.repository

import cat.copernic.appvehicles.reserva.data.api.remote.ReservaApi
import cat.copernic.appvehicles.reserva.data.model.CreateReservaRequest
import cat.copernic.appvehicles.reserva.data.model.ReservaResponse

class ReservaRepository(private val api: ReservaApi) {

    suspend fun crearReserva(request: CreateReservaRequest): ReservaResponse {
        return api.crearReserva(request)
    }
    suspend fun getReservaById(id: Long): ReservaResponse {
        return api.getReservaById(id)
    }

    suspend fun getReservesClient(email: String, asc: Boolean): List<ReservaResponse> {
        val order = if (asc) "asc" else "desc"
        return api.getReservas(email = email, order = order)
    }
}