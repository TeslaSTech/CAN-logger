public interface MessageInterface {
    public void addSignal(Signal signal);
    public Signal lookup(String PID) throws NoPIDException;
    public String convert(String in) throws NumberFormatException, NoPIDException;
    public int getAddress();
    public String getProtocol();

}
