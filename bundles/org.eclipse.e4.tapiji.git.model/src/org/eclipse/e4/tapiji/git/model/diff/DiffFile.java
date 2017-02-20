package org.eclipse.e4.tapiji.git.model.diff;


import java.util.ArrayList;
import java.util.List;


public class DiffFile {

    private int added;
    private int deleted;
    private List<DiffSection> sections = new ArrayList<>();

    public DiffFile() {
        super();
    }

    public void setAdded(int added) {
        this.added = added;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public void setSections(List<DiffSection> sections) {
        this.sections = sections;
    }

    public void addSection(DiffSection section) {
        sections.add(section);
    }

    public int getAdded() {
        return added;
    }

    public int getDeleted() {
        return deleted;
    }

    public List<DiffSection> getSections() {
        return sections;
    }

    @Override
    public String toString() {
        return "DiffFile [added=" + added + ", deleted=" + deleted + ", sections=" + sections + "]";
    }

}
