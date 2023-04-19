public class Main {

    private static final String[] zxColourNames = {
            "Black",
            "Blue",
            "Red",
            "Magenta",
            "Green",
            "Cyan",
            "Yellow",
            "Grey",
            "Black",
            "B Blue",
            "B Red",
            "B Magenta",
            "B Green",
            "B Cyan",
            "B Yellow",
            "White"
    };


    public static void main(String[] args) {
        printAttributeDescription(0x38);
    }

    public static void printAttributeDescription(int a) {
        int ink = a & 0x07;
        int paper = (a & 0x38) >> 3;
        int bright = (a & 0x40) >> 3; // 0 or 8
        int flash = a & 0x80;
        System.out.println("Attribute $" + Integer.toHexString(a) + " " + a + " " + Integer.toBinaryString(a));
        System.out.println("Ink: " + zxColourNames[ink + bright] + (flash != 0 ? " (flash)" : ""));
        System.out.println("Paper: " + zxColourNames[paper + bright] + (flash != 0 ? " (flash)" : ""));
    }
}
