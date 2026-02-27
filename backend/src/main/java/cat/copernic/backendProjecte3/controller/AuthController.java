package cat.copernic.backendProjecte3.controller;

/**
 *
 * @author bharr
 */
import cat.copernic.backendProjecte3.business.ClientService; // O tu servicio de auth si lo separaste
import cat.copernic.backendProjecte3.dto.ClientRegistreDTO;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.exceptions.ErrorAltaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth") // Esta es la dirección base de la "puerta"
@CrossOrigin
public class AuthController {

    @Autowired
    private ClientService clientService; // Inyectamos la lógica

    /**
     * Endpoint para registrar un cliente desde el móvil.
     * URL: POST http://localhost:8080/api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody ClientRegistreDTO registerDTO) {
        
        try {
            // Llamamos a la lógica que acabamos de programar
            Client nuevoCliente = clientService.registrarNouClient(registerDTO);
            
            // Si todo va bien, respondemos con código 201 (CREATED) y un JSON de éxito
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuari registrat correctament");
            response.put("email", nuevoCliente.getEmail()); // Devolvemos el ID
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (ErrorAltaException e) {
            // Si hay error (email o DNI duplicado), devolvemos código 409 (CONFLICT)
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            
        } catch (Exception e) {
            // Cualquier otro error inesperado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error intern del servidor: " + e.getMessage());
        }
    }
}
