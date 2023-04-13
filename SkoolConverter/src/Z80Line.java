import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Z80Line {

    public enum Type {
        Empty,
        Directive,
        Label,
        Comment,
        ContinuationComment,
        Data,
        Instruction
    }

    private static final Pattern addressPattern = Pattern.compile("^([0-9]{5}|\\$[0-9a-fA-F]{4})$");

    private Type type;
    private char typeChar;
    private int address;
    private String instruction;
    private String directive;
    private String comment;
    private String label;

    public Z80Line(String line) {
        parseLine(line);
    }

    private void parseLine(String line) {
        if (line.isEmpty()) {
            type = Type.Empty;
        } else {
            typeChar = line.charAt(0);
            if (typeChar == '@') {
                directive = line.length() > 1 ? line.substring(1) : "";
                if (directive.startsWith("label=")) {
                    label = directive.substring(6);
                    type = Type.Label;
                } else {
                    type = Type.Directive;
                }
            } else if (typeChar == ';') {
                comment = line.length() > 1 ? line.substring( 1).trim() : "";
                type = Type.Comment;
            } else {
                int commentPos = line.indexOf(" ;");
                if (commentPos != -1) {
                    if (line.length() > commentPos + 2) {
                        comment = line.substring(commentPos + 2).trim();
                    } else {
                        comment = "";
                    }
                    line = line.substring(0, commentPos);
                }
                if (line.length() >= 6) {
                    String addrString = line.substring(1, 6);
                    Matcher m = addressPattern.matcher(addrString);
                    if (m.find()) {
                        address = Util.parseInt(addrString);
                    } else if (comment != null) {
                        type = Type.ContinuationComment;
                    }
                }
                if (type == null) {
                    instruction = line.length() >= 8 ? line.substring(7).trim() : "";
                    if (instruction.startsWith("DEFB") || instruction.startsWith("DEFW") || instruction.startsWith("DEFM")) {
                        type = Type.Data;
                    } else {
                        type = Type.Instruction;
                    }
                }
            }
        }
    }

    public Type getType() {
        return type;
    }

    public char getTypeChar() {
        return typeChar;
    }

    public int getAddress() {
        return address;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getDirective() {
        return directive;
    }

    public String getComment() {
        return comment;
    }

    public String getLabel() {
        return label;
    }
}
