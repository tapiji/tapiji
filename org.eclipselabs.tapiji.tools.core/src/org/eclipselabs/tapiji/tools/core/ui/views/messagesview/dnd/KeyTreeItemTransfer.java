package org.eclipselabs.tapiji.tools.core.ui.views.messagesview.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;
import org.eclipselabs.tapiji.tools.core.Logger;
import org.eclipselabs.tapiji.translator.rbe.model.tree.IKeyTreeItem;

public class KeyTreeItemTransfer extends ByteArrayTransfer {

	private static final String KEY_TREE_ITEM = "keyTreeItem";

	private static final int TYPEID = registerType(KEY_TREE_ITEM);

	private static KeyTreeItemTransfer transfer = new KeyTreeItemTransfer();

	public static KeyTreeItemTransfer getInstance() {
		return transfer;
	}

	public void javaToNative(Object object, TransferData transferData) {
		if (!checkType(object)  || !isSupportedType(transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		IKeyTreeItem[] terms = (IKeyTreeItem[]) object;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oOut = new ObjectOutputStream(out);
			for (int i = 0, length = terms.length; i < length; i++) {
				oOut.writeObject(terms[i]);
			}
			byte[] buffer = out.toByteArray();
			oOut.close();
			
			super.javaToNative(buffer, transferData);
		} catch (IOException e) {
			Logger.logError(e);
		}
	}

	public Object nativeToJava(TransferData transferData) {
		if (isSupportedType(transferData)) {

			byte[] buffer;
			try {
				buffer = (byte[]) super.nativeToJava(transferData);
			} catch (Exception e) {
				Logger.logError(e);
				buffer = null;
			}
			if (buffer == null)
				return null;

			List<IKeyTreeItem> terms = new ArrayList<IKeyTreeItem>();
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				ObjectInputStream readIn = new ObjectInputStream(in);
				//while (readIn.available() > 0) {
				IKeyTreeItem newTerm = (IKeyTreeItem) readIn.readObject();
					terms.add(newTerm);
				//}
				readIn.close();
			} catch (Exception ex) {
				Logger.logError(ex);
				return null;
			}
			return terms.toArray(new IKeyTreeItem[terms.size()]);
		}

		return null;
	}

	protected String[] getTypeNames() {
		return new String[] { KEY_TREE_ITEM };
	}

	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	boolean checkType(Object object) {
		if (object == null || !(object instanceof IKeyTreeItem[])
				|| ((IKeyTreeItem[]) object).length == 0) {
			return false;
		}
		IKeyTreeItem[] myTypes = (IKeyTreeItem[]) object;
		for (int i = 0; i < myTypes.length; i++) {
			if (myTypes[i] == null) {
				return false;
			}
		}
		return true;
	}

	protected boolean validate(Object object) {
		return checkType(object);
	}
}