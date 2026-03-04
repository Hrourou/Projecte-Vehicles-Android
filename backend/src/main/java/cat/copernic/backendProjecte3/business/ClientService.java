/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.business;

import cat.copernic.appvehicles.dto.ClientUpdateDTO;
import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.exceptions.ErrorAltaException;
import cat.copernic.backendProjecte3.exceptions.ErrorDeleteException;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 *
 * @author manel
 */
@Service
public class ClientService {
    
    private final ClientRepository clientRepository;
    
    @Autowired
    private ClientRepository clientRepo;

    /**
     * @return
     */
    public List<Client> obtenirTots() {
        return clientRepo.findAll();
    }

    /**
     * @param email
     * @return
     */
    public Client obtenirPerId(String email) {
        return clientRepo.findById(email)
                .orElseThrow(() -> new RuntimeException("Client no trobat amb email: " + email));
    }

    /**
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
    /**
     * @param client
     * @return
     */
    @Transactional
    public Client guardarClient(Client client) {

        return clientRepo.save(client);
    }
    /**
     * @param dadesClient
     * @param passwordEnClar
     * @return
     * @throws ErrorAltaException
     */
    @Transactional
    public Client registrarNouClient(Client dadesClient, String passwordEnClar) throws ErrorAltaException {
        if (clientRepo.existsById(dadesClient.getUsername())) {
            throw new ErrorAltaException("Ja existeix un usuari amb aquest email");
        }

        // Encriptem password
        dadesClient.setPassword(PasswordHasher.encode(passwordEnClar));

        // Assignem estat actiu directament (sense validació posterior)
        // dadesClient.setActiu(true); // Si tinguessis un camp boolean
        return clientRepo.save(dadesClient);
    }
    
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client obtenirPerDni(String dni) {
        return clientRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Client no trobat"));
    }

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

        return clientRepository.save(client);
    }
}
