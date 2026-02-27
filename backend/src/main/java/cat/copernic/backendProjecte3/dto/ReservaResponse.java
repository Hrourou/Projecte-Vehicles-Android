/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.dto;

import cat.copernic.backendProjecte3.entities.Reserva;
import java.time.LocalDate;
/**
 *
 * @author HAMZA
 */
public class ReservaResponse {
    private Long idReserva;
    private LocalDate dataInici;
    private LocalDate dataFi;
    private String clientEmail;
    private String vehicleMatricula;
    private String importTotal;
    private String fiancaPagada;

    // getters/setters...

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
}