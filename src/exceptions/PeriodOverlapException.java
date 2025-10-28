package exceptions;

public class PeriodOverlapException extends RuntimeException {
    public PeriodOverlapException(String message) {
        super(message);
    }
}
