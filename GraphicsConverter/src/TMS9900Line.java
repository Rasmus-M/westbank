public class TMS9900Line {

    public enum Type {
        Empty,
        Label,
        Directive,
        Comment,
        ContinuationCommentData,
        ContinuationCommentInstruction,
        Data,
        Instruction
    }

    private Type type;
    private String label;
    private String directive;
    private String instruction;
    private int[] bytes;
    private String comment;

    public TMS9900Line(String line) {
        parseLine(line);
    }

    public TMS9900Line(int[] bytes, String comment) {
        this.type = Type.Data;
        this.instruction = "byte";
        this.bytes = bytes;
        this.comment = comment;
    }

    private void parseLine(String line) {
        if (line.isEmpty()) {
            type = Type.Empty;
        } else if (line.startsWith("*")) {
            type = Type.Comment;
            comment = line.substring(1).trim();
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
            line = line.trim();
            if (line.endsWith(":")) {
                type = Type.Label;
                label = line.substring(0, line.length() - 1);
            } else if (line.startsWith("byte")) {
                type = Type.Data;
                instruction = "byte";
                String[] byteStrings = line.substring(4).trim().split(",");
                bytes = new int[byteStrings.length];
                for (int i = 0; i < byteStrings.length; i++) {
                    bytes[i] = Util.parseInt(byteStrings[i]);
                }
            }
        }
    }

    public Type getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDirective() {
        return directive;
    }

    public void setDirective(String directive) {
        this.directive = directive;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int[] getBytes() {
        return bytes;
    }

    public void setBytes(int[] bytes) {
        this.bytes = bytes;
    }

    public String toString() {
        switch (type) {
            case Empty:
                return "";
            case Label:
                String labelWithColon = label + (label.equals("!") ? "" : ":");
                return labelWithColon + (comment != null ? Util.space(39 - labelWithColon.length()) + "; " + comment : "");
            case Directive:
                return Util.space(7) + directive;
            case Comment:
                return "* " + comment;
            case ContinuationCommentData:
                return Util.space(69) + "; " + comment;
            case ContinuationCommentInstruction:
                return Util.space(59) + "; " + comment;
            case Data:
                StringBuilder fullInstruction = new StringBuilder(instruction + " ");
                if (bytes != null) {
                    for (int i = 0; i < bytes.length; i++) {
                        fullInstruction.append(Util.tiHexString(bytes[i], false)).append(i < bytes.length - 1 ? "," : "");
                    }
                }
                String dataCommentIndent = Util.space(Math.max(69 - 7 - fullInstruction.length(), 1));
                return Util.space(7) + fullInstruction + (comment != null ? dataCommentIndent + "; " + comment : "");
            case Instruction:
                String commentIndent = Util.space(Math.max(39 - 7 - instruction.length(), 1));
                String comment = (this.comment != null ? "; " + this.comment : "");
                return Util.space(7) + instruction + commentIndent + "; " + comment;
        }
        return "";
    }
}
