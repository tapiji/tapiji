package org.eclipse.e4.tapiji.git.model.diff;


public class DiffLine {

    private final String lineNumberLeft;
    private final String lineNumberRight;
    private final String line;
    private boolean accepted;

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

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public String toString() {
        return "DiffLine [lineNumberLeft=" + lineNumberLeft + ", lineNumberRight=" + lineNumberRight + ", line=" + line + ", accepted=" + accepted + "]";
    }

}
