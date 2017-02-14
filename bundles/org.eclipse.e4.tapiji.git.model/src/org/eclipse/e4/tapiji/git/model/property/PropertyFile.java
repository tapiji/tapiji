package org.eclipse.e4.tapiji.git.model.property;


import java.io.File;


public class PropertyFile {

    private final String name;
    private final long modified;
    private final String path;

    private PropertyFile(String name, long modified, String path) {
        this.name = name;
        this.modified = modified;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public long modified() {
        return modified;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "PropertyFile [name=" + name + ", modified=" + modified + ", path=" + path + "]";
    }

    public static PropertyFile create(File file) {
        return new PropertyFile(file.getName(), file.lastModified(), file.getPath());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (modified ^ (modified >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PropertyFile other = (PropertyFile) obj;
        if (modified != other.modified) return false;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        if (path == null) {
            if (other.path != null) return false;
        } else if (!path.equals(other.path)) return false;
        return true;
    }

}
