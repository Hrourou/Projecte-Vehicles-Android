/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.entities;

import cat.copernic.backendProjecte3.enums.Reputacio;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "client")
@PrimaryKeyJoinColumn(name = "client_email")
public class Client extends Usuari{

    // --- Datos Personales ---
    
    @Column(nullable = false, length = 20)
    private String dni;

    private String adreca;

    private String carnetConduir;

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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getAdreca() {
        return adreca;
    }

    public void setAdreca(String adreca) {
        this.adreca = adreca;
    }

    public String getCarnetConduir() {
        return carnetConduir;
    }

    public void setCarnetConduir(String carnetConduir) {
        this.carnetConduir = carnetConduir;
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
        StringBuilder sb = new StringBuilder();
        sb.append("Client{");
        sb.append("dni=").append(dni);
        sb.append(", adreca=").append(adreca);
        sb.append(", carnetConduir=").append(carnetConduir);
        sb.append(", numeroTargetaCredit=").append(numeroTargetaCredit);
        sb.append(", reputacio=").append(reputacio);
        sb.append(", reservas=").append(reservas);
        sb.append('}');
        return sb.toString();
    }
}

