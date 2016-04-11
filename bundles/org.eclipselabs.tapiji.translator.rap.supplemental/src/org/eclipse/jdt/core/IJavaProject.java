package org.eclipse.jdt.core;

import org.eclipse.core.runtime.IPath;

/**
 * IJavaProject: added only needed parts, just to get the source code compiled in RAP.
 * 
 * @author Matthias Lettmayer
 *
 */
public interface IJavaProject {
	
	/**
	 * This is a helper method returning the resolved classpath for the project
	 * as a list of simple (non-variable, non-container) classpath entries.
	 * All classpath variable and classpath container entries in the project's
	 * raw classpath will be replaced by the simple classpath entries they
	 * resolve to.
	 * <p>
	 * The resulting resolved classpath is accurate for the given point in time.
	 * If the project's raw classpath is later modified, or if classpath
	 * variables are changed, the resolved classpath can become out of date.
	 * Because of this, hanging on resolved classpath is not recommended.
	 * </p>
	 * <p>
	 * Note that if the resolution creates duplicate entries 
	 * (i.e. {@link IClasspathEntry entries} which are {@link Object#equals(Object)}), 
	 * only the first one is added to the resolved classpath.
	 * </p>
	 *
	 * @param ignoreUnresolvedEntry indicates how to handle unresolvable
	 * variables and containers; <code>true</code> indicates that missing
	 * variables and unresolvable classpath containers should be silently
	 * ignored, and that the resulting list should consist only of the
	 * entries that could be successfully resolved; <code>false</code> indicates
	 * that a <code>JavaModelException</code> should be thrown for the first
	 * unresolved variable or container
	 * @return the resolved classpath for the project as a list of simple
	 * classpath entries, where all classpath variable and container entries
	 * have been resolved and substituted with their final target entries
	 * @exception JavaModelException in one of the corresponding situation:
	 * <ul>
	 *    <li>this element does not exist</li>
	 *    <li>an exception occurs while accessing its corresponding resource</li>
	 *    <li>a classpath variable or classpath container was not resolvable
	 *    and <code>ignoreUnresolvedEntry</code> is <code>false</code>.</li>
	 * </ul>
	 * @see IClasspathEntry
	 */
	IClasspathEntry[] getResolvedClasspath(boolean ignoreUnresolvedEntry);
	
	/**
	 * Returns the first existing package fragment on this project's classpath
	 * whose path matches the given (absolute) path, or <code>null</code> if none
	 * exist.
	 * The path can be:
	 * 	- internal to the workbench: "/Project/src"
	 *  - external to the workbench: "c:/jdk/classes.zip/java/lang"
	 * @param path the given absolute path
	 * @exception JavaModelException if this project does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 * @return the first existing package fragment on this project's classpath
	 * whose path matches the given (absolute) path, or <code>null</code> if none
	 * exist
	 */
	IPackageFragment findPackageFragment(IPath path); //throws JavaModelException;
	
	/**
	 * Returns the existing package fragment roots identified by the given entry.
	 * A classpath entry within the current project identifies a single root.
	 * <p>
	 * If the classpath entry denotes a variable, it will be resolved and return
	 * the roots of the target entry (empty if not resolvable).
	 * <p>
	 * If the classpath entry denotes a container, it will be resolved and return
	 * the roots corresponding to the set of container entries (empty if not resolvable).
	 * <p>
	 * The result does not include package fragment roots in other projects
	 * referenced on this project's classpath.
	 * 
	 * @param entry the given entry
	 * @return the existing package fragment roots identified by the given entry
	 * @see IClasspathContainer
	 * @since 2.1
	 */
	IPackageFragmentRoot[] findPackageFragmentRoots(IClasspathEntry entry);
	
	/**
	 * Returns an array of non-Java resources directly contained in this project.
	 * It does not transitively answer non-Java resources contained in folders;
	 * these would have to be explicitly iterated over.
	 * <p>
	 * Non-Java resources includes other files and folders located in the
	 * project not accounted for by any of it source or binary package fragment
	 * roots. If the project is a source folder itself, resources excluded from the
	 * corresponding source classpath entry by one or more exclusion patterns
	 * are considered non-Java resources and will appear in the result
	 * (possibly in a folder)
	 * </p>
	 *
	 * @return an array of non-Java resources (<code>IFile</code>s and/or
	 *              <code>IFolder</code>s) directly contained in this project
	 * @exception JavaModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	Object[] getNonJavaResources(); //throws JavaModelException;
}
