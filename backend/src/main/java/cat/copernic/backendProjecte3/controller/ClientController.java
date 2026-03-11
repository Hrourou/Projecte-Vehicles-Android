package cat.copernic.backendProjecte3.controller;

import cat.copernic.backendProjecte3.dto.ClientProfileDTO;
import cat.copernic.backendProjecte3.dto.ClientUpdateDTO;
import cat.copernic.backendProjecte3.business.ClientService;
import cat.copernic.backendProjecte3.entities.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // Cambiamos a buscar por email
    @GetMapping("/{email}")
    public ResponseEntity<ClientProfileDTO> getClient(@PathVariable String email) {
        Client client = clientService.obtenirPerId(email); // obtenirPerId ya busca por email
        return ResponseEntity.ok(ClientProfileDTO.from(client));
    }

    // Cambiamos a actualizar por email
    @PutMapping("/{email}")
    public ResponseEntity<ClientProfileDTO> updateClient(
            @PathVariable String email,
            @RequestBody ClientUpdateDTO dto) {

        // Ahora sí, llamamos al método correcto pasándole el email de la URL
        Client updated = clientService.actualitzarPerfilPerEmail(email, dto);
        
        return ResponseEntity.ok(ClientProfileDTO.from(updated));
    }
}