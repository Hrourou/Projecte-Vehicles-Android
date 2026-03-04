package cat.copernic.appvehicles.model

import cat.copernic.appvehicles.vehicle.data.remote.VehicleDto

fun VehicleDto.toDomain(): Vehicle {
    return Vehicle(
        id = id,
        marca = marca,
        model = model,
        variant = variant,
        preuHora = preuHora
    )
}