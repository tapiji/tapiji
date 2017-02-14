package org.eclipse.e4.tapiji.git.core.internal.file;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;
import org.eclipse.e4.tapiji.git.model.property.PropertyFile;


public class FileFinder extends SimpleFileVisitor<Path> {

    private PathMatcher matcher;
    private List<PropertyDirectory> directories;

    public FileFinder(String pattern) {
        super();
        this.directories = new ArrayList<>();
        this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        if (matcher.matches(path.getFileName())) {
            Optional<PropertyDirectory> found = directories.stream().filter(dir -> dir.getDirectory().equals(path.getParent())).findFirst();
            if (found.isPresent()) {
                found.get().addFile(PropertyFile.create(path.toFile()));
            } else {
                PropertyDirectory dir = PropertyDirectory.create(path.getParent());
                dir.addFile(PropertyFile.create(path.toFile()));
                directories.add(dir);
            }
        }
        return FileVisitResult.CONTINUE;
    }

    public List<PropertyDirectory> directories() {
        return directories;
    }
}
