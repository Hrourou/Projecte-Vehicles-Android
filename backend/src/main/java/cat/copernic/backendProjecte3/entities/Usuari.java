/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.entities;
import cat.copernic.backendProjecte3.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 *
 * @author manel
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "usuari")
public class Usuari{
    
    @Id
    @Column(length = 100)
    private String email;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole rol = UserRole.NONE;

    // Constructor con campos
    public Usuari(String email) {
        this.email = email;
    }
    
    public Usuari() {}

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    
    public List<UserRole> getAuthorities() {
        
        List<UserRole> roles = new ArrayList<>();
        
        roles.add(this.getRol());
        
        return roles;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public String getUsername() {
        return this.email;
    }

    public UserRole getRol() {
        return rol;
    }

    public void setRol(UserRole rol) {
        this.rol = rol;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuari usuari = (Usuari) o;
        return Objects.equals(email, usuari.email);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usuari{");
        sb.append("email=").append(email);
        sb.append(", password=").append(password);
        sb.append(", rol=").append(rol);
        sb.append('}');
        return sb.toString();
    }
}
