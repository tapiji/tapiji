package org.eclipse.jdt.core;

/**
 * IPackageFragment: added only needed parts, just to get the source code compiled in RAP.
 * 
 * @author Matthias Lettmayer
 *
 */
public interface IPackageFragment {
	
	/**
	 * Returns the dot-separated package name of this fragment, for example
	 * <code>"java.lang"</code>, or <code>""</code> (the empty string),
	 * for the default package.
	 *
	 * @return the dot-separated package name of this fragment
	 */
	String getElementName();
	
	/**
	 * Returns an array of non-Java resources contained in this package fragment.
	 * <p>
	 * Non-Java resources includes other files and folders located in the same
	 * directory as the compilation units or class files for this package
	 * fragment. Source files excluded from this package by virtue of
	 * inclusion/exclusion patterns on the corresponding source classpath entry
	 * are considered non-Java resources and will appear in the result
	 * (possibly in a folder).
	 * </p><p>
	 * Since 3.3, if this package fragment is inside an archive, the non-Java resources
	 * are a tree of {@link IJarEntryResource}s. One can navigate this tree using
	 * the {@link IJarEntryResource#getChildren()} and
	 * {@link IJarEntryResource#getParent()} methods.
	 * </p>
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return an array of non-Java resources (<code>IFile</code>s,
	 *              <code>IFolder</code>s, or <code>IStorage</code>s if the
	 *              package fragment is in an archive) contained in this package
	 *              fragment
	 * @see IClasspathEntry#getInclusionPatterns()
	 * @see IClasspathEntry#getExclusionPatterns()
	 */
	Object[] getNonJavaResources(); //throws JavaModelException;
}
