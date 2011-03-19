package org.eclipselabs.tapiji.tools.core.extensions;

import java.io.Serializable;

import org.eclipse.core.resources.IFile;

/**
 * Describes a text fragment within a source resource.
 * 
 * @author Martin Reiterer
 */
public interface ILocation {

    /**
    * Returns the source resource's physical location.
    * 
    * @return The file within the text fragment is located
    */
    public IFile getFile();

    /**
    * Returns the position of the text fragments starting character.
    * 
    * @return The position of the first character
    */
    public int getStartPos();

    /**
    * Returns the position of the text fragments last character.
    * 
    * @return The position of the last character
    */
    public int getEndPos();

    /**
    * Returns the text fragment.
    * 
    * @return The text fragment
    */
    public String getLiteral();

    /**
    * Returns additional metadata. The type and content of this property is not
    * specified and can be used to marshal additional data for the computation
    * of resolution proposals.
    * 
    * @return The metadata associated with the text fragment
    */
    public Serializable getData();
}
