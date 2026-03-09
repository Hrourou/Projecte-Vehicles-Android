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

import cat.copernic.backendProjecte3.business.UserLogic;
import cat.copernic.backendProjecte3.dto.LoginRequest;
import cat.copernic.backendProjecte3.dto.LoginResponse;
import cat.copernic.backendProjecte3.exceptions.AccesDenegatException;
import cat.copernic.backendProjecte3.entities.Usuari;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper; // Herramienta para convertir JSON a Objeto

    @Autowired
    private UserLogic userLogic; // Inyectamos la lógica de usuarios donde hicimos el método login

    /**
     * Endpoint para el inicio de sesión (RF01).
     */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Llamamos a la lógica de negocio
            Usuari usuari = userLogic.login(loginRequest.getEmail(), loginRequest.getPassword());

            // 2. Mapeamos la entidad al DTO de respuesta
            LoginResponse response = new LoginResponse();
            response.setEmail(usuari.getEmail());
            response.setNomComplet(usuari.getNomComplet());

            // Generamos un token básico (UUID) para que el móvil guarde el estado de la sesión
            response.setToken(UUID.randomUUID().toString());

            // 3. Devolvemos 200 OK con los datos
            return ResponseEntity.ok(response);

        } catch (AccesDenegatException e) {
            // Si fallan las credenciales, devolvemos 401 Unauthorized
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (Exception e) {
            // Error genérico del servidor 500
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error intern del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

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
