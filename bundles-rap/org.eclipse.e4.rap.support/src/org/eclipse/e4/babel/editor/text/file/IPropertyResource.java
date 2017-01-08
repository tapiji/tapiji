package org.eclipse.e4.babel.editor.text.file;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;

public interface IPropertyResource {
    public void saveDocument();
    
    public IDocument getDocument();

    public String getEncoding();

    public String getModificationTimeStamp(final String format);

    public int getNumberOfLines();

    public PropertyFileType getFileType();
    
    public String getFileName();
    
    public String getFileExtension();

    public void dispose();

    public IFile getIFile();

    public String getName();

    void writeFile(String content) throws IOException;
}
