import java.io.File;

public interface SerialReaderInterface {

    public void listPorts();

    public void logObdValues(int timeStart, int timeEnd, int pollFrequency, String targetPort, File targetFile);

}
