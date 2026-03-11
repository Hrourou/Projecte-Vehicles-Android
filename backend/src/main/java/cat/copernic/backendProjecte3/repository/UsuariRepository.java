package cat.copernic.backendProjecte3.repository;

import cat.copernic.backendProjecte3.entities.Usuari;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuariRepository extends JpaRepository<Usuari, String> {

    Optional<Usuari> findByEmail(String email);

    Optional<Usuari> findByResetPasswordToken(String resetPasswordToken);
}