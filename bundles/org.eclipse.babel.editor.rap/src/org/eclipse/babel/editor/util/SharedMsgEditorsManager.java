package org.eclipse.babel.editor.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.babel.editor.IMessagesEditor;

public class SharedMsgEditorsManager {
	public final static SharedMsgEditorsManager INSTANCE = new SharedMsgEditorsManager();
	/** A map with all opened editors and their RB ID as key; A RB can be opened by multiple Editors (therefore the List) */
	private static Map<Long, List<IMessagesEditor>> openedMsgEditorsMap = new HashMap<Long, List<IMessagesEditor>>();
	
	public List<IMessagesEditor> getSharedMessagesEditors(long rbID) {
		return openedMsgEditorsMap.get(rbID);
	}
	
	public void addMessagesEditor(long rbID, IMessagesEditor msgEditor) {
		List<IMessagesEditor> sharedEditors = openedMsgEditorsMap.get(rbID);
		if (sharedEditors == null) {
			// lazy init
			sharedEditors = new ArrayList<IMessagesEditor>();
			openedMsgEditorsMap.put(rbID, sharedEditors);
		}
		sharedEditors.add(msgEditor);
	}
	
	public void removeMessagesEditor(long rbID, IMessagesEditor msgEditor) {
		List<IMessagesEditor> sharedEditors = openedMsgEditorsMap.get(rbID);
		if (sharedEditors != null)
			sharedEditors.remove(msgEditor);
	}
}
