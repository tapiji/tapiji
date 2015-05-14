/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Martin Reiterer - initial API and implementation
 * Christian Behon - Refactor
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.ui.widget.dnd;


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
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.model.Term;


public final class TermTransfer extends ByteArrayTransfer {

    private static final String TERM = "term";
    private static final int TYPEID = registerType(TERM);
    private static final String TAG = TermTransfer.class.getSimpleName();

    private TermTransfer() {
        super();
    }

    private static class TermTransferHolder {

        private static final TermTransfer INSTANCE = new TermTransfer();
    }

    public static TermTransfer getInstance() {
        return TermTransferHolder.INSTANCE;
    }

    @Override
    public void javaToNative(final Object object, final TransferData transferData) {
        if (!checkType(object) || !isSupportedType(transferData)) {
            DND.error(DND.ERROR_INVALID_DATA);
        }
        final Term[] terms = (Term[]) object;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                        ObjectOutputStream oOut = new ObjectOutputStream(out);) {
            for (final Term term2 : terms) {
                oOut.writeObject(term2);
            }
            final byte[] buffer = out.toByteArray();
            super.javaToNative(buffer, transferData);
        } catch (final IOException exception) {
            Log.e(TAG, exception);
        }
    }

    @Override
    public Object nativeToJava(final TransferData transferData) {
        if (isSupportedType(transferData)) {
            final byte[] buffer = getBytesFrom(transferData);
            if (null != buffer) {
                final List<Term> terms = new ArrayList<Term>();
                try (ByteArrayInputStream in = new ByteArrayInputStream(buffer);
                                ObjectInputStream readIn = new ObjectInputStream(in);) {
                    // while (readIn.available() > 0) {
                    final Term newTerm = (Term) readIn.readObject();
                    terms.add(newTerm);
                    // }
                } catch (final Exception exception) {
                    Log.e(TAG, exception);
                    return null;
                }
                return terms.toArray(new Term[terms.size()]);
            }
        }
        return null;
    }

    private byte[] getBytesFrom(final TransferData transferData) {
        byte[] buffer;
        try {
            buffer = (byte[]) super.nativeToJava(transferData);
        } catch (final Exception exception) {
            Log.e(TAG, exception);
            buffer = null;
        }
        return buffer;
    }

    @Override
    protected String[] getTypeNames() {
        return new String[] {TERM};
    }

    @Override
    protected int[] getTypeIds() {
        return new int[] {TYPEID};
    }

    boolean checkType(final Object object) {
        if ((object == null) || !(object instanceof Term[]) || (((Term[]) object).length == 0)) {
            return false;
        }
        final Term[] myTypes = (Term[]) object;
        for (final Term myType : myTypes) {
            if (myType == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean validate(final Object object) {
        return checkType(object);
    }
}
