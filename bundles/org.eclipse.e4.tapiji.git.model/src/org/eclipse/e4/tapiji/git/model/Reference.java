package org.eclipse.e4.tapiji.git.model;


public class Reference {

    private String name;
    private String ref;

    public Reference(String name, String ref) {
        super();
        this.name = name;
        this.ref = ref;
    }

    public String getName() {
        return name;
    }

    public String getRef() {
        return ref;
    }

    @Override
    public String toString() {
        return "Reference [name=" + name + ", ref=" + ref + "]";
    }
}
