/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.business;

import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.exceptions.ErrorAltaException;
import cat.copernic.backendProjecte3.exceptions.ErrorDeleteException;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cat.copernic.backendProjecte3.dto.ClientRegistreDTO;
import cat.copernic.backendProjecte3.enums.UserRole;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author manel
 */
@Service
public class ClientService {
    
    @Autowired
    private ClientRepository clientRepo;

    /***
     * 
     * @return 
     */
    public List<Client> obtenirTots() {
        return clientRepo.findAll();
    }

    /***
     * 
     * @param email
     * @return 
     */
    public Client obtenirPerId(String email) {
        return clientRepo.findById(email)
                .orElseThrow(() -> new RuntimeException("Client no trobat amb email: " + email));
    }

    /***
     * 
     * @param email
     * @throws ErrorDeleteException 
     */
    @Transactional
    public void eliminarClient(String email) throws ErrorDeleteException {
        if (!clientRepo.existsById(email)) {
            throw new ErrorDeleteException("No es pot eliminar: " + email);
        }
        clientRepo.deleteById(email);
    }

    /***
     * 
     * @param client
     * @return 
     */
    @Transactional
    public Client guardarClient(Client client) {
        
        return clientRepo.save(client);
    }

    /***
     * Registra un nuevo cliente validando duplicados y mapeando desde DTO.
     * * @param dto Datos del registro provenientes del cliente (móvil/web)
     * @param dto
     * @return El cliente guardado
     * @throws ErrorAltaException Si el email o DNI ya existen
     */
   @Transactional
    public Client registrarNouClient(ClientRegistreDTO dto, MultipartFile fotoDni, MultipartFile fotoCarnet) throws ErrorAltaException {
        
        // 1. Validar Email (PK)
        if (clientRepo.existsById(dto.getEmail())) {
            throw new ErrorAltaException("Ja existeix un usuari amb aquest email: " + dto.getEmail());
        }
        
        // 2. Validar DNI
        if (clientRepo.existsByDni(dto.getDni())) {
            throw new ErrorAltaException("Ja existeix un client amb aquest DNI: " + dto.getDni());
        }

        // 3. Guardar las fotos en el disco duro del servidor y obtener la ruta final
        String rutaDni = guardarArxiu(fotoDni);
        String rutaCarnet = guardarArxiu(fotoCarnet);
        
        // 4. Mapeo Manual DTO -> Entidad Client
        Client nouClient = new Client();
        
        nouClient.setEmail(dto.getEmail());
        nouClient.setNomComplet(dto.getNomComplet());
        nouClient.setPassword(PasswordHasher.encode(dto.getPassword())); 
        nouClient.setRol(UserRole.CLIENT); 
        
        nouClient.setDni(dto.getDni());
        nouClient.setDataCaducitatDni(dto.getDataCaducitatDni());
        
        // Asignamos la ruta REAL de la foto (si subió foto) o el string por defecto que venía
        nouClient.setImatgeDni(rutaDni != null ? rutaDni : dto.getImatgeDni());
        
        nouClient.setNacionalitat(dto.getNacionalitat());
        nouClient.setAdreca(dto.getAdreca());
        
        nouClient.setTipusCarnetConduir(dto.getTipusCarnetConduir());
        nouClient.setDataCaducitatCarnet(dto.getDataCaducitatCarnet());
        
        // Asignamos la ruta REAL de la foto de la licencia
        nouClient.setImatgeCarnet(rutaCarnet != null ? rutaCarnet : dto.getImatgeCarnet());
        
        nouClient.setNumeroTargetaCredit(dto.getNumeroTargetaCredit());
        
        return clientRepo.save(nouClient);
    }
    
    // Función auxiliar para guardar el archivo físico
    // Función auxiliar para guardar el archivo físico
    private String guardarArxiu(MultipartFile arxiu) {
        if (arxiu == null || arxiu.isEmpty()) return null;

        try {
            String uploadDir = "uploads/";
            java.io.File dir = new java.io.File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + arxiu.getOriginalFilename();
            
            // AQUÍ ESTÁ EL CAMBIO: Usamos el nombre completo de la librería (java.nio.file)
            java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + fileName);
            java.nio.file.Files.write(path, arxiu.getBytes());

            return path.toString(); 
            
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
