/*******************************************************************************
 * Copyright (c) 2007 Pascal Essiembre, Alexej Strelzow, Matthias Lettmayer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pascal Essiembre - initial API and implementation
 *    Alexej Strelzow - TapJI integration, bug fixes & enhancements
 *                    - issue 35, 36, 48, 73
 *    Matthias Lettmayer - extracted messages editor into own class for SWT specific implementation
 ******************************************************************************/

package org.eclipse.babel.editor.internal;

import org.eclipse.babel.core.message.internal.IMessagesBundleGroupListener;
import org.eclipse.babel.core.message.internal.MessagesBundle;
import org.eclipse.babel.core.message.internal.MessagesBundleGroupAdapter;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

public class MessagesEditor extends AbstractMessagesEditor {

    @Override
    protected IMessagesBundleGroupListener getMsgBundleGroupListner() {
        return new MessagesBundleGroupAdapter() {
            @Override
            public void messagesBundleAdded(MessagesBundle messagesBundle) {
                addMessagesBundle(messagesBundle);
            }
        };
    }

    @Override
    protected void initRAP() {
        // nothing to do
    }

    @Override
    protected void disposeRAP() {
        // nothing to do
    }

    @Override
    public void setEnabled(boolean enabled) {
        i18nPage.setEnabled(enabled);
        for (ITextEditor textEditor : textEditorsIndex) {
            // TODO disable editors
        }
    }

}
