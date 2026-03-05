package cat.copernic.backendProjecte3.repository;

import cat.copernic.backendProjecte3.entities.Client;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    Optional<Client> findByDni(String dni);

    boolean existsByDni(String dni);
}