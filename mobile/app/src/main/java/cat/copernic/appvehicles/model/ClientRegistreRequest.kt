package cat.copernic.appvehicles.model

/**
 * Data class que representa el cuerpo JSON que se enviará al Backend.
 * Debe coincidir campo por campo con ClientRegistreDTO.java
 */
data class ClientRegisterRequest(
    val email: String,
    val password: String,
    val nomComplet: String,

    // Datos de Identificación
    val dni: String,
    val dataCaducitatDni: String, // Formato "yyyy-MM-dd"
    val imatgeDni: String,        // URL o Base64
    val nacionalitat: String,
    val adreca: String,

    // Datos de Conducción
    val tipusCarnetConduir: String,
    val dataCaducitatCarnet: String, // Formato "yyyy-MM-dd"
    val imatgeCarnet: String,        // URL o Base64

    // Datos Económicos
    val numeroTargetaCredit: String
)
