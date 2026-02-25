/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import cat.copernic.backendProjecte3.business.ReservaService;
import cat.copernic.backendProjecte3.entities.Reserva;
import cat.copernic.backendProjecte3.exceptions.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 *
 * @author HAMZA
 */
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
    public void cancelReserva(
            @PathVariable Long id,
            @RequestParam String userName
    ) throws ReservaNoTrobadaException, AccesDenegatException {
        reservaService.anularReserva(id, userName);
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

    // DTO response que devolverá la API para cada reserva
    public static class ReservaResponse {
        private Long idReserva;
        private LocalDate dataInici;
        private LocalDate dataFi;
        private String clientEmail;
        private String vehicleMatricula;
        private String importTotal;
        private String fiancaPagada;

        public static ReservaResponse fromEntity(Reserva r) {
            ReservaResponse dto = new ReservaResponse();
            dto.idReserva = r.getIdReserva();
            dto.dataInici = r.getDataInici();
            dto.dataFi = r.getDataFi();
            dto.clientEmail = r.getClient() != null ? r.getClient().getUsername() : null;
            dto.vehicleMatricula = r.getVehicle() != null ? r.getVehicle().getMatricula() : null;
            dto.importTotal = r.getImportTotal() != null ? r.getImportTotal().toPlainString() : null;
            dto.fiancaPagada = r.getFiancaPagada() != null ? r.getFiancaPagada().toPlainString() : null;
            return dto;
        }

        public Long getIdReserva() { return idReserva; }
        public LocalDate getDataInici() { return dataInici; }
        public LocalDate getDataFi() { return dataFi; }
        public String getClientEmail() { return clientEmail; }
        public String getVehicleMatricula() { return vehicleMatricula; }
        public String getImportTotal() { return importTotal; }
        public String getFiancaPagada() { return fiancaPagada; }
    }
}