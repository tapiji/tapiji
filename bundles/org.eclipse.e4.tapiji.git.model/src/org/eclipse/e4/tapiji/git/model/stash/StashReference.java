package org.eclipse.e4.tapiji.git.model.stash;


public class StashReference {

    private int reference;

    public StashReference(int reference) {
        super();
        this.reference = reference;
    }

    public int getReference() {
        return reference;
    }

    @Override
    public String toString() {
        return "StashReference [reference=" + reference + "]";
    }

}
