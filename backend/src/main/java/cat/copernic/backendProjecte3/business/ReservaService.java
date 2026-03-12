package cat.copernic.backendProjecte3.business;

import cat.copernic.backendProjecte3.dto.CancelReservaResponse;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.entities.Reserva;
import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.EstatReserva;
import cat.copernic.backendProjecte3.enums.EstatVehicle;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.exceptions.*;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import cat.copernic.backendProjecte3.repository.ReservaRepository;
import cat.copernic.backendProjecte3.repository.VehicleRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final VehicleRepository vehicleRepository;
    private final ClientRepository clientRepository;

    @Autowired
    private UserLogic userLogic;

    @Value("${reserva.cancel.fullRefundDays:3}")
    private int fullRefundDays;

    public ReservaService(
            ReservaRepository reservaRepository,
            VehicleRepository vehicleRepository,
            ClientRepository clientRepository
    ) {
        this.reservaRepository = reservaRepository;
        this.vehicleRepository = vehicleRepository;
        this.clientRepository = clientRepository;
    }

    public List<Reserva> obtenirPerClient(String email) {
        return reservaRepository.findByClient_Email(email);
    }

    public Reserva obtenirPerId(Long id) throws ReservaNoTrobadaException {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNoTrobadaException("Reserva no trobada"));
    }

    @Transactional
    public Reserva crearReserva(String emailClient, String matricula, LocalDate inici, LocalDate fi, String userName) 
            throws ReservaDatesNoValidsException, VehicleNoDisponibleException, AccesDenegatException, DadesNoTrobadesException {

        // control de ROL
        UserRole rol = userLogic.getRole(userName).orElseThrow(() -> new AccesDenegatException("Usuari no trobat o sense rol"));
        if (rol != UserRole.CLIENT && rol != UserRole.AGENT && rol != UserRole.ADMIN) {
            throw new AccesDenegatException("Rol no vàlid");
        }

        // Validacio dates
        if (inici.isAfter(fi)) {
            throw new ReservaDatesNoValidsException("La data d'inici és abans que data final!!");
        }
        if (inici.isBefore(LocalDate.now())) {
            throw new ReservaDatesNoValidsException("Reserva en el passat!!");
        }

        // Validació disponibilitat
        List<Reserva> reserves = reservaRepository.findReservasSolapadas(matricula, inici, fi);
        if (!reserves.isEmpty()) {
            throw new ReservaDatesNoValidsException("Vehicle no disponible en aquestes dates");
        }

        // client i vehicle
        Client client = clientRepository.findById(emailClient).orElseThrow(() -> new DadesNoTrobadesException("Client no trobat"));
        Vehicle vehicle = vehicleRepository.findById(matricula).orElseThrow(() -> new DadesNoTrobadesException("Vehicle no trobat"));

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
        dies = (dies == 0 ? 1 : dies);

        // el preu hora per 24 hores i pels dies de lloguer
        BigDecimal importTotal = vehicle.getPreuHora().multiply(new BigDecimal(24)).multiply(new BigDecimal(dies));

        reserva.setImportTotal(importTotal);
        reserva.setFiancaPagada(vehicle.getFiancaEstandard());
        reserva.setEstat(EstatReserva.ACTIVA);

        return reservaRepository.save(reserva);
    }

    @Transactional
    public CancelReservaResponse anularReserva(Long idReserva, String userName)
            throws ReservaNoTrobadaException, AccesDenegatException, ReservaNoCancelableException {

        // control de ROL
        UserRole rol = userLogic.getRole(userName).orElseThrow(() -> new AccesDenegatException("Usuari no trobat o sense rol"));
        if (rol != UserRole.CLIENT && rol != UserRole.AGENT && rol != UserRole.ADMIN) {
            throw new AccesDenegatException("Rol no vàlid");
        }

        Reserva reserva = obtenirPerId(idReserva);

        LocalDate today = LocalDate.now();
        LocalDate inici = reserva.getDataInici();

        // Si hoy es el mismo día o después del inicio => ya iniciada (o finalizada)
        if (!today.isBefore(inici)) {
            throw new ReservaNoCancelableException("No es pot anul·lar una reserva iniciada o finalitzada.");
        }

        long daysAhead = ChronoUnit.DAYS.between(today, inici);
        BigDecimal refund = BigDecimal.ZERO;

        if (daysAhead >= fullRefundDays) {
            BigDecimal importTotal = reserva.getImportTotal() != null ? reserva.getImportTotal() : BigDecimal.ZERO;
            BigDecimal fianca = reserva.getFiancaPagada() != null ? reserva.getFiancaPagada() : BigDecimal.ZERO;
            refund = importTotal.add(fianca);
        }

        // RF55: CANVIEM L'ESTAT, NO L'ESBORREM
        reserva.setEstat(EstatReserva.CANCELADA);
        reservaRepository.save(reserva);

        // SIMULACIÓ D'ENVIAMENT D'EMAIL
        System.out.println("====== ENVIANT EMAIL AL CLIENT ======");
        System.out.println("Per a: " + reserva.getClient().getEmail());
        System.out.println("Assumpte: Confirmació d'anul·lació de reserva");
        System.out.println("Cos del missatge: Hola " + reserva.getClient().getNomComplet() + ",");
        System.out.println("La teva reserva del vehicle " + reserva.getVehicle().getMatricula() + " ha estat anul·lada amb èxit.");
        System.out.println("Se t'ha aplicat un reemborsament de: " + refund + " €");
        System.out.println("=====================================");

        String msg = (refund.compareTo(BigDecimal.ZERO) > 0)
                ? "Reserva anul·lada. Reemborsament: " + refund + " €"
                : "Reserva anul·lada. Sense reemborsament.";

        return new CancelReservaResponse(idReserva, refund, msg);
    }
}