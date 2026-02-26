package cat.copernic.appvehicles.reserva.data.model

import com.google.gson.annotations.SerializedName

data class ReservaResponse(
    @SerializedName("idReserva")
    val idReserva: Long,
    val dataInici: String,
    val dataFi: String,
    val clientEmail: String,
    val vehicleMatricula: String,
    val importTotal: String,
    val fiancaPagada: String
)