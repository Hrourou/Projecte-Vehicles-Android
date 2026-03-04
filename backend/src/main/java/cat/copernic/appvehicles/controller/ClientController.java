package cat.copernic.appvehicles.controller;

import cat.copernic.appvehicles.dto.ClientProfileDTO;
import cat.copernic.appvehicles.dto.ClientUpdateDTO;
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

    @GetMapping("/{dni}")
    public ResponseEntity<ClientProfileDTO> getClient(@PathVariable String dni) {
        Client client = clientService.obtenirPerDni(dni);
        return ResponseEntity.ok(ClientProfileDTO.from(client));
    }

    @PutMapping("/{dni}")
    public ResponseEntity<ClientProfileDTO> updateClient(
            @PathVariable String dni,
            @RequestBody ClientUpdateDTO dto) {

        Client updated = clientService.actualitzarPerfilPerDni(dni, dto);
        return ResponseEntity.ok(ClientProfileDTO.from(updated));
    }
}