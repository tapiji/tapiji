package at.ac.tuwien.inso.eclipse.rbe.model.tree;

import java.util.Collection;
import java.util.Set;

public interface IKeyTreeItem {

	String getId();

	Set<IKeyTreeItem> getChildren();

	String getName();

	Collection<? extends IKeyTreeItem> getNestedChildren();

	boolean applyFilter(String filter);

	Object getParent();

	IKeyTree getKeyTree();

}
