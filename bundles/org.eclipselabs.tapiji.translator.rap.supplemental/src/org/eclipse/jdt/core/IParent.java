package org.eclipse.jdt.core;

/**
 * IParent: added only needed parts, just to get the source code compiled in RAP.
 * 
 * @author Matthias Lettmayer
 *
 */
public interface IParent {
	/**
	 * Returns the immediate children of this element.
	 * Unless otherwise specified by the implementing element,
	 * the children are in no particular order.
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *      exception occurs while accessing its corresponding resource
	 * @return the immediate children of this element
	 */
	IJavaElement[] getChildren(); //throws JavaModelException;
}
