public interface MessageInterface {
    public void addSignal(Signal signal);
    public Signal lookup(String PID) throws NoPIDException;
    public String convert(String in, boolean printPIDName) throws NumberFormatException, NoPIDException;
    public int getAddress();
    public String getProtocol();

}
