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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((ref == null) ? 0 : ref.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Reference other = (Reference) obj;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        if (ref == null) {
            if (other.ref != null) return false;
        } else if (!ref.equals(other.ref)) return false;
        return true;
    }

}
