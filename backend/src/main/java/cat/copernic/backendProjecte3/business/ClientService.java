package cat.copernic.backendProjecte3.business;

import cat.copernic.backendProjecte3.dto.ClientUpdateDTO;
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
import org.springframework.web.multipart.MultipartFile;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepo;

    public List<Client> obtenirTots() {
        return clientRepo.findAll();
    }

    public Client obtenirPerId(String email) {
        return clientRepo.findById(email)
                .orElseThrow(() -> new RuntimeException("Client no trobat amb email: " + email));
    }

    public Client obtenirPerDni(String dni) {
        return clientRepo.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Client no trobat amb DNI: " + dni));
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
     * Registra un nuevo cliente validando duplicados y guardando fotos si existen.
     */
    @Transactional
    public Client registrarNouClient(ClientRegistreDTO dto, MultipartFile fotoDni, MultipartFile fotoCarnet) throws ErrorAltaException {

        // 1. Validar Email
        if (clientRepo.existsById(dto.getEmail())) {
            throw new ErrorAltaException("Ja existeix un usuari amb aquest email: " + dto.getEmail());
        }

        // 2. Validar DNI
        if (clientRepo.existsByDni(dto.getDni())) {
            throw new ErrorAltaException("Ja existeix un client amb aquest DNI: " + dto.getDni());
        }

        // 3. Guardar fotos si existen
        String rutaDni = guardarArxiu(fotoDni);
        String rutaCarnet = guardarArxiu(fotoCarnet);

        // 4. Mapear DTO -> Entidad
        Client nouClient = new Client();

        nouClient.setEmail(dto.getEmail());
        nouClient.setNomComplet(dto.getNomComplet());
        nouClient.setPassword(PasswordHasher.encode(dto.getPassword()));
        nouClient.setRol(UserRole.CLIENT);

        nouClient.setDni(dto.getDni());
        nouClient.setDataCaducitatDni(dto.getDataCaducitatDni());
        nouClient.setImatgeDni(rutaDni != null ? rutaDni : dto.getImatgeDni());

        nouClient.setNacionalitat(dto.getNacionalitat());
        nouClient.setAdreca(dto.getAdreca());

        nouClient.setTipusCarnetConduir(dto.getTipusCarnetConduir());
        nouClient.setDataCaducitatCarnet(dto.getDataCaducitatCarnet());
        nouClient.setImatgeCarnet(rutaCarnet != null ? rutaCarnet : dto.getImatgeCarnet());

        nouClient.setNumeroTargetaCredit(dto.getNumeroTargetaCredit());

        return clientRepo.save(nouClient);
    }

    /**
     * RF04: Actualizar perfil de cliente por DNI
     */
    @Transactional
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

        if (dto.getImatgeDni() != null) {
            client.setImatgeDni(dto.getImatgeDni());
        }

        if (dto.getImatgeCarnet() != null) {
            client.setImatgeCarnet(dto.getImatgeCarnet());
        }

        return clientRepo.save(client);
    }

    /**
     * Función auxiliar para guardar archivos en el servidor
     */
    private String guardarArxiu(MultipartFile arxiu) {

        if (arxiu == null || arxiu.isEmpty()) return null;

        try {

            String uploadDir = "uploads/";
            java.io.File dir = new java.io.File(uploadDir);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + arxiu.getOriginalFilename();

            java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + fileName);
            java.nio.file.Files.write(path, arxiu.getBytes());

            return path.toString();

        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}