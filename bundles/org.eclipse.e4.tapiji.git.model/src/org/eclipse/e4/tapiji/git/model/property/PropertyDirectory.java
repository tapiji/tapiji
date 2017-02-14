package org.eclipse.e4.tapiji.git.model.property;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class PropertyDirectory {

    private Path directory;
    private List<PropertyFile> files = new ArrayList<PropertyFile>();

    public PropertyDirectory(Path directory) {
        this.directory = directory;
    }

    public Path getDirectory() {
        return directory;
    }

    public List<PropertyFile> getFiles() {
        return files;
    }

    public void addFile(PropertyFile file) {
        files.add(file);
    }

    public String getName() {
        return directory.getFileName().toString();
    }

    @Override
    public String toString() {
        return "PropertyDirectory [directory=" + directory + ", files=" + files + "]";
    }

    public static PropertyDirectory create(Path path) {
        return new PropertyDirectory(path);
    }

    public boolean hasFiles() {
        return !files.isEmpty();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((directory == null) ? 0 : directory.hashCode());
        result = prime * result + ((files == null) ? 0 : files.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PropertyDirectory other = (PropertyDirectory) obj;
        if (directory == null) {
            if (other.directory != null) return false;
        } else if (!directory.equals(other.directory)) return false;
        if (files == null) {
            if (other.files != null) return false;
        } else if (!files.equals(other.files)) return false;
        return true;
    }

}
