/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DTO;

import cat.copernic.backendProjecte3.entities.Vehicle;

public class VehicleMapper {

    public static VehicleResponseDTO toDTO(Vehicle vehicle) {

        return new VehicleResponseDTO(
                vehicle.getMatricula(),
                vehicle.getTipusVehicle().name(),
                vehicle.getMotor(),
                vehicle.getPotencia(),
                vehicle.getColor(),
                vehicle.getPreuHora()
        );
    }
}
