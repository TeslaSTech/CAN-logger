// https://github.com/Fazecast/jSerialComm/wiki/Nonblocking-Reading-Usage-Example

import com.fazecast.jSerialComm.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Toolkit that utilizes the jSerialComm library to log data from an ELM327 OBD2-USB converter over an emulated RS-232.
 */
public class SerialReader implements SerialReaderInterface {

    SerialPort[] ports;

    /**
     * Creates a new SerialReader object and populates the ports array.
     */
    public SerialReader() {
        this.ports = SerialPort.getCommPorts();
    }

    /**
     * Lists all available COM ports.
     */
    public void listPorts() {
        for (SerialPort port : ports) {
            System.out.println(port.getDescriptivePortName() + ": " + port.getPortDescription());
        }
    }

    /**
     * Log diagnostic values to a CSV file.
     * @param timeStart Time marker to start collecting data (t=0)
     * @param timeEnd Time marker to stop collecting data (time end)
     * @param pollFrequency Frequency in Hertz to collect data. MAXIMUM 20 Hz.
     * @param targetPort Target COM port where ELM327 is located.
     * @param targetFile The target file to write to. WILL ERASE ANY DATA.
     */
    public void logObdValues(int timeStart, int timeEnd, int pollFrequency, String targetPort, File targetFile) {
        // Gets a few important Service 01 OBD parameters - https://en.wikipedia.org/wiki/OBD-II_PIDs#Service_01_-_Show_current_data
        /*
        * 05 - Coolant temperature
        * 0C - Engine speed
        * 0D - Vehicle speed
        * 11 - Throttle position
         */

        String[] queries = {"05", "0C", "0D", "11"};
        List<String[]> responses = new ArrayList<>();

        // Step 1: Open a COM port.
        SerialPort comPort = SerialPort.getCommPort(targetPort);
        // Opening the port
        if (comPort.openPort()) {
            // Set some important stuff up: the baud rate, stop bits, parity (integrity checker), and timeouts.
            System.out.printf("Opened port %s at %d baud.\n", comPort.getDescriptivePortName(), comPort.getBaudRate());
            comPort.setComPortParameters(9600, Byte.SIZE, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 1000);
            OutputStream output = comPort.getOutputStream();
            InputStream input = comPort.getInputStream();

            // Flush EVERYTHING to make sure the buffers are clean
            comPort.flushIOBuffers();

            try {

                // Reset the ELM327
                //output.write("AT Z\r".getBytes());

                // Keep track if running behind or on time on data sampling
                double lagTime = 0.0;
                int collectedSamples = 0;

                System.out.printf("\nCollecting data now, %d seconds remaining...\n\n", (timeEnd-timeStart));

                for (double i = timeStart; i < timeEnd; i += (1.0/pollFrequency)) {
                    long startTime = System.currentTimeMillis();
                    String[] temp = new String[queries.length + 1];
                    temp[0] = Double.toString(Math.floor(i * 100) / 100.0);
                    for (int j = 1; j < temp.length; j++) {
                        String query = "01 " + queries[j - 1] + "\r";
                        int queryLength = query.getBytes().length;
                        output.write(query.getBytes());
                        Thread.sleep(80);
                        if (input.available() > 0) {
                            byte[] newData = new byte[comPort.bytesAvailable()];
                            int numRead = comPort.readBytes(newData, newData.length);
                            String curLine = new String(newData, StandardCharsets.UTF_8);
                            curLine = curLine.substring(curLine.indexOf("\r") + 1, curLine.lastIndexOf("\r")).replaceAll("\\t+|\\r+|\\n+", "");
                            temp[j] = curLine;
                            //System.out.println(curLine);
                        }
                    }
                    responses.add(temp);
                    collectedSamples += temp.length - 1;

                    // Wait for the required number of milliseconds to stay at the target sample rate.
                    long timeToWait = (long) ((1.0/pollFrequency) * 1000) - (80 * queries.length);
                    if (timeToWait > 0) {
                        Thread.sleep(timeToWait);
                    }

                    // Get the time taken for this iteration of the loop
                    double timeTaken = (System.currentTimeMillis()-startTime) / 1000.0;
                    lagTime += timeTaken - (1.0/pollFrequency);
                    //System.out.printf("This loop iteration took %f sec, it should have taken %f sec \n", timeTaken, (1.0/pollFrequency));
                }
                output.close();
                input.close();
                int correctSampleNumber = (timeEnd - timeStart) * pollFrequency * queries.length + queries.length;

                /*System.out.println("[----------DIAGNOSTICS REPORT----------]");
                System.out.printf("The system was %.2f s off target (%.2f seconds actual vs %.2f seconds theoretical).\n", lagTime, lagTime + (timeEnd-timeStart), (double) (timeEnd-timeStart));
                System.out.printf("The system collected %d pieces of data whereas it should have collected %d.\n", collectedSamples, correctSampleNumber);
                System.out.println("[-----Raw data-----]");
                for (String[] data : responses) {
                    System.out.println(Arrays.toString(data));
                }*/

                List<String> csvCols = new ArrayList<>();
                csvCols.add("Timestamp");
                Collections.addAll(csvCols, queries);

                CSVTools.writeToCSV(responses, csvCols.toArray(new String[0]), targetFile);

            } catch (IOException | InterruptedException e) {
                System.out.printf("There was an error: %s", e.toString());
                System.exit(1);
                //throw new RuntimeException(e);
            } finally {
                comPort.closePort();
            }

        } else {
            System.out.println("COM port not available to be opened.");
            return;
        }

    }

}
