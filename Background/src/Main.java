import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {


    static final int N_CHARACTER_BANKS = 7;
    static final int startBankDay = 4;
    static final int startBankDusk = 18;
    static final int startBankNight = 25;

    public static void main(String[] args) throws IOException {

        byte[] buffer = new byte[0x40000];
        readFile("westbank-v2-8.bin", buffer);

        byte[] day = new byte[0x420];
        readFile("graphics/day.bin", day);

        byte[] day2 = new byte[0x420];
        readFile("graphics/day2.bin", day2);

        byte[] dusk = new byte[0x420];
        readFile("graphics/dusk.bin", dusk);

        byte[] night = new byte[0x420];
        readFile("graphics/night.bin", night);

        byte[][] dayBackgrounds = new byte[][] {
                day,    // Bandit 1
                day,    // Bandit 1
                day,    // Bandit 1
                day,    // Farmer
                day,    // Farmer
                day,    // Farmer / Bandit 1
                day,    // Farmer
                day,    // Farmer
                day,    // Bandit 2 (white)
                day,    // Bandit 2 (white)
                day,    // Daisy
                day,    // Daisy
                day,    // Daisy / Bandit 1
                day,    // Daisy
                day,    // Daisy
                day,    // Bandit 3 (blue)
                day,    // Bandit 3 (blue)
                day,    // Bandit 3 (blue)
                day2,   // Bandit 4 (black)
                day2,   // Bandit 4 (black)
                day2,   // Bandit 4 (black)
                day,    // Bandit 5 (yellow)
                day,    // Bandit 5 (yellow)
                day,    // Bandit 5 (yellow)
                day2,   // Bandit 6 (green)
                day2,   // Bandit 6 (green)
                day2,   // Bandit 6 (green)
                day2,   // Dwarf
                day2,   // Dwarf
                day2,   // Dwarf
                day2,   // Dwarf
                day2,   // Dwarf
                day2,   // Dwarf
                day2,   // Dwarf
                day2,   // Dwarf
                day2,   // Dwarf
                day2,   // Julius
                day2,   // Julius
                day2,   // Julius
                day2,   // Julius
                day2,   // Bandit 6 (green)
                day,    // Bandit 2 (white)
                day,    // Bandit 3 (blue)
                day2,   // Bandit 4 (black)
                day,    // Bandit 5 (yellow)
                day2,   // Julius
                day2,   // Julius
                day     // Unused
        };

        byte[][] duskBackgrounds = new byte[][] {
                dusk, dusk, dusk, dusk, dusk, dusk, dusk, dusk,
                dusk, dusk, dusk, dusk, dusk, dusk, dusk, dusk,
                dusk, dusk, dusk, dusk, dusk, dusk, dusk, dusk,
                dusk, dusk, dusk, dusk, dusk, dusk, dusk, dusk,
                dusk, dusk, dusk, dusk, dusk, dusk, dusk, dusk,
                dusk, dusk, dusk, dusk, dusk, dusk, dusk, dusk
        };

        byte[][] nightBackgrounds = new byte[][] {
                night, night, night, night, night, night, night, night,
                night, night, night, night, night, night, night, night,
                night, night, night, night, night, night, night, night,
                night, night, night, night, night, night, night, night,
                night, night, night, night, night, night, night, night,
                night, night, night, night, night, night, night, night
        };

        System.arraycopy(buffer, startBankDay * 0x2000, buffer, startBankDusk * 0x2000, N_CHARACTER_BANKS * 0x2000);
        System.arraycopy(buffer, startBankDay * 0x2000, buffer, startBankNight * 0x2000, N_CHARACTER_BANKS * 0x2000);

        applyBackgroundsToBanks(startBankDay, dayBackgrounds, buffer);
        applyBackgroundsToBanks(startBankDusk, duskBackgrounds, buffer);
        applyBackgroundsToBanks(startBankNight, nightBackgrounds, buffer);

        FileOutputStream fos = new FileOutputStream("westbank-v2-8.bin");
        fos.write(buffer);
        fos.close();
    }

    private static void applyBackgroundsToBanks(int startBank, byte[][] backgrounds, byte[] buffer) {
        int characterIndex = 0;
        for (int bank = startBank; bank < startBank + N_CHARACTER_BANKS; bank++) {
            for (int ch = 0; ch < 7; ch++) {
                byte[] background = backgrounds[characterIndex];
                int base = bank * 0x2000 + 0x40 + ch * 0x420;
                int patternBase = base;
                int colorBase = base + 0x210;
                for (int row = 0; row < 11; row++) {
                    for (int i = 0; i < 0x30; i++) {
                        int offset = row * 0x30 + i;
                        int patternAddr = patternBase + offset;
                        int colorAddr = colorBase + offset;
                        int patternByte = buffer[patternAddr] & 0xff;
                        int colorByte = buffer[colorAddr] & 0xff;
                        int fgColor = (colorByte & 0xf0) >> 4;
                        int bgColor = (colorByte & 0x0f);
                        if (fgColor == 7) {
                            if (fgColor != bgColor) {
                                int tmp = fgColor;
                                fgColor = bgColor;
                                bgColor = tmp;
                                patternByte ^=  0xff;
                            } else {
                                patternByte = 0;
                            }
                        }
                        int backgroundPatternByte = background[offset] & 0xff;
                        int backgroundColorByte = background[offset + 0x210] & 0xff;
                        int backgroundFgColor = (backgroundColorByte & 0xf0) >> 4;
                        int backgroundBgColor = (backgroundColorByte & 0x0f);
                        if (backgroundFgColor == 7) {
                            if (backgroundFgColor != backgroundBgColor) {
                                int tmp = backgroundFgColor;
                                backgroundFgColor = backgroundBgColor;
                                backgroundBgColor = tmp;
                                backgroundPatternByte ^=  0xff;
                            } else {
                                backgroundPatternByte = 0;
                            }
                        }
                        if (bgColor == 7) {
                            if (patternByte == 0) {
                                buffer[patternAddr] = (byte) backgroundPatternByte;
                                buffer[colorAddr] = (byte) ((backgroundFgColor << 4) | backgroundBgColor);
                            } else if (backgroundPatternByte != 0xff) {
                                buffer[patternAddr] = (byte) patternByte;
                                buffer[colorAddr] = (byte) ((fgColor << 4) | backgroundBgColor);
                            } else {
                                buffer[patternAddr] = (byte) patternByte;
                                buffer[colorAddr] = (byte) ((fgColor << 4) | backgroundFgColor);
                            }
                        }
                    }
                }
                characterIndex++;
                if (characterIndex > 0x2f) {
                    break;
                }
            }
        }
    }

    private static void readFile(String filename, byte[] buffer) throws IOException {
        FileInputStream fis = new FileInputStream(filename);
        fis.read(buffer);
        fis.close();
    }
}
