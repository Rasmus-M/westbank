import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new SkoolConverter(true).convert(
            new File("ZX-Spectrum/westbank.skool"),
            new File("src/westbank.a99"),
            0x4000
        );
    }
}
