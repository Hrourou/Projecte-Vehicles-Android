package cat.copernic.backendProjecte3.business;

import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.entities.Usuari;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.exceptions.AccesDenegatException;
import cat.copernic.backendProjecte3.repository.UsuariRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author manel
 */
@Service
public class UserLogic {
    
    @Autowired
    UsuariRepository usuariRepository;

    /***
     * Metodo para autenticar un usuario
     * @param email email del usuario
     * @param rawPassword contraseña en texto plano recibida del cliente
     * @return Entidad Usuari si las credenciales son correctas
     * @throws AccesDenegatException si el usuario no existe o la contraseña es incorrecta
     */
    public Usuari login(String email, String rawPassword) throws AccesDenegatException {
        
        Usuari user = usuariRepository.findByEmail(email)
                .orElseThrow(() -> new AccesDenegatException("Usuari o contrasenya incorrectes")); // Mensaje genérico por seguridad
        
        if (PasswordHasher.check(rawPassword, user.getPassword())) {
            return user; // Devolvemos el usuario completo si el login es correcto
        } else {
            // Por seguridad, es mejor no decir explícitamente "Bad Password", 
            // sino un mensaje genérico para no dar pistas a atacantes.
            throw new AccesDenegatException("Usuari o contrasenya incorrectes");
        }
    }
    
    public Optional<UserRole> getRole(String email) throws AccesDenegatException{
        
        Usuari user = usuariRepository.findByEmail(email).orElseThrow(()-> new AccesDenegatException("Usuari no existeix"));
        
        return Optional.of(user.getRol());
    }
    
    
}
