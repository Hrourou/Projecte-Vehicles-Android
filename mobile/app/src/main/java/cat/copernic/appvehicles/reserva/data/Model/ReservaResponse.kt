package cat.copernic.appvehicles.reserva.data.model

import com.google.gson.annotations.SerializedName

data class ReservaResponse(
    @SerializedName("idReserva")
    val idReserva: Long,

    @SerializedName("dataInici")
    val dataInici: String,

    @SerializedName("dataFi")
    val dataFi: String,

    @SerializedName("clientEmail")
    val clientEmail: String,

    @SerializedName("vehicleMatricula")
    val vehicleMatricula: String,

    @SerializedName("importTotal")
    val importTotal: String,

    @SerializedName("fiancaPagada")
    val fiancaPagada: String,

    // OBLIGUEM A RETROFIT A LLEGIR EL CAMP "estat" DEL JSON DE SPRING BOOT
    @SerializedName("estat")
    val estat: String? = "ACTIVA",

    @SerializedName("vehicleFotoBase64")
    val vehicleFotoBase64: String? = null // Añade esta línea al final de tu data class
)