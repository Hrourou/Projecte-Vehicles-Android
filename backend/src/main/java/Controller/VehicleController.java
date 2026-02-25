/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DTO.VehicleResponseDTO;
import cat.copernic.backendProjecte3.business.VehicleService;
import DTO.VehicleMapper;
import DTO.VehicleResponseDTO;
import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.exceptions.DadesNoTrobadesException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}