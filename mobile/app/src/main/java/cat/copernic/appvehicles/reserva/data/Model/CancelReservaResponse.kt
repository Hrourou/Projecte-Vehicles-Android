package cat.copernic.appvehicles.reserva.data.model

data class CancelReservaResponse(
    val idReserva: Long,
    val refundAmount: String?, // puede ser nulo si no hay reembolso
    val message: String
)