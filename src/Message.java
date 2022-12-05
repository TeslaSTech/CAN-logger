import java.util.ArrayList;

public class Message {

    /* A DBC message declaration usually looks something like this:
     * BO_ 2024 OBD2: 8 Vector__XXX
     * "BO_" indicates a message
     * The address this message is sent from will be 2024, or 0x7E8. This is the primary Engine Control Unit.
     * 8 is the length of the message, in bytes. They will be hexadecimal.
     */

    private int address;
    private String protocol;
    private int length;
    private ArrayList<Signal> signals;

    public Message(int address, String protocol, int length) {
        this.address = address;
        this.protocol = protocol;
        this.length = length;
        signals = new ArrayList<>();
    }

    public void addSignal(Signal signal) {
        signals.add(signal);
    }

    public Signal lookup(String PID) throws NoPIDException {
        // convert the integer PID to hex String
        for (Signal s : signals) {
            if (s.getPID().equals(PID)) {
                return s;
            }
        }
        throw new NoPIDException("No such PID was found.", PID);
    }

    public String convert(String in) throws NumberFormatException, NoPIDException {
        ArrayList<String> bytes = new ArrayList<>();
        // Step 1: Removing excess whitespace
        in = in.replaceAll(" +", "");
        // Step 2: Every 2 characters = 1 byte
        if (in.length() % 2 == 0) {
            for (int i = 0; i < in.length() - 1; i += 2) {
                bytes.add(in.substring(i, i + 2));
            }
        } else {
            throw new RuntimeException("The data doesn't appear to be complete!");
        }

        int counter;
        int bytesToGo;

        // Finding out the format (whether the message length or response mode are in index 0)
        // System.out.println(bytes);
        if (bytes.get(0).startsWith("4")) {
            // this means that the response mode, usually formatted 4x, is in index 0.
            // 41, for example, would mean a response to a mode 01 request.
            // if the length is not specified, the blank parts are trimmed off usually.
            counter = 0;
            bytesToGo = bytes.size() - 2; // 41 0C 1A F8 - in this case the data is 2 bytes long
        } else if (bytes.get(0).startsWith("01")) {
            // this means that this is a *request* for data, which is not what this program is for.
            throw new RuntimeException("That is a request frame. Try something starting with \"03\" or the like.");
        } else {
            // it would be 03-06 usually, representing 3-6 bytes to follow. CAN uses 7 bytes per frame,
            // the 8th byte is unused every time.
            counter = 1;
            bytesToGo = Integer.parseInt(bytes.get(0)) - 2; // 03 41 0C 1A FF FF FF FF
        }
        // now we find the PID
        String PID = bytes.get(++counter);
        //System.out.println("PID: " + PID); // for now

        // look up the PID
        Signal s;
        s = lookup(PID);

        // now we find the data value
        StringBuilder data = new StringBuilder();
        // if the data is to be interpreted sequentially
        for (int i = ++counter; i < counter + bytesToGo; i++) {
            data.append(bytes.get(i));
        }
        //System.out.println("Data: " + data.toString()); // for now

        int value = (int) (s.getOffset() + s.getScale() * Integer.parseInt(data.toString(), 16) * 100);

        return (value / 100.0) + " " + s.getUnit();
    }

    public int getAddress() {
        return address;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getType() {
        return "Message";
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Signal s : signals) {
            sb.append(s.toString() + "\n\n");
        }

        return sb.toString();
    }
}
