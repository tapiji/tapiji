package org.eclipselabs.tapiji.translator.rbe.model.tree;

import java.util.Collection;
import java.util.Set;

public interface IKeyTreeItem {

    /**
     * Returns the key of the corresponding Resource-Bundle entry.
     * @return The key of the Resource-Bundle entry
     */
    String getId();

    /**
     * Returns the set of Resource-Bundle entries of the next deeper 
     * hierarchy level that share the represented entry as their common
     * parent.
     * @return The direct child Resource-Bundle entries
     */
    Set<IKeyTreeItem> getChildren();

    /**
     * The represented Resource-Bundle entry's id without the prefix defined
     * by the entry's parent. 
     * @return The Resource-Bundle entry's display name.
     */
    String getName();

    /** 
     * Returns the set of Resource-Bundle entries from all deeper hierarchy 
     * levels that share the represented entry as their common parent.
     * @return All child Resource-Bundle entries
     */
    Collection<? extends IKeyTreeItem> getNestedChildren();

    /**
     * Returns whether this Resource-Bundle entry is visible under the 
     * given filter expression.
     * @param filter The filter expression
     * @return True if the filter expression matches the represented Resource-Bundle entry
     */
    boolean applyFilter(String filter);

    /**
     * The Resource-Bundle entries parent.
     * @return The parent Resource-Bundle entry
     */
    Object getParent();

    /**
     * The Resource-Bundles key representation.
     * @return The Resource-Bundle reference, if known
     */
    IKeyTree getKeyTree();

}
