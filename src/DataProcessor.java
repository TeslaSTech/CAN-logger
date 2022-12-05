/**
 * DBC data parser
 * @author Valmik Revankar
 * Date: 11/3/2022
 * <p>
 * Pseudocode/planning:
 * Step 1. Set up BufferedReader to read in line-by-line
 * Step 2. Classify lines
 * - BO_ = message
 * - SG_ = signal from message
 *  - the S1/Service signal uses EXTENDED multiplexing
 *    so look out for a couple things:
 *      - MultiplexOR signal (may need to sweep through once to find them?)
 *      - Multiplexed signals that are also MultiplexORs
 *      - The actual multiplexed signals
 * - SG_MUL_VAL = multiplexed signal
 * - CM_ = comment
 * - BA_ = enumeration (needs an accompanying VAL_ field)
 * -
 * </p>
 */

import java.io.*;
import java.util.ArrayList;

public class DataProcessor {

    public static final String[] VERSION_DELIMS = new String[]{ "\"", "\"" };
    public static final String[] MESSAGE_DELIMS = new String[]{ "BO_ ", " ", ": ", " " };
    public static final String[] SIGNAL_DELIMS = new String[]{ " SG_ ", " ", " ", ":", "|", "@" , "(", ",", ")", "[", "|", "]", "\"", "\"" };

    private FileReader fr;
    private BufferedReader br;
    private ArrayList<Message> keys;

    public DataProcessor() {
        try {
            fr = new FileReader("OBD2-DBC-MDF4/CSS-Electronics-OBD2-v1.4.dbc");
            br = new BufferedReader(fr);
            keys = generateKeys();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Message> generateKeys() {
        ArrayList<Message> data = new ArrayList<>();
        try {

            LineInterpreter messages = new LineInterpreter(MESSAGE_DELIMS);
            LineInterpreter signals = new LineInterpreter(SIGNAL_DELIMS);

            boolean seenMessage = false;

            while (br.ready()) {
                String currentLine = br.readLine();
                String signalType = "undef";
                ArrayList<String> out;

                // first search for a message
                if (currentLine.startsWith("BO_") && !seenMessage) {
                    seenMessage = true;
                    signalType = "message";
                    out = messages.interpret(currentLine);
                } else if (currentLine.startsWith(" SG_") && seenMessage) {
                    signalType = "signal";
                    out = signals.interpret(currentLine);
                } else {
                    continue;
                }
                if (!out.isEmpty()) {
                    // convert it to its respective type of signal
                    if (signalType.equals("message")) {
                        data.add(new Message(Integer.parseInt(out.get(0)), out.get(1), Integer.parseInt(out.get(2))));
                    } else if (!data.isEmpty()) {
                        try {
                            data.get(0).addSignal(new Signal(out));
                        } catch (NoPIDException e) {
                            continue;
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    public ArrayList<Message> getKeys() {
        return keys;
    }


}
