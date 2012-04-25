package org.eclipse.babel.core.message.manager;

import org.eclipse.babel.core.message.IMessagesBundle;

/**
 * Used to sync TapiJI with Babel and vice versa.
 * <br><br>
 * 
 * @author Alexej Strelzow
 */
public interface IMessagesEditorListener {

	/**
	 * Should only be called when the editor performs save!
	 */
	void onSave();
	
	/**
	 * Can be called when the Editor changes.
	 */
	void onModify();
	
	/**
	 * Called when a {@link IMessagesBundle} changed.
	 * @param bundle
	 */
	void onResourceChanged(IMessagesBundle bundle);
	// TODO: Get rid of this method, maybe merge with onModify()
	
}
