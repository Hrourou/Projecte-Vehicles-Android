package cat.copernic.appvehicles.reserva.data.model

data class CreateReservaRequest(
    val emailClient: String,
    val matricula: String,
    val dataInici: String,
    val dataFi: String,
    val userName: String
)
