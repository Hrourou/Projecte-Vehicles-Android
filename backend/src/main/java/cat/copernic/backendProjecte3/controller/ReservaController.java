package cat.copernic.backendProjecte3.controller;

import cat.copernic.backendProjecte3.business.ReservaService;
import cat.copernic.backendProjecte3.dto.CancelReservaResponse;
import cat.copernic.backendProjecte3.entities.Reserva;
import cat.copernic.backendProjecte3.exceptions.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cat.copernic.backendProjecte3.dto.ReservaResponse;

@RestController
@RequestMapping("/api/reserves")
@CrossOrigin(origins = "*")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    // Listar reservas por cliente y ordenar asc/desc por fecha de inicio
    @GetMapping
    public List<ReservaResponse> getReservesByClient(
            @RequestParam String email,
            @RequestParam(defaultValue = "desc") String order
    ) {
        List<Reserva> reserves = reservaService.obtenirPerClient(email);

        Comparator<Reserva> cmp = Comparator.comparing(Reserva::getDataInici);
        if ("desc".equalsIgnoreCase(order)) {
            cmp = cmp.reversed();
        }

        return reserves.stream()
            .sorted(cmp)
            .map(ReservaResponse::fromEntity)
            .collect(Collectors.toList());
    }

    // Obtener detalle por id
    @GetMapping("/{id}")
    public ReservaResponse getReservaById(@PathVariable Long id) throws ReservaNoTrobadaException {
        Reserva r = reservaService.obtenirPerId(id);
        return ReservaResponse.fromEntity(r);
    }

    // Crear reserva
    @PostMapping
    public ReservaResponse createReserva(@RequestBody CreateReservaRequest req)
            throws ReservaDatesNoValidsException, VehicleNoDisponibleException, AccesDenegatException, DadesNoTrobadesException {

        // Pasamos los 5 parámetros en el orden correcto, incluyendo el userName
        Reserva r = reservaService.crearReserva(
                req.getEmailClient(),
                req.getMatricula(),
                req.getDataInici(),
                req.getDataFi(),
                req.getUserName()
        );

        return ReservaResponse.fromEntity(r);
    }
    
    // Anular reserva
    @DeleteMapping("/{id}")
    public CancelReservaResponse cancelReserva(
            @PathVariable Long id,
            @RequestParam String userName
    ) throws ReservaNoTrobadaException, AccesDenegatException, ReservaNoCancelableException {
        return reservaService.anularReserva(id, userName);
    }

    // DTO request para crear reserva
    public static class CreateReservaRequest {
        private String emailClient;
        private String matricula;
        private LocalDate dataInici;
        private LocalDate dataFi;
        private String userName;

        public String getEmailClient() { return emailClient; }
        public void setEmailClient(String emailClient) { this.emailClient = emailClient; }
        public String getMatricula() { return matricula; }
        public void setMatricula(String matricula) { this.matricula = matricula; }
        public LocalDate getDataInici() { return dataInici; }
        public void setDataInici(LocalDate dataInici) { this.dataInici = dataInici; }
        public LocalDate getDataFi() { return dataFi; }
        public void setDataFi(LocalDate dataFi) { this.dataFi = dataFi; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
    }
}