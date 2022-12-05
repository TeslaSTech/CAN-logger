import java.util.ArrayList;

public class Signal {
    // breaks down the string into name, multiplex information, bit start, length, endian/sign together, scale, offset, min, max, unit

    private String PID; // will be stored as hex String
    private String name;
    private int bitStart;
    private int length;
    private boolean endian; // false = little, true = big
    private boolean sign; // false = unsigned, true = signed
    private double scale;
    private int offset;
    private double min;
    private double max;
    private String unit;


    public Signal(ArrayList<String> info) throws NoPIDException {
        // break down an arraylist into usable data
        //System.out.println(info);
        // Step 1: Find PID
        int PID_index = info.get(0).indexOf("PID_");

        if (PID_index >= 0) {
            PID = info.get(0).substring(PID_index + 4, PID_index + 6);
            name = info.get(0).substring(PID_index + 7);
        } else {
            throw new NoPIDException("PID index < 0, i.e. it does not exist.", "N/A");
        }

        int indexOffset = 0;
        if (info.get(1).contains("m") || info.get(1).contains("M")) {
            indexOffset = 1;
        }

        // Step 2: Parse everything else in order)
        bitStart = Integer.parseInt(info.get(1 + indexOffset));
        length = Integer.parseInt(info.get(2 + indexOffset));
        endian = info.get(3 + indexOffset).charAt(0) == '0';
        sign = info.get(3 + indexOffset).charAt(1) == '-';
        scale = Double.parseDouble(info.get(4 + indexOffset));
        offset = Integer.parseInt(info.get(5 + indexOffset));
        min = Double.parseDouble(info.get(6 + indexOffset));
        max = Double.parseDouble(info.get(7 + indexOffset));
        if (info.size() == 9 + indexOffset)
            unit = info.get(8 + indexOffset);
        else
            unit = "";
    }

    public String getType() {
        return "Signal";
    }

    public String toString() {
        String endianString;
        String signString;
        if (endian)
            endianString = "Big endian";
        else
            endianString = "Little endian";
        if (sign) {
            signString = "Signed";
        } else
            signString = "Unsigned";

        return "Name: " + name + "\tPID: " + PID + "\tBit start: " + bitStart + "\t" + endianString + "\t" + signString + "\tScale: " + scale + "\tOffset: " + offset + "\tMinimum value: " + min + "\tMaximum value: " + max + "\tUnit: " + unit;
    }

    public String getPID() {
        return PID;
    }

    public String getName() {
        return name;
    }

    public int getBitStart() {
        return bitStart;
    }

    public int getLength() {
        return length;
    }

    public boolean isEndian() {
        return endian;
    }

    public boolean isSign() {
        return sign;
    }

    public double getScale() {
        return scale;
    }

    public int getOffset() {
        return offset;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public String getUnit() {
        return unit;
    }
}