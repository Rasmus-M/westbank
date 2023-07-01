import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {


    static final int N_CHARACTER_BANKS = 7;
    static final int startBankDay = 4;
    static final int[] dayColors = {7, 7, 7, 7, 7, 7, 7, 7, 11, 11, 11};
    static final int startBankDusk = 18;
    static final int[] duskColors = {4, 4, 4, 4, 4, 4, 4, 4, 10, 10, 10};
    static final int startBankNight = 25;
    static final int[] nightColors = {0, 0, 0, 0, 0, 0, 0, 0, 14, 14, 14};

    public static void main(String[] args) throws IOException {

        byte[] buffer = new byte[0x40000];

        FileInputStream fis = new FileInputStream("westbank8.bin");
        fis.read(buffer);
        fis.close();

        System.arraycopy(buffer, startBankDay * 0x2000, buffer, startBankDusk * 0x2000, N_CHARACTER_BANKS * 0x2000);
        System.arraycopy(buffer, startBankDay * 0x2000, buffer, startBankNight * 0x2000, N_CHARACTER_BANKS * 0x2000);

        applyColorsToBanks(startBankDay, dayColors, buffer);
        applyColorsToBanks(startBankDusk, duskColors, buffer);
        applyColorsToBanks(startBankNight, nightColors, buffer);

        FileOutputStream fos = new FileOutputStream("westbank8.bin");
        fos.write(buffer);
        fos.close();
    }

    private static void applyColorsToBanks(int startBank, int[] colors, byte[] buffer) {
        for (int bank = startBank; bank < startBank + N_CHARACTER_BANKS; bank++) {
            for (int ch = 0; ch < 7; ch++) {
                int base = bank * 0x2000 + 0x40 + ch * 0x0420 + 0x210;
                for (int row = 0; row < 11; row++) {
                    for (int i = 0; i < 0x30; i++) {
                        int addr = base + row * 0x30 + i;
                        int b = buffer[addr] & 0xff;
                        int h = (b & 0xf0) >> 4;
                        int l = (b & 0x0f);
                        if (h == 7) h = colors[row];
                        if (l == 7) l = colors[row];
                        b = (h << 4) | l;
                        buffer[addr] = (byte) b;
                    }
                }
            }
        }
    }
}
