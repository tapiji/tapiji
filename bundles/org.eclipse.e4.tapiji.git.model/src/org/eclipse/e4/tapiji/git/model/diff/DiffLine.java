package org.eclipse.e4.tapiji.git.model.diff;


public class DiffLine {

    private String lineNumberLeft;
    private String lineNumberRight;
    private String line;

    public DiffLine(String lineNumberLeft, String lineNumberRight, String line) {
        super();
        this.lineNumberLeft = lineNumberLeft;
        this.lineNumberRight = lineNumberRight;
        this.line = line;
    }

    public String getLineNumberLeft() {
        return lineNumberLeft;
    }

    public String getLineNumberRight() {
        return lineNumberRight;
    }

    public String getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "DiffLine [lineNumberLeft=" + lineNumberLeft + ", lineNumberRight=" + lineNumberRight + ", line=" + line + "]";
    }

}
