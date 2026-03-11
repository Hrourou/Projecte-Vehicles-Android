package cat.copernic.backendProjecte3.exceptions;

public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException(String message) {
        super(message);
    }
}