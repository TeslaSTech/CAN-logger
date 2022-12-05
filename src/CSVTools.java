import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CSVTools {
    public static void writeToCSV(List<String[]> arrayIn, String[] queries, File targetFile) {
        try {
            // Set up writing to the file
            FileWriter fw = new FileWriter(targetFile);

            // set up header
            ArrayList<String> header = new ArrayList<>();
            header.addAll(List.of(queries));

            if (header.size() == arrayIn.get(0).length) {
                // this ^ is a data integrity check
                arrayIn.add(0, header.toArray(new String[0]));
                for (String[] a : arrayIn) {
                    for (int i = 0; i < a.length; i++) {
                        String s = a[i];
                        fw.append(s);
                        if (i != a.length - 1) {
                            fw.append(",");
                        }
                    }
                    fw.append("\n");
                }

                fw.close();

            } else {
                throw new RuntimeException(String.format("Header fields: %d, but data fields: %d\n", header.size(), arrayIn.get(0).length));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void readFromCSV (ArrayList<String[]> out, File fileIn) {
        try {
            Scanner s = new Scanner(fileIn);

            while (s.hasNextLine()) {
                String currentLine = s.nextLine();
                out.add(currentLine.split(","));
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
