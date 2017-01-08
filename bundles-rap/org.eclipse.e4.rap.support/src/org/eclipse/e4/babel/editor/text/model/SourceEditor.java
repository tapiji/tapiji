package org.eclipse.e4.babel.editor.text.model;


import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.e4.babel.editor.text.file.IPropertyResource;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.SourceViewer;


/**
 * Wrapper around a properties file text editor
 * providing extra functionality.
 * 
 * @author Pascal Essiembre
 * @author Tobias Langner
 * @author Christian Behon
 */
public class SourceEditor {

    private final Locale locale;
    private final IPropertyResource document;
    private String contentCache;

    /**
     * Constructor
     * 
     * @param editor text editor
     * @param locale a locale
     * @param file properties file
     */
    private SourceEditor(final IPropertyResource document, final Locale locale) {
        super();
        this.document = document;
        this.locale = locale;
        this.contentCache = getContent();
    }

    /**
     * Create new instance of {@link SourceEditor}
     * 
     * @param document Document to get and save content
     * @param locale Locale
     * @param file File
     * @return SourceEditor
     */
    public static SourceEditor create(final IPropertyResource document, final Locale locale) {
        return new SourceEditor(document, locale);
    }

    /**
     * Gets the locale associated with this source editor.
     * 
     * @return locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Gets the file associated with this source editor.
     * 
     * @return properties file
     */
    public IFile getFile() {
        return document.getIFile();
    }

    /**
     * Gets the text editor associated with this source editor.
     * 
     * @return text editor
     */
    public IPropertyResource getDocument() {
        return document;
    }

    /**
     * Checks whether the underlying file content differs from the cached source
     * editor content.
     * 
     * @return <code>true</code> if dirty
     */
    public boolean isCacheDirty() {
         boolean same = !getContent().equals(contentCache);
         return same;
    }

    /**
     * Resets the source editor cache.
     */
    public void resetCache() {
        contentCache = getContent();
    }

    /**
     * Gets the content of this source editor.
     * 
     * @return content
     */
    public String getContent() {
        return document.getDocument().get();
    }

    /**
     * Sets the content of this source editor (replacing existing content).
     * 
     * @param content new content
     */
    public void setContent(String content) {
        document.getDocument().set(content);
        contentCache = content;
    }

    /**
     * Save content to file
     */
    public void saveDocument() {
        document.saveDocument();
    }

    /**
     * Checks whether this source editor is read-only.
     * 
     * @return <code>true</code> if read-only.
     */
    public boolean isReadOnly() {
        return false;// ((ISourceViewer) editor).isEditorInputReadOnly();
    }

    public void selectKey(String key, SourceViewer sourceViewer) {
        if (key != null) {
            String editorContent = getContent();
            Pattern pattern = Pattern.compile("^" + Pattern.quote(key)+ ".*$", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(editorContent);
            if (matcher.find()) {
                int start = matcher.start();
                sourceViewer.revealRange(start, 0);
                sourceViewer.setSelectedRange(start, 0);
            }
        }
    }

    public String getCurrentKey(SourceViewer sourceViewer) {
        /*if (sourceViewer.getSelectionProvider().getSelection() instanceof TextSelection) {
            TextSelection selection = (TextSelection) sourceViewer.getSelectionProvider().getSelection();
            int selectionStart = selection.getOffset();
            String content = getContent();
            int start = 0, end = 0;
            
            // Extract the bounds of the line containing the selection
            for (start = selectionStart; start > 0 && content.charAt(start-1) != '\n'; start--);
            for (end = start; end < content.length()-1 && content.charAt(end+1) != '=' && content.charAt(end+1) != '\n'; end++);
            String line = content.substring(start, end).trim();
            return line;
        }*/
        return null;
    }

    // TODO add save and revertToSave here (spawning a thread)
}
