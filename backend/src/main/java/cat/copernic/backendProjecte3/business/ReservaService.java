/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.business;

import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.entities.Reserva;
import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.EstatVehicle;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.exceptions.AccesDenegatException;
import cat.copernic.backendProjecte3.exceptions.DadesNoTrobadesException;
import cat.copernic.backendProjecte3.exceptions.ReservaDatesNoValidsException;
import cat.copernic.backendProjecte3.exceptions.ReservaNoTrobadaException;
import cat.copernic.backendProjecte3.exceptions.VehicleNoDisponibleException;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import cat.copernic.backendProjecte3.repository.ReservaRepository;
import cat.copernic.backendProjecte3.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author manel
 */
@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepo;

    @Autowired
    private VehicleRepository vehicleRepo;

    @Autowired
    private ClientRepository clientRepo;
    
    @Autowired
    private UserLogic userLogic;


    public List<Reserva> obtenirTotes() {
        return reservaRepo.findAll();
    }

    public Reserva obtenirPerId(Long id) throws ReservaNoTrobadaException {
        return reservaRepo.findById(id)
                .orElseThrow(() -> new ReservaNoTrobadaException("Reserva no trobada: " + id));
    }

    public List<Reserva> obtenirPerClient(String email) {
        return reservaRepo.findByClient_Email(email);
    }

    @Transactional
    public void eliminarReserva(Long id) {
        reservaRepo.deleteById(id);
    }
    
    //TODO: recuperar totes les reserves

    //TODO: recuperar una reserva
    
    //TODO: consultar en un rang de dates els vehicles no reservats
    
    //TODO: lliurar vehicle

    /***
     * Crea una reserva entre dies dates pel client i vehicle solicitat
     * @param emailClient
     * @param matricula
     * @param inici
     * @param fi
     * @param userName
     * @return 
     * @throws cat.copernic.backendProjecte3.exceptions.ReservaDatesNoValidsException les dates no son vàlides
     * @throws cat.copernic.backendProjecte3.exceptions.VehicleNoDisponibleException el vehicle està de baixa
     */
    @Transactional
    public Reserva crearReserva(String emailClient, String matricula, LocalDate inici, LocalDate fi, String userName) throws ReservaDatesNoValidsException, VehicleNoDisponibleException, AccesDenegatException, DadesNoTrobadesException {
        
        //control de ROL
        UserRole rol = userLogic.getRole(userName).orElseThrow();
        if (rol != UserRole.CLIENT && rol != UserRole.AGENT && rol != UserRole.ADMIN) {
            throw new AccesDenegatException("Rol no vàlid");
        }
        
        //Validacio dates
        if (inici.isAfter(fi)) {
            throw new ReservaDatesNoValidsException("La data d'inici abans que data final!!");
        }
        if (inici.isBefore(LocalDate.now())) {
            throw new ReservaDatesNoValidsException("Reserva en el passat!!");
        }

        // Validació disponibilitat
        List<Reserva> reserves = reservaRepo.findReservasSolapadas(matricula, inici, fi);
        if (!reserves.isEmpty()) {
            throw new ReservaDatesNoValidsException("Vehicle no disponible en aquestes dates");
        }

        // client i vehicle
        Client client = clientRepo.findById(emailClient).orElseThrow(() -> new DadesNoTrobadesException("Client no trobat"));
        Vehicle vehicle = vehicleRepo.findById(matricula).orElseThrow(() -> new DadesNoTrobadesException("Vehicle no trobat"));
        
        if (vehicle.getEstatVehicle().equals(EstatVehicle.BAIXA))
            throw new VehicleNoDisponibleException("El vehicle està fora de servei");

        // creem reserva
        Reserva reserva = new Reserva();
        reserva.setClient(client);
        reserva.setVehicle(vehicle);
        reserva.setDataInici(inici);
        reserva.setDataFi(fi);

        // calculem dies entre dates
        long dies = ChronoUnit.DAYS.between(inici, fi);
        // menys d'un dia és un dia sencer
        dies = (dies == 0?1:dies);
        
        // el preu hora per 24 hores i pels dies de llogier
        BigDecimal importTotal = vehicle.getPreuHora().multiply(new BigDecimal(24)).multiply(new BigDecimal(dies));
        
        reserva.setImportTotal(importTotal);
        reserva.setFiancaPagada(vehicle.getFiancaEstandard());

        return reservaRepo.save(reserva);
    }


    @Transactional
    public void anularReserva(Long idReserva, String userName) throws ReservaNoTrobadaException, AccesDenegatException {
        
        //control de ROL
        UserRole rol = userLogic.getRole(userName).orElseThrow();
        if (rol != UserRole.CLIENT && rol != UserRole.AGENT && rol != UserRole.ADMIN) {
            throw new AccesDenegatException("Rol no vàlid");
        }
        
        Reserva reserva = obtenirPerId(idReserva);
        
        reservaRepo.delete(reserva);
    }
}