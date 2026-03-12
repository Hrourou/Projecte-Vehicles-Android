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

    @Autowired
    private EmailService emailService;

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

        // Control de ROL
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

        // Client i vehicle
        Client client = clientRepository.findById(emailClient)
                .orElseThrow(() -> new DadesNoTrobadesException("Client no trobat"));

        Vehicle vehicle = vehicleRepository.findById(matricula)
                .orElseThrow(() -> new DadesNoTrobadesException("Vehicle no trobat"));

        if (vehicle.getEstatVehicle().equals(EstatVehicle.BAIXA))
            throw new VehicleNoDisponibleException("El vehicle està fora de servei");

        // --- NOU: VALIDAR LÍMITS REALS DEL VEHICLE ---
        long dies = ChronoUnit.DAYS.between(inici, fi);
        dies = (dies <= 0 ? 1 : dies);

        if (dies < vehicle.getMinDiesLloguer() || dies > vehicle.getMaxDiesLloguer()) {
            throw new ReservaDatesNoValidsException(
                "Els dies seleccionats (" + dies + ") no estan permesos per a aquest vehicle. Límits: "
                + vehicle.getMinDiesLloguer() + " - " + vehicle.getMaxDiesLloguer() + " dies."
            );
        }

        // Creem reserva
        Reserva reserva = new Reserva();
        reserva.setClient(client);
        reserva.setVehicle(vehicle);
        reserva.setDataInici(inici);
        reserva.setDataFi(fi);

        // Calculem dies entre dates
        dies = ChronoUnit.DAYS.between(inici, fi);
        dies = (dies == 0 ? 1 : dies);

        // El preu hora per 24 hores i pels dies de lloguer
        BigDecimal importTotal = vehicle.getPreuHora()
                .multiply(new BigDecimal(24))
                .multiply(new BigDecimal(dies));

        reserva.setImportTotal(importTotal);
        reserva.setFiancaPagada(vehicle.getFiancaEstandard());
        reserva.setEstat(EstatReserva.ACTIVA);

        // Guardem a la base de dades
        Reserva reservaGuardada = reservaRepository.save(reserva);

        // ENVIAMENT DEL CORREU REAL D'ALTA
        try {
            emailService.sendReservationCreatedEmail(
                client.getEmail(),
                client.getNomComplet(),
                vehicle.getMatricula(),
                inici.toString(),
                fi.toString(),
                "RES-" + reservaGuardada.getIdReserva()
            );
        } catch (Exception e) {
            System.err.println("Error enviant el correu d'alta de reserva: " + e.getMessage());
        }

        return reservaGuardada;
    }

    @Transactional
    public CancelReservaResponse anularReserva(Long idReserva, String userName)
            throws ReservaNoTrobadaException, AccesDenegatException, ReservaNoCancelableException {

        // Control de ROL
        UserRole rol = userLogic.getRole(userName).orElseThrow(() -> new AccesDenegatException("Usuari no trobat o sense rol"));
        if (rol != UserRole.CLIENT && rol != UserRole.AGENT && rol != UserRole.ADMIN) {
            throw new AccesDenegatException("Rol no vàlid");
        }

        Reserva reserva = obtenirPerId(idReserva);

        LocalDate today = LocalDate.now();
        LocalDate inici = reserva.getDataInici();

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

        reserva.setEstat(EstatReserva.CANCELADA);
        reservaRepository.save(reserva);

        try {
            emailService.sendReservationCancelledEmail(
                reserva.getClient().getEmail(),
                reserva.getClient().getNomComplet(),
                reserva.getVehicle().getMatricula(),
                "RES-" + idReserva,
                refund.doubleValue()
            );
        } catch (Exception e) {
            System.err.println("Error enviant el correu d'anul·lació: " + e.getMessage());
        }

        String msg = (refund.compareTo(BigDecimal.ZERO) > 0)
                ? "Reserva anul·lada. Reemborsament: " + refund + " €"
                : "Reserva anul·lada. Sense reemborsament.";

        return new CancelReservaResponse(idReserva, refund, msg);
    }
}