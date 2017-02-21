package org.eclipse.e4.tapiji.git.model.diff;


import java.util.ArrayList;
import java.util.List;


public class DiffHunk {

    private String header;
    List<DiffLine> lines = new ArrayList<>();

    public DiffHunk() {
        super();
    }

    public String getHeader() {
        return header;
    }

    public List<DiffLine> getLines() {
        return lines;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setLines(List<DiffLine> lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        return "DiffHunk [header=" + header + ", lines=" + lines + "]";
    }

    public void addLineDiff(DiffLine lineDiff) {
        lines.add(lineDiff);
    }

}
