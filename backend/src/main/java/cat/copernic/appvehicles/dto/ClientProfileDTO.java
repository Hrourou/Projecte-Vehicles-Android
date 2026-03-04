package cat.copernic.appvehicles.dto;


import cat.copernic.backendProjecte3.entities.Client;
import java.time.LocalDate;

public class ClientProfileDTO {

    public String dni;
    public String nomComplet;
    public String email;
    public String telefon;
    public String adreca;
    public String nacionalitat;
    public String numeroTargetaCredit;
    public LocalDate dataCaducitatDni;
    public String tipusCarnetConduir;
    public LocalDate dataCaducitatCarnet;

    public static ClientProfileDTO from(Client c) {
        ClientProfileDTO dto = new ClientProfileDTO();
        dto.dni = c.getDni();
        dto.nomComplet = c.getNomComplet();
        dto.email = c.getEmail();
        dto.telefon = c.getTelefon();
        dto.adreca = c.getAdreca();
        dto.nacionalitat = c.getNacionalitat();
        dto.numeroTargetaCredit = c.getNumeroTargetaCredit();
        dto.dataCaducitatDni = c.getDataCaducitatDni();
        dto.tipusCarnetConduir = c.getTipusCarnetConduir();
        dto.dataCaducitatCarnet = c.getDataCaducitatCarnet();
        return dto;
    }
}