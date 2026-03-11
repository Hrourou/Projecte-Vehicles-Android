package cat.copernic.backendProjecte3.business;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordRecoveryEmail(String to, String userName, String token) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("AppVehicles - Password recovery");

        message.setText(
                "Hello " + userName + ",\n\n" +
                "We received a request to recover your password.\n\n" +
                "Use this recovery token in the mobile app:\n\n" +
                token + "\n\n" +
                "This token expires in 30 minutes.\n\n" +
                "If you did not request it, ignore this email.\n\n" +
                "AppVehicles Team"
        );

        mailSender.send(message);
    }
}