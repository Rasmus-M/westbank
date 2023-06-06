import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GraphicsConverter {

    public static final Color[] PALETTE = {
            new Color(0,0,0),
            new Color(0,0,0),
            new Color(33,200,66),
            new Color(94,220,120),
            new Color(84,85,237),
            new Color(125,118,252),
            new Color(212,82,77),
            new Color(66,235,245),
            new Color(252,85,84),
            new Color(255,121,120),
            new Color(212,193,84),
            new Color(230,206,128),
            new Color(33,176,59),
            new Color(201,91,186),
            new Color(204,204,204),
            new Color(255,255,255)
    };

    public void convertToCharacters(File inputFile, File outputFile) throws IOException {
        List<TMS9900Line> inputLines = readInputFile(inputFile);
        List<TMS9900Line> outputLines = convertToCharacters(inputLines);
        writeOutputFile(outputFile, outputLines);
    }
    private List<TMS9900Line> convertToCharacters(List<TMS9900Line> inputLines) {
        List<TMS9900Line> outputLines = new ArrayList<>();
        List<TMS9900Line> characterRow = null;
        int i = 0;
        while (i < inputLines.size()) {
            TMS9900Line inputLine = inputLines.get(i);
            switch (inputLine.getType()) {
                case Comment:
                    outputLines.add(inputLine);
                    break;
                case Label:
                    inputLine.setComment(null);
                    outputLines.add(inputLine);
                    break;
                case Data:
                    if (characterRow == null) {
                        characterRow = new ArrayList<>();
                    }
                    characterRow.add(inputLine);
                    if (characterRow.size() == 8) {
                        writeCharacterRow(outputLines, characterRow);
                        characterRow = null;
                    }
                    break;
            }
            i++;
        }
        return outputLines;
    }

    private void writeCharacterRow(List<TMS9900Line> outputLines, List<TMS9900Line> characterRow) {
        int width = characterRow.get(0).getBytes().length;
        int[][] transposedGrid = new int[width][characterRow.size()];
        for (int y = 0; y < characterRow.size(); y++) {
            TMS9900Line pixelRow = characterRow.get(y);
            for (int x = 0; x < pixelRow.getBytes().length; x++) {
                transposedGrid[x][y] = pixelRow.getBytes()[x];
            }
        }
        for (int i = 0; i < transposedGrid.length; i++) {
            outputLines.add(new TMS9900Line(transposedGrid[i], i == 0 ? width + " columns" : null));
        }
    }

    public void convertToImages(File inputFile) throws IOException {
        List<TMS9900Line> inputLines = readInputFile(inputFile);
        List<int[]> grid = null;
        String lastLabel = null;
        int i = 0;
        while (i < inputLines.size()) {
            TMS9900Line inputLine = inputLines.get(i);
            switch (inputLine.getType()) {
                case Label:
                    lastLabel = inputLine.getLabel();
                case Empty:
                case Comment:
                    if (grid != null && grid.size() > 0) {
                        writeImageFile(lastLabel != null ? lastLabel : "image", grid);
                        grid = null;
                        lastLabel = null;
                    }
                    break;
                case Data:
                    if (grid == null) {
                        grid = new ArrayList<>();
                    }
                    grid.add(inputLine.getBytes());
                    break;
            }
            i++;
        }
    }

    private List<TMS9900Line> readInputFile(File inputFile) throws IOException {
        List<TMS9900Line> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line = reader.readLine();
        while (line != null) {
            lines.add(new TMS9900Line(line));
            line = reader.readLine();
        }
        return lines;
    }

    private void writeOutputFile(File outputFile, List<TMS9900Line> lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        for (TMS9900Line tms9900Line : lines) {
            writer.write(tms9900Line.toString());
            writer.newLine();
        }
        writer.close();
    }

    private void writeImageFile(String fileName, List<int[]> grid) throws IOException {
        BufferedImage image = new BufferedImage(grid.get(0).length * 8, grid.size(), BufferedImage.TYPE_BYTE_INDEXED, createIndexColorModel());
        WritableRaster raster = image.getRaster();
        int[] pixel = new int[1];
        for (int y = 0; y < grid.size(); y++) {
            int[] row = grid.get(y);
            for (int c = 0; c < row.length; c++) {
                int b = row[c];
                int x = c * 8;
                for (int p = 0; p < 8; p++) {
                    int bit = b & (0x80 >> p);
                    pixel[0] = bit != 0 ? 1 : 7;
                    raster.setPixel(x, y, pixel);
                    x++;
                }
            }
        }
        ImageIO.write(image, "png", new File("graphics/images/" + fileName + ".png"));
    }

    private IndexColorModel createIndexColorModel() {
        byte[] r = new byte[16];
        byte[] g = new byte[16];
        byte[] b = new byte[16];
        for (int i = 0; i < 16; i++) {
            r[i] = (byte) PALETTE[i].getRed();
            g[i] = (byte) PALETTE[i].getGreen();
            b[i] = (byte) PALETTE[i].getBlue();
        }
        return new IndexColorModel(4, 16, r, g, b);
    }

}
