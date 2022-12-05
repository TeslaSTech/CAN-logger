public class NoPIDException extends Exception {
    public final String PID;
    public NoPIDException(String errorMessage, String PID) {
        super(errorMessage);
        this.PID = PID;
    }
}
