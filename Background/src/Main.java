import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    static int[] colors = {7, 7, 7, 7, 7, 7, 7, 7, 11, 11, 11};

    public static void main(String[] args) throws IOException {

        byte[] buffer = new byte[0x20000];

        FileInputStream fis = new FileInputStream("westbank8.bin");
        fis.read(buffer);
        fis.close();

        for (int bank = 4; bank < 11; bank++) {
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

        FileOutputStream fos = new FileOutputStream("westbankx8.bin");
        fos.write(buffer);
        fos.close();
    }
}
