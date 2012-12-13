package org.eclipse.babel.editor.compat;

import org.eclipse.swt.SWT;

public class SwtRapCompatibilitySWT extends SWT {
	
	/**
	 * Style constant for right to left orientation (value is 1&lt;&lt;26).
	 * <p>
	 * When orientation is not explicitly specified, orientation is
	 * inherited.  This means that children will be assigned the
	 * orientation of their parent.  To override this behavior and
	 * force an orientation for a child, explicitly set the orientation
	 * of the child when that child is created.
	 * <br>Note that this is a <em>HINT</em>.
	 * </p>
	 * <p><b>Used By:</b><ul>
	 * <li><code>Control</code></li>
	 * <li><code>Menu</code></li>
	 * <li><code>GC</code></li> 
	 * </ul></p>
	 * 
	 * @since 2.1.2
	 */
	public static final int RIGHT_TO_LEFT = 1 << 26;
	//public static final int RIGHT_TO_LEFT = SWT.RIGHT;
}
