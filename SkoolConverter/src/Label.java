public class Label {

    private final int address;
    private final String text;

    public Label(int address, String text) {
        this.address = address;
        this.text = text;
    }

    public int getAddress() {
        return address;
    }

    public String getText() {
        return text;
    }

    public String toString(boolean hex) {
        return text != null ? text.toLowerCase() : "_" + (hex ? Util.hexString(address, 4) : address);
    }
}
