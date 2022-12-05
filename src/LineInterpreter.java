import java.util.ArrayList;

public class LineInterpreter {

    private final boolean debug = false;

    private String[] delimiters;

    public LineInterpreter(String[] delimiters) {
        this.delimiters = delimiters;
    }

    /**
     * It's like String.split(), but the delimiter changes after every field. :)
     * @param line The line to split into an ArrayList
     * @return All the individual values in the array
     */
    public ArrayList<String> interpret(String line) {
        ArrayList<String> out = new ArrayList<>();

        /*
        Pseudocode:
        For each delimiter:
            Find the index of the next delimiter
            Take the substring of (0, index of next delimiter) and append it to the ArrayList
            Cut the string down to avoid finding the wrong delimiter.
        Return the ArrayList.
         */

        ArrayList<String> delimitersNotFound = new ArrayList<>();

        for (int i = 0; i < delimiters.length; i++) {
            int nextDelimiter = line.indexOf(delimiters[i]);
            if (nextDelimiter < 0) {
                delimitersNotFound.add(delimiters[i]);
                continue;
            }
            String trimLine = line.substring(0, nextDelimiter);
            if (!trimLine.matches(" +") && !trimLine.isEmpty()) {
                out.add(trimLine.replaceAll(" +", ""));
            }
            line = line.substring(nextDelimiter + delimiters[i].length());
        }
        if (!delimitersNotFound.isEmpty()) {
            if (!out.isEmpty() && debug) {
                System.out.println("Didn't find delimiters: " + delimitersNotFound.toString());
                System.out.println(out.toString());
            }
        }
        return out;
    }
}
