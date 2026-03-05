package cat.copernic.backendProjecte3.business;

import cat.copernic.appvehicles.dto.ClientUpdateDTO;
import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.dto.ClientRegistreDTO;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.exceptions.ErrorAltaException;
import cat.copernic.backendProjecte3.exceptions.ErrorDeleteException;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    private ClientRepository clientRepo;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> obtenirTots() {
        return clientRepo.findAll();
    }

    public Client obtenirPerId(String email) {
        return clientRepo.findById(email)
                .orElseThrow(() -> new RuntimeException("Client no trobat amb email: " + email));
    }

    @Transactional
    public void eliminarClient(String email) throws ErrorDeleteException {
        if (!clientRepo.existsById(email)) {
            throw new ErrorDeleteException("No es pot eliminar: " + email);
        }
        clientRepo.deleteById(email);
    }

    @Transactional
    public Client guardarClient(Client client) {
        return clientRepo.save(client);
    }

    /**
     * Registra un nuevo cliente validando duplicados y mapeando desde DTO.
     * @param dto Datos del registro provenientes del cliente (móvil/web)
     * @return El cliente guardado
     * @throws ErrorAltaException Si el email o DNI ya existen
     */
    @Transactional
    public Client registrarNouClient(ClientRegistreDTO dto) throws ErrorAltaException {

        // 1. Validar Email (PK)
        if (clientRepo.existsById(dto.getEmail())) {
            throw new ErrorAltaException("Ja existeix un usuari amb aquest email: " + dto.getEmail());
        }

        // 2. Validar DNI
        if (clientRepo.existsByDni(dto.getDni())) {
            throw new ErrorAltaException("Ja existeix un client amb aquest DNI: " + dto.getDni());
        }

        // 3. Mapeo DTO -> Entidad
        Client nouClient = new Client();

        // --- Datos Usuari ---
        nouClient.setEmail(dto.getEmail());
        nouClient.setNomComplet(dto.getNomComplet());
        nouClient.setPassword(PasswordHasher.encode(dto.getPassword()));
        nouClient.setRol(UserRole.CLIENT);

        // --- Datos Client ---
        nouClient.setDni(dto.getDni());
        nouClient.setDataCaducitatDni(dto.getDataCaducitatDni());
        nouClient.setImatgeDni(dto.getImatgeDni());
        nouClient.setNacionalitat(dto.getNacionalitat());
        nouClient.setAdreca(dto.getAdreca());

        nouClient.setTipusCarnetConduir(dto.getTipusCarnetConduir());
        nouClient.setDataCaducitatCarnet(dto.getDataCaducitatCarnet());
        nouClient.setImatgeCarnet(dto.getImatgeCarnet());

        nouClient.setNumeroTargetaCredit(dto.getNumeroTargetaCredit());

        return clientRepo.save(nouClient);
    }

    public Client obtenirPerDni(String dni) {
        return clientRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Client no trobat"));
    }

    /**
     *  RF04: actualiza perfil por DNI, incluyendo documentación si llega en DTO.
     */
    public Client actualitzarPerfilPerDni(String dni, ClientUpdateDTO dto) {
        Client client = obtenirPerDni(dni);

        client.setNomComplet(dto.getNomComplet());
        client.setTelefon(dto.getTelefon());
        client.setAdreca(dto.getAdreca());
        client.setNacionalitat(dto.getNacionalitat());
        client.setNumeroTargetaCredit(dto.getNumeroTargetaCredit());
        client.setDataCaducitatDni(dto.getDataCaducitatDni());
        client.setTipusCarnetConduir(dto.getTipusCarnetConduir());
        client.setDataCaducitatCarnet(dto.getDataCaducitatCarnet());

        // NUEVO: permitir cambiar imágenes si vienen informadas
        if (dto.getImatgeDni() != null) {
            client.setImatgeDni(dto.getImatgeDni());
        }
        if (dto.getImatgeCarnet() != null) {
            client.setImatgeCarnet(dto.getImatgeCarnet());
        }

        return clientRepository.save(client);
    }
}