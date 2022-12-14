public interface SignalInterface {


    public String getPID();

    public String getName();

    public int getBitStart();

    public int getLength();

    public boolean isEndian();

    public boolean isSign();

    public double getScale();

    public int getOffset();

    public double getMin();

    public double getMax();

    public String getUnit();
}
