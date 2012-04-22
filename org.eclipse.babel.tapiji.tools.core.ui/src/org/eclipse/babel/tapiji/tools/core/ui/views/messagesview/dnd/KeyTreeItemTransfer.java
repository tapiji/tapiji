/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.views.messagesview.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IKeyTreeNode;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

public class KeyTreeItemTransfer extends ByteArrayTransfer {

	private static final String KEY_TREE_ITEM = "keyTreeItem";

	private static final int TYPEID = registerType(KEY_TREE_ITEM);

	private static KeyTreeItemTransfer transfer = new KeyTreeItemTransfer();

	public static KeyTreeItemTransfer getInstance() {
		return transfer;
	}

	public void javaToNative(Object object, TransferData transferData) {
		if (!checkType(object) || !isSupportedType(transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		IKeyTreeNode[] terms = (IKeyTreeNode[]) object;
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

			List<IKeyTreeNode> terms = new ArrayList<IKeyTreeNode>();
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				ObjectInputStream readIn = new ObjectInputStream(in);
				// while (readIn.available() > 0) {
				IKeyTreeNode newTerm = (IKeyTreeNode) readIn.readObject();
				terms.add(newTerm);
				// }
				readIn.close();
			} catch (Exception ex) {
				Logger.logError(ex);
				return null;
			}
			return terms.toArray(new IKeyTreeNode[terms.size()]);
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
		if (object == null || !(object instanceof IKeyTreeNode[])
		        || ((IKeyTreeNode[]) object).length == 0) {
			return false;
		}
		IKeyTreeNode[] myTypes = (IKeyTreeNode[]) object;
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
