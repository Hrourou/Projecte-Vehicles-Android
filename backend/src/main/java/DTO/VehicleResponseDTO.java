/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DTO;

import java.math.BigDecimal;

public class VehicleResponseDTO {

    private String matricula;
    private String tipusVehicle;
    private String motor;
    private String potencia;
    private String color;
    private BigDecimal preuHora;

    public VehicleResponseDTO(
            String matricula,
            String tipusVehicle,
            String motor,
            String potencia,
            String color,
            BigDecimal preuHora) {

        this.matricula = matricula;
        this.tipusVehicle = tipusVehicle;
        this.motor = motor;
        this.potencia = potencia;
        this.color = color;
        this.preuHora = preuHora;
    }

    public String getMatricula() { return matricula; }
    public String getTipusVehicle() { return tipusVehicle; }
    public String getMotor() { return motor; }
    public String getPotencia() { return potencia; }
    public String getColor() { return color; }
    public BigDecimal getPreuHora() { return preuHora; }
}
