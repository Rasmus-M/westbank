import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GraphicsConverter {

    public void convert(File inputFile, File outputFile) throws IOException {
        List<TMS9900Line> inputLines = readInputFile(inputFile);
        List<TMS9900Line> outputLines = convert(inputLines);
        writeOutputFile(outputFile, outputLines);
    }
    private List<TMS9900Line> convert(List<TMS9900Line> inputLines) {
        List<TMS9900Line> outputLines = new ArrayList<>();
        List<TMS9900Line> characterRow = null;
        int i = 0;
        while (i < inputLines.size()) {
            TMS9900Line inputLine = inputLines.get(i);
            switch (inputLine.getType()) {
                case Comment:
                    break;
                case Label:
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
        for (int[] bytes : transposedGrid) {
            outputLines.add(new TMS9900Line(bytes));
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
}
