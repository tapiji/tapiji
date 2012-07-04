package org.eclipse.jdt.core;

import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;

/**
 * IStorage: added only needed parts, just to get the source code compiled in RAP.
 * 
 * @author Matthias Lettmayer
 *
 */
public interface IStorage {
	/**
	 * Returns an open input stream on the contents of this storage.
	 * The caller is responsible for closing the stream when finished.
	 *
	 * @return an input stream containing the contents of this storage
	 * @exception CoreException if the contents of this storage could 
	 *		not be accessed.   See any refinements for more information.
	 */
	public InputStream getContents() throws CoreException;
	
	/**
	 * Returns the name of this storage. 
	 * The name of a storage is synonymous with the last segment
	 * of its full path though if the storage does not have a path,
	 * it may still have a name.
	 *
	 * @return the name of the data represented by this storage,
	 *		or <code>null</code> if this storage has no name
	 * @see #getFullPath()
	 */
	public String getName();
}
