package org.eclipselabs.e4.tapiji.translator.handlers;


import java.io.File;
import java.text.MessageFormat;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.core.GlossaryManager;
import org.eclipselabs.e4.tapiji.utils.FileUtils;


public class NewGlossaryHandler {

  private static final String TAG = NewGlossaryHandler.class.getSimpleName();

  @Execute
  public void execute(@Named(IServiceConstants.ACTIVE_SHELL) final Shell shell) {
    final String[] fileNames = FileUtils.queryFileName(shell, "New Glossary", SWT.SAVE, FileUtils.XML_FILE_ENDINGS);
    if (fileNames != null) {
      String fileName = fileNames[0];
      fileName = FileUtils.checkXmlFileEnding(fileName);

      if (new File(fileName).exists()) {
        final boolean result = showQuestionDialog(shell, fileName);
        if (!result) {
          Log.i(TAG, String.format("Filename %s already exists", fileName));
          return;
        }
      }
      GlossaryManager.newGlossary(new File(fileName));
    }
  }

  public boolean showQuestionDialog(final Shell shell, String fileName) {
    final String recallPattern = "The file \"{0}\" already exists. Do you want to replace this file with an empty translation glossary?";
    final boolean result = MessageDialog.openQuestion(shell, "File already exists!",
            new MessageFormat(recallPattern).format(new String[] {fileName}));
    return result;
  }
}
