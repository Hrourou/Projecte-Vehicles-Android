/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.business;

import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.EstatVehicle;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import cat.copernic.backendProjecte3.repository.ReservaRepository;
import cat.copernic.backendProjecte3.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author manel
 */
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }
    
    public List<Vehicle> obtenirTots() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> cercarVehiclesDisponibles(LocalDate inici, LocalDate fi, TipusVehicle tipus) {

    long dies = java.time.temporal.ChronoUnit.DAYS.between(inici, fi);

    if (dies < 2 || dies > 15) {
        throw new IllegalArgumentException("El rang de dies ha de ser entre 2 i 15 dies");
    }

    return vehicleRepository.findDisponibles(inici, fi, tipus);
}
}