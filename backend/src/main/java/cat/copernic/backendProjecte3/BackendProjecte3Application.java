package cat.copernic.backendProjecte3;

import cat.copernic.backendProjecte3.repository.ClientRepository;
import cat.copernic.backendProjecte3.repository.ReservaRepository;
import cat.copernic.backendProjecte3.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendProjecte3Application implements CommandLineRunner {

    @Autowired
    private VehicleRepository vehicleRepo;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private ReservaRepository reservaRepo;

    /**
     * Inici aplicació Java.
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendProjecte3Application.class, args);
    }

    /**
     * Inici SpringBoot.
     * 
     * ⚠️ Dades de prova DESACTIVADES temporalment
     * per poder provar només RF90 (vehicles).
     */
    @Override
    public void run(String... args) throws Exception {
        // System.out.println("Inserint dades de prova...");
        //
        // try {            
        //     Client clientVip = crearClientExemple();
        //     Vehicle cotxeElectring = crearVehicleExemple();
        //     crearReservaExemple(clientVip, cotxeElectring);
        //
        //     System.out.println("Dades inserides correctament.");
        // } catch (Exception e) {
        //     System.err.println("ERROR inserint dades de prova: " + e.getMessage());
        // }
    }

    /*
    // ================================
    // MÈTODES DE PROVA DESACTIVATS
    // ================================

    private Client crearClientExemple() {
        Client c = new Client();
        c.setEmail("maria@test.com");
        c.setPassword(PasswordHasher.encode("123456"));
        c.setNomComplet("Maria Garcia"); // Faltava això
        c.setDni("44556677D");
        c.setReputacio(Reputacio.PREMIUM);
        c.setRol(UserRole.CLIENT);
        return clientRepo.save(c);
    }
    
    private Vehicle crearVehicleExemple() {
        Vehicle v = new Vehicle();
        v.setMatricula("9988GTI");
        v.setTipusVehicle(TipusVehicle.COTXE);
        v.setMotor("Elèctric");
        v.setPreuHora(new BigDecimal("25.00"));
        v.setFiancaEstandard(new BigDecimal("400.00"));
        return vehicleRepo.save(v);
    }

    private void crearReservaExemple(Client c, Vehicle v) {
        Reserva r = new Reserva();
        r.setClient(c);
        r.setVehicle(v);
        r.setDataInici(LocalDate.now().plusWeeks(1));
        r.setDataFi(LocalDate.now().plusWeeks(1).plusDays(3));
        r.setImportTotal(new BigDecimal("150.00"));
        r.setFiancaPagada(new BigDecimal("300.00"));
        reservaRepo.save(r);
    }
    */
}