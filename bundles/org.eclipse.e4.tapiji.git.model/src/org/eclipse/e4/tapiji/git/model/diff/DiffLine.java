package org.eclipse.e4.tapiji.git.model.diff;


public class DiffLine {

    private final String numberLeft;
    private final String numberRight;
    private final String text;
    private DiffLineStatus status;

    public DiffLine(String lineNumberLeft, String lineNumberRight, String line) {
        super();
        this.numberLeft = lineNumberLeft;
        this.numberRight = lineNumberRight;
        this.text = line;
        this.status = DiffLineStatus.DEFAULT;
    }

    public String getNumberLeft() {
        return numberLeft;
    }

    public String getNumberRight() {
        return numberRight;
    }

    public String getText() {
        return text;
    }

    public DiffLineStatus getStatus() {
        return status;
    }

    public void setStatus(DiffLineStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DiffLine [numberLeft=" + numberLeft + ", numberRight=" + numberRight + ", text=" + text + ", status=" + status + "]";
    }

}
