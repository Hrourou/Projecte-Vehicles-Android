package cat.copernic.appvehicles.vehicle.ui.view

/**
 * Model temporal (Mock)
 * Més endavant vindrà de la capa Domain
 */
data class VehicleMock(
    val id: Int,
    val marca: String,
    val model: String,
    val variant: String,
    val preuHora: Double
)