package cat.copernic.backendProjecte3.controller;

import cat.copernic.backendProjecte3.dto.VehicleResponseDTO;
import cat.copernic.backendProjecte3.business.VehicleService;
import cat.copernic.backendProjecte3.dto.VehicleMapper;
import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import cat.copernic.backendProjecte3.exceptions.DadesNoTrobadesException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * RF90 - Llistar vehicles
     */
    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> llistarVehicles()
            throws DadesNoTrobadesException {

        List<Vehicle> vehicles = vehicleService.obtenirTots();

        if (vehicles.isEmpty()) {
            throw new DadesNoTrobadesException("No hi ha vehicles disponibles");
        }

        List<VehicleResponseDTO> response = vehicles.stream()
                .map(VehicleMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * RF - Cercar vehicles disponibles per dates
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<VehicleResponseDTO>> cercarVehiclesDisponibles(
            @RequestParam String inici,
            @RequestParam String fi,
            @RequestParam(required = false) TipusVehicle tipus,
            @RequestParam(required = false) String codiPostal
    ) {

        LocalDate dataInici = LocalDate.parse(inici);
        LocalDate dataFi = LocalDate.parse(fi);

        List<Vehicle> vehicles = vehicleService.cercarVehiclesDisponibles(
                dataInici,
                dataFi,
                tipus
        );

        List<VehicleResponseDTO> response = vehicles.stream()
                .map(VehicleMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}