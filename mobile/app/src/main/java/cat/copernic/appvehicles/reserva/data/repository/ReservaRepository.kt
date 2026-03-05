package cat.copernic.appvehicles.reserva.data.repository

import cat.copernic.appvehicles.reserva.data.api.remote.ReservaApi
import cat.copernic.appvehicles.reserva.data.model.CancelReservaResponse
import cat.copernic.appvehicles.reserva.data.model.CreateReservaRequest
import cat.copernic.appvehicles.reserva.data.model.ReservaResponse
import retrofit2.HttpException

class ReservaRepository(private val api: ReservaApi) {

    // Fem una crida a l'API per crear una reserva
    suspend fun crearReserva(request: CreateReservaRequest): ReservaResponse {
        try {
            return api.crearReserva(request)
        } catch (e: HttpException) {
            // Llegim el missatge d'error real del backend en comptes del "HTTP 400"
            val msg = e.response()?.errorBody()?.string()
            throw Exception(msg ?: "Error al crear la reserva")
        }
    }

    // Obtenir el detall
    suspend fun getReservaById(id: Long): ReservaResponse {
        return api.getReservaById(id)
    }

    // L'ERROR 409 S'ARREGLA AQUÍ: Llegim el text de l'error del Spring Boot
    suspend fun cancelReserva(id: Long, userName: String): CancelReservaResponse {
        try {
            return api.cancelReserva(id, userName)
        } catch (e: HttpException) {
            val msg = e.response()?.errorBody()?.string()
            throw Exception(msg ?: "Error en anul·lar la reserva")
        }
    }

    // Obtenir la llista
    suspend fun getReservesClient(email: String, asc: Boolean): List<ReservaResponse> {
        val order = if (asc) "asc" else "desc"
        return api.getReservas(email = email, order = order)
    }
}