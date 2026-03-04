package cat.copernic.appvehicles.vehicle.data.remote

data class VehicleDto(
    val id: String,
    val marca: String,
    val model: String,
    val variant: String,
    val preuHora: Double
)