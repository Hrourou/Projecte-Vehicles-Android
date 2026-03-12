package cat.copernic.backendProjecte3;

import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.entities.Reserva;
import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.EstatVehicle;
import cat.copernic.backendProjecte3.enums.Reputacio;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import cat.copernic.backendProjecte3.repository.ReservaRepository;
import cat.copernic.backendProjecte3.repository.VehicleRepository;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;

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

    public static void main(String[] args) {
        SpringApplication.run(BackendProjecte3Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Inserint dades de prova per a la DEMO...");

        try {
            // 1. Netegem la base de dades (ordre important per les Foreign Keys)
            reservaRepo.deleteAll();
            vehicleRepo.deleteAll();
            // Nota: No esborrem clients per no trencar usuaris si ja n'has registrat a mà

            // 2. CREACIÓ DE 3 CLIENTS (Amb les 3 fotos obligatòries)
            // IMPORTANTE: Asegúrate de que estas imágenes existan en la carpeta raíz de tu proyecto backend, 
            // o cambia la ruta a algo como "src/main/resources/images/maria_dni.jpg"
            Client client1 = crearClientExemple(
                    "maria@test.com", "Maria Garcia", "44556677D",
                    "src/main/resources/demo-imatges/maria_perfil.jpg",
                    "src/main/resources/demo-imatges/maria_dni.jpg",
                    "src/main/resources/demo-imatges/maria_carnet.jpg"
            );
            Client client2 = crearClientExemple(
                    "joan@test.com", "Joan Piñol", "11223344X",
                    "joan_perfil.jpg", "joan_dni.jpg", "joan_carnet.jpg"
            );
            Client client3 = crearClientExemple(
                    "laura@test.com", "Laura Vila", "99887766Z",
                    "laura_perfil.jpg", "laura_dni.jpg", "laura_carnet.jpg"
            );

            // 3. CREACIÓ DE 3 VEHICLES
            // 3. CREACIÓ DE 3 VEHICLES (Ara amb fotos!)
            Vehicle tesla = crearVehicleExemple("1111AAA", "Tesla", "Model 3", "Elèctric", "25.00", "400.00", "src/main/resources/demo-imatges/Tesla_Model_3.png");
            Vehicle toyota = crearVehicleExemple("2222BBB", "Toyota", "Corolla", "Híbrid", "15.00", "200.00", "src/main/resources/demo-imatges/Toyota_Corolla.jpg");
            Vehicle seat = crearVehicleExemple("3333CCC", "Seat", "Ibiza", "Combustió", "10.00", "150.00", "src/main/resources/demo-imatges/Seat_Ibiza.jpg");

            // 4. CREACIÓ DE 6 RESERVES (2 per vehicle, en diferents dates i clients)
            // Reserves pel Tesla
            crearReservaExemple(client1, tesla, 1, 3, "150.00", "400.00"); // D'aquí 1 dia, dura 3 dies
            crearReservaExemple(client2, tesla, 10, 2, "100.00", "400.00"); // D'aquí 10 dies, dura 2 dies

            // Reserves pel Toyota
            crearReservaExemple(client3, toyota, 5, 4, "120.00", "200.00");
            crearReservaExemple(client1, toyota, 15, 1, "30.00", "200.00");

            // Reserves pel Seat
            crearReservaExemple(client2, seat, 2, 5, "100.00", "150.00");
            crearReservaExemple(client3, seat, 20, 2, "40.00", "150.00");

            System.out.println("Dades inserides correctament! La Demo està a punt.");
        } catch (Exception e) {
            System.err.println("ERROR inserint dades de prova: " + e.getMessage());
        }
    }

    // Mètode auxiliar per llegir arxius i convertir-los a byte[] (BLOB)
    private byte[] llegirImatge(String rutaArxiu) {
        if (rutaArxiu == null || rutaArxiu.isEmpty()) {
            return null;
        }
        try {
            File arxiu = new File(rutaArxiu);
            if (arxiu.exists()) {
                return Files.readAllBytes(arxiu.toPath());
            } else {
                System.out.println("AVÍS: No s'ha trobat la imatge " + rutaArxiu + " - Guardant null.");
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error llegint la imatge " + rutaArxiu + ": " + e.getMessage());
            return null;
        }
    }

    private Client crearClientExemple(String email, String nom, String dni, String imgPerfil, String imgDni, String imgCarnet) {
        Client c = clientRepo.findById(email).orElse(new Client());

        c.setEmail(email);

        if (c.getPassword() == null) {
            c.setPassword(PasswordHasher.encode("123456"));
        }

        c.setNomComplet(nom);
        c.setDni(dni);
        c.setReputacio(Reputacio.PREMIUM);
        c.setRol(UserRole.CLIENT);

        // Convertim les rutes (Strings) a byte[] abans de guardar
        // c.setFotoPerfil(llegirImatge(imgPerfil)); // Si tens el camp foto perfil a Client
        c.setImatgeDni(llegirImatge(imgDni));
        c.setImatgeCarnet(llegirImatge(imgCarnet));

        return clientRepo.save(c);
    }

    private Vehicle crearVehicleExemple(String matricula, String marca, String model, String variant, String preu, String fianca, String rutaImatge) {
        Vehicle v = new Vehicle();
        v.setMatricula(matricula);
        v.setMarca(marca);
        v.setModel(model);
        v.setTipusVehicle(TipusVehicle.COTXE);
        v.setVariant(variant);
        v.setEstatVehicle(EstatVehicle.ALTA);
        v.setPreuHora(new BigDecimal(preu));
        v.setFiancaEstandard(new BigDecimal(fianca));
        
        // ¡NUEVO! Leemos el archivo y lo guardamos como BLOB
        v.setFotoBinario(llegirImatge(rutaImatge));
        
        return vehicleRepo.save(v);
    }

    private void crearReservaExemple(Client c, Vehicle v, int offsetDiesInici, int duradaDies, String importTotal, String fianca) {
        Reserva r = new Reserva();
        r.setClient(c);
        r.setVehicle(v);

        LocalDate dataInici = LocalDate.now().plusDays(offsetDiesInici);
        LocalDate dataFi = dataInici.plusDays(duradaDies);

        r.setDataInici(dataInici);
        r.setDataFi(dataFi);
        r.setImportTotal(new BigDecimal(importTotal));
        r.setFiancaPagada(new BigDecimal(fianca));
        reservaRepo.save(r);
    }
}
