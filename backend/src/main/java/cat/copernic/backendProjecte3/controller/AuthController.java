package cat.copernic.backendProjecte3.controller;

import cat.copernic.backendProjecte3.business.ClientService;
import cat.copernic.backendProjecte3.dto.ClientRegistreDTO;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.exceptions.ErrorAltaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // IMPORTANTE

import java.util.HashMap;
import java.util.Map;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper; // Herramienta para convertir JSON a Objeto

    /**
     * Endpoint para registrar un cliente desde el móvil con fotos.
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(
            @RequestPart("clientData") String clientDataJson, // El JSON con los textos
            @RequestPart(value = "fotoIdentificacio", required = false) MultipartFile fotoIdentificacio, // La foto 1
            @RequestPart(value = "fotoLlicencia", required = false) MultipartFile fotoLlicencia // La foto 2
    ) {
        
        try {
            // 1. Convertimos el JSON de Android a nuestro DTO
            ClientRegistreDTO registerDTO = objectMapper.readValue(clientDataJson, ClientRegistreDTO.class);

            // 2. Llamamos a la lógica pasándole el DTO y los archivos físicos
            Client nuevoCliente = clientService.registrarNouClient(registerDTO, fotoIdentificacio, fotoLlicencia);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuari registrat correctament");
            response.put("email", nuevoCliente.getEmail());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (ErrorAltaException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse); // Esto Android lo lee como 409
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error intern del servidor: " + e.getMessage());
        }
    }
}
