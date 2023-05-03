import java.io.File;
import java.io.IOException;

public class ConvertToCharacters {
    public static void main(String[] args) throws IOException {
        GraphicsConverter converter = new GraphicsConverter();
        converter.convertToCharacters(
            new File("graphics/bang-gfx.a99"),
            new File("src/bang-gfx.a99")
        );
        converter.convertToCharacters(
            new File("graphics/characters-gfx.a99"),
            new File("src/characters-gfx.a99")
        );
        converter.convertToCharacters(
            new File("graphics/door-gfx.a99"),
            new File("src/door-gfx.a99")
        );
        converter.convertToCharacters(
            new File("graphics/misc-gfx.a99"),
            new File("src/misc-gfx.a99")
        );
        converter.convertToCharacters(
            new File("graphics/tellers-gfx.a99"),
            new File("src/tellers-gfx.a99")
        );
    }
}
