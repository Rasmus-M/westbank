import java.io.File;
import java.io.IOException;

public class ConvertToImages {

    public static void main(String[] args) throws IOException {
        GraphicsConverter converter = new GraphicsConverter();
        converter.convertToImages(new File("graphics/door-gfx.a99"));
    }
}
