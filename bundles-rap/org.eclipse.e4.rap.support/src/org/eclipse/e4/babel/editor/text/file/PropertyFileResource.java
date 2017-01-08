package org.eclipse.e4.babel.editor.text.file;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;


public final class PropertyFileResource implements IPropertyResource {

    protected static final int DEFAULT_FILE_CAPACITY = 10 * 1024;
    protected static final String DEFAULT_ENCODING = "UTF-8";
    protected static final String TAG = IPropertyResource.class.getSimpleName();

    protected Document document;
    public File file;

    private PropertyFileResource(final File file) {
        super();
        this.file = file;
        
        //FileMonitor.getInstance().addFileChangeListener(
         //               this.fileChangeListener, file, 2000); // TODO make file scan
    }

    @Override
    public void saveDocument() {
        try {
            writeFile(document.get());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public IDocument getDocument() {
        if (null != document) {
            return document;

        }
        document = new Document();
        try {
            document.set(readFile());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return document;

    }


    private String readFile() throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    @Override
    public void writeFile(final String content) throws IOException {
        Files.write(file.toPath(), content.getBytes());
    }

    public void createFile(String path, String content) throws IOException {
        file = new File(path);
        file.createNewFile();
        writeFile(content);
    }


    @Override
    public String getEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getModificationTimeStamp(String format) {
        String date = "";
        if (format != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            if (null != document) {
                date = simpleDateFormat.format(new Date(document.getModificationStamp()));
            }
        }
        return date;
    }

    @Override
    public int getNumberOfLines() {
        return 0;
    }

    @Override
    public PropertyFileType getFileType() {
        return PropertyFileType.FILE;
    }
    
    public static IPropertyResource create(final File file, final String content) throws IOException {
        PropertyFileResource newFile = new PropertyFileResource(file);
        if (!file.exists()) {
            newFile.writeFile(content);
        }
        return newFile;
    }

    public static IPropertyResource create(final File file) throws IOException {
        return create(file, null);
    }

    @Override
    public String getFileName() {
        return file.getName();
    }

    @Override
    public String getFileExtension() {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    @Override
    public IFile getIFile() {
        String path = file.getAbsolutePath(); // P:\Workspace\AST\TEST\src\messages\Messages_de.properties
        int index = path.indexOf("src");
        String pathBeforeSrc = path.substring(0, index - 1);
        int lastIndexOf = pathBeforeSrc.lastIndexOf(File.separatorChar);
        String projectName = path.substring(lastIndexOf + 1, index - 1);
        String relativeFilePath = path.substring(index, path.length());

        return null;
       // return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).getFile(relativeFilePath);
    }

    @Override
    public String getName() {
        return file.getName();
    }
    @Override
    public void dispose() {
        if (document != null) {
            document.set(null);
        }
        file = null;
        document = null;
    }
}
