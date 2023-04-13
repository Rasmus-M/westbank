import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new GraphicsConverter().convert(
            new File("graphics/characters.a99"),
            new File("src/characters.a99")
        );
    }
}
