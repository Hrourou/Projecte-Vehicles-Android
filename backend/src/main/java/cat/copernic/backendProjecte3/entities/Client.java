/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.entities;

import cat.copernic.backendProjecte3.enums.Reputacio;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "client")
@PrimaryKeyJoinColumn(name = "client_email")
public class Client extends Usuari {

    // --- Datos de Identificación ---
    @Column(nullable = false, length = 20)
    private String dni;

    @Column(name = "data_caducitat_dni")
    private LocalDate dataCaducitatDni;

    @Column(name = "imatge_dni") // Guardaremos la URL o ruta del archivo
    private String imatgeDni;

    private String nacionalitat;

    private String adreca;

    // --- Datos de Conducción ---
    @Column(name = "tipus_carnet_conduir") // Ejemplo: A, B, C...
    private String tipusCarnetConduir;

    @Column(name = "data_caducitat_carnet")
    private LocalDate dataCaducitatCarnet;

    @Column(name = "imatge_carnet") // Guardaremos la URL o ruta del archivo
    private String imatgeCarnet;

    // --- Datos Económicos ---
    @Column(name = "numero_targeta_credit")
    private String numeroTargetaCredit;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'NORMAL'")
    private Reputacio reputacio;
    
    @OneToMany(
        mappedBy = "client", 
        cascade = CascadeType.ALL
    )
    private List<Reserva> reservas = new ArrayList<>();

    public Client() { super(); }

    // --- Getters y Setters ---

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDate getDataCaducitatDni() {
        return dataCaducitatDni;
    }

    public void setDataCaducitatDni(LocalDate dataCaducitatDni) {
        this.dataCaducitatDni = dataCaducitatDni;
    }

    public String getImatgeDni() {
        return imatgeDni;
    }

    public void setImatgeDni(String imatgeDni) {
        this.imatgeDni = imatgeDni;
    }

    public String getNacionalitat() {
        return nacionalitat;
    }

    public void setNacionalitat(String nacionalitat) {
        this.nacionalitat = nacionalitat;
    }

    public String getAdreca() {
        return adreca;
    }

    public void setAdreca(String adreca) {
        this.adreca = adreca;
    }

    public String getTipusCarnetConduir() {
        return tipusCarnetConduir;
    }

    public void setTipusCarnetConduir(String tipusCarnetConduir) {
        this.tipusCarnetConduir = tipusCarnetConduir;
    }

    public LocalDate getDataCaducitatCarnet() {
        return dataCaducitatCarnet;
    }

    public void setDataCaducitatCarnet(LocalDate dataCaducitatCarnet) {
        this.dataCaducitatCarnet = dataCaducitatCarnet;
    }

    public String getImatgeCarnet() {
        return imatgeCarnet;
    }

    public void setImatgeCarnet(String imatgeCarnet) {
        this.imatgeCarnet = imatgeCarnet;
    }

    public String getNumeroTargetaCredit() {
        return numeroTargetaCredit;
    }

    public void setNumeroTargetaCredit(String numeroTargetaCredit) {
        this.numeroTargetaCredit = numeroTargetaCredit;
    }

    public Reputacio getReputacio() {
        return reputacio;
    }

    public void setReputacio(Reputacio reputacio) {
        this.reputacio = reputacio;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.dni);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Client other = (Client) obj;
        return Objects.equals(this.dni, other.dni);
    }

    @Override
    public String toString() {
        return "Client{" + "dni=" + dni + ", nacionalitat=" + nacionalitat + ", adreca=" + adreca + ", reputacio=" + reputacio + '}';
    }
}

