/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.backendProjecte3.repository;

import cat.copernic.backendProjecte3.entities.Usuari;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author manel
 */
@Repository
public interface UsuariRepository extends JpaRepository<Usuari, String> {
    
    // Método para buscar cualquier usuario por email
    Optional<Usuari> findByEmail(String email);
}
