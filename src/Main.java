import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
public class Main {

    public static void main(String[] args) {
        logValues();
        readFromFile();
    }

    public static void logValues() {
        SerialReader s = new SerialReader();

        s.logObdValues(0, 5, 3, "CNCB0", new File("output.csv"));
    }

    public static void readFromFile() {
        DataProcessor d = new DataProcessor();
        File f = new File("output.csv");
        ArrayList<String[]> inputArray = new ArrayList<>();
        CSVTools.readFromCSV(inputArray, f);

        /*
        for (String[] s : inputArray) {
            System.out.println(Arrays.toString(s));
        }
        */

        try {
            // Reading in the first line that contains the key PID and making it human readable
            String[] keys = inputArray.get(0);

            for (int i = 1; i < keys.length; i++) {
                keys[i] = d.getKeys().get(0).lookup(keys[i]).getName();
            }

            inputArray.set(0, keys);

            for (int i = 1; i < inputArray.size(); i++) {
                String[] current = new String[inputArray.get(i).length];
                current[0] = inputArray.get(i)[0];
                for (int j = 1; j < inputArray.get(i).length; j++) {
                    current[j] = d.getKeys().get(0).convert(inputArray.get(i)[j]);
                }
                inputArray.set(i, current);
            }
        } catch (NoPIDException e) {
            throw new RuntimeException(e);
        }

        String[] queries = inputArray.remove(0);
        System.out.println(Arrays.toString(queries));
        for (String[] s : inputArray) {
            System.out.println(Arrays.toString(s));
        }

        CSVTools.writeToCSV(inputArray, queries, new File("output_decoded.csv"));

    }

    public static void interactive() {
        DataProcessor d = new DataProcessor();
        Scanner s = new Scanner(System.in);
        // String exampleData = "41 05 87";
        System.out.print("Enter CAN frame: ");
        String in = s.nextLine();

        do {
            try {
                System.out.println(d.getKeys().get(0).convert(in));
            } catch (NumberFormatException e) {
                System.out.println("The data couldn't be interpreted correctly. Check the frame?");
            } catch (NoPIDException e) {
                System.out.println("The PID " + e.PID + " couldn't be found.");
            } catch (RuntimeException e){
                System.out.println(e.getMessage());
            }



            System.out.print("Enter CAN frame: ");
            in = s.nextLine();
        } while (!in.toLowerCase().equals("exit"));

    }

}
