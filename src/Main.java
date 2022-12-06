import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    // i call it the CAN-opener
    static Scanner s = new Scanner(System.in);

    public static void main(String[] args) {

        boolean exit = false;

        do {
            // Print menu
            System.out.println("/------------------\\");
            System.out.println("| CAN-Opener v0.1b |");
            System.out.println("\\------------------/\n");
            System.out.println("Choose something to do:");
            System.out.println("1. Read data from an ELM327 device and save it to CSV");
            System.out.println("2. Read in a raw CSV log file and convert it to spreadsheet data");
            System.out.println("3. Interactive mode (enter frame, get conversion)");
            System.out.println("4. Quit");

            boolean exitMenu = true;
            do {
                System.out.print("Make a choice: ");
                char choice = s.next().charAt(0);

                switch (choice) {
                    case '1':
                        // Case 1: Read data from ELM → CSV
                        logValues();
                        break;
                    case '2':
                        // Case 2: Read data from CSV → Readable CSV
                        readFromFile();
                        break;
                    case '3':
                        // Case 3: Interactive mode
                        interactive();
                        break;
                    case '4':
                        System.out.println("Thank you for using this program!");
                        System.exit(0);
                        break;
                    default:
                        exitMenu = false;
                        System.out.println("That choice didn't appear to be valid.");
                        continue;
                }
            } while (!exitMenu);
        } while (true);
    }

    public static void logValues() {
        System.out.println();
        System.out.println("Warning: Currently this program only samples four pieces of data due to complexity/efficiency limitations.");
        System.out.println("They are: Engine Coolant Temp, Engine RPM, Vehicle Speed, and Throttle Position.\n");

        // Step 1: List available COM serial ports and have the user select the appropriate one.
        SerialReader reader = new SerialReader();
        System.out.println("Available serial ports:");
        reader.listPorts();
        System.out.print("Select a serial port from the above list (Type the name out): ");
        String comPort = s.next().toUpperCase();
        System.out.print("\nHow many seconds' worth of data should be collected? (1 second ≅ 3 sets of samples): ");
        int timeEnd = s.nextInt();
        String filePath;
        do {
            System.out.print("Type a valid .csv filename to save the data to (If it exists, it will be overwritten): ");
            filePath = s.next();
        } while (!filePath.matches("\\w+[.](csv)"));

        reader.logObdValues(0, timeEnd, 3, comPort, new File(filePath));

        System.out.printf("\nData successfully saved to %s. Returning to main in 5 seconds...\n", filePath);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        clearScreen();
    }

    public static void readFromFile() {
        String filePathIn;
        String filePathOut;

        do {
            System.out.print("Enter a valid CSV file to read from: ");
            filePathIn = s.next();
        } while (!new File(filePathIn).exists() || !filePathIn.matches("\\w+[.](csv)"));
        do {
            System.out.print("Enter a valid CSV file to output to (if it exists, it'll be overwritten): ");
            filePathOut = s.next();
        } while (!filePathOut.matches("\\w+[.](csv)"));

        DataProcessor d = new DataProcessor();
        File f = new File(filePathIn);
        ArrayList<String[]> inputArray = new ArrayList<>();
        CSVTools.readFromCSV(inputArray, f);

        try {
            // Reading in the first line that contains the key PID and making it human-readable
            String[] keys = inputArray.get(0);

            for (int i = 1; i < keys.length; i++) {
                keys[i] = d.getKeys().get(0).lookup(keys[i]).getName();
            }

            // Write the keys in the human-readable format (mode 01 PID → English)
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
        /*System.out.println(Arrays.toString(queries));
        for (String[] s : inputArray) {
            System.out.println(Arrays.toString(s));
        }*/

        CSVTools.writeToCSV(inputArray, queries, new File(filePathOut));

        System.out.printf("\nData successfully saved to %s. Returning to main in 5 seconds...\n", filePathOut);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        clearScreen();
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


            System.out.print("Enter CAN frame (\"exit\" to return to menu): ");
            in = s.nextLine();
        } while (!in.toLowerCase().equals("exit"));

    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}
