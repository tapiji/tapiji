package org.eclipselabs.tapiji.translator.rap.views.widgets.dnd;

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
import org.eclipselabs.tapiji.translator.rap.model.Term;


public class TermTransfer extends ByteArrayTransfer {

	private static final String TERM = "term";

	private static final int TYPEID = registerType(TERM);

	private static TermTransfer transfer = new TermTransfer();

	public static TermTransfer getInstance() {
		return transfer;
	}

	public void javaToNative(Object object, TransferData transferData) {
		if (!checkType(object)  || !isSupportedType(transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		Term[] terms = (Term[]) object;
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
			e.printStackTrace();
		}
	}

	public Object nativeToJava(TransferData transferData) {
		if (isSupportedType(transferData)) {

			byte[] buffer;
			try {
				buffer = (byte[]) super.nativeToJava(transferData);
			} catch (Exception e) {
				e.printStackTrace();
				buffer = null;
			}
			if (buffer == null)
				return null;

			List<Term> terms = new ArrayList<Term>();
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				ObjectInputStream readIn = new ObjectInputStream(in);
				//while (readIn.available() > 0) {
					Term newTerm = (Term) readIn.readObject();
					terms.add(newTerm);
				//}
				readIn.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
			return terms.toArray(new Term[terms.size()]);
		}

		return null;
	}

	protected String[] getTypeNames() {
		return new String[] { TERM };
	}

	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	boolean checkType(Object object) {
		if (object == null || !(object instanceof Term[])
				|| ((Term[]) object).length == 0) {
			return false;
		}
		Term[] myTypes = (Term[]) object;
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