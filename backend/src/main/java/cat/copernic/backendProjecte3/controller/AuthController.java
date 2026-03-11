package cat.copernic.backendProjecte3.controller;

import cat.copernic.backendProjecte3.business.ClientService;
import cat.copernic.backendProjecte3.business.UserLogic;
import cat.copernic.backendProjecte3.dto.ClientRegistreDTO;
import cat.copernic.backendProjecte3.dto.PasswordRecoveryRequest;
import cat.copernic.backendProjecte3.dto.PasswordRecoveryResponse;
import cat.copernic.backendProjecte3.dto.ResetPasswordRequest;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.entities.Usuari;
import cat.copernic.backendProjecte3.exceptions.AccesDenegatException;
import cat.copernic.backendProjecte3.exceptions.ErrorAltaException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // IMPORTANTE
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private UserLogic userLogic;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(
            @RequestPart("clientData") String clientDataJson,
            @RequestPart(value = "fotoIdentificacio", required = false) MultipartFile fotoIdentificacio,
            @RequestPart(value = "fotoLlicencia", required = false) MultipartFile fotoLlicencia
    ) {

        try {
            ClientRegistreDTO registerDTO = objectMapper.readValue(clientDataJson, ClientRegistreDTO.class);
            Client nuevoCliente = clientService.registrarNouClient(registerDTO, fotoIdentificacio, fotoLlicencia);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuari registrat correctament");
            response.put("email", nuevoCliente.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (ErrorAltaException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error intern del servidor: " + e.getMessage());
        }
    }

    @PostMapping("/recover-password")
    public ResponseEntity<PasswordRecoveryResponse> recoverPassword(@RequestBody PasswordRecoveryRequest request) {

        userLogic.recoverPassword(request.getEmail());

        return ResponseEntity.ok(
                new PasswordRecoveryResponse(
                        "recover_sent",
                        "If the email exists, you will receive recovery instructions shortly."
                )
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<PasswordRecoveryResponse> resetPassword(@RequestBody ResetPasswordRequest request) {

        userLogic.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok(
                new PasswordRecoveryResponse(
                        "password_reset_ok",
                        "Your password has been updated successfully."
                )
        );
    }
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
}