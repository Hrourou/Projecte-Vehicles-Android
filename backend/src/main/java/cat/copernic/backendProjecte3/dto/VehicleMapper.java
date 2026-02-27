package cat.copernic.backendProjecte3.dto;

import cat.copernic.backendProjecte3.entities.Vehicle;

public class VehicleMapper {

    public static VehicleResponseDTO toDTO(Vehicle vehicle) {

        return new VehicleResponseDTO(
                vehicle.getMatricula(),
                vehicle.getMarca(),
                vehicle.getModel(),
                vehicle.getVariant(),
                vehicle.getFotoUrl(),
                vehicle.getPotencia(),
                vehicle.getColor(),
                vehicle.getLimitQuilometratge(),
                vehicle.getPreuHora(),
                vehicle.getFiancaEstandard(),
                vehicle.getMinDiesLloguer(),
                vehicle.getMaxDiesLloguer()
        );
    }
}