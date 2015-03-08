package org.eclipselabs.e4.tapiji.translator.handlers;


import java.io.File;
import java.text.MessageFormat;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.translator.core.GlossaryManager;
import org.eclipselabs.e4.tapiji.translator.utils.FileUtils;


public class NewGlossaryHandler {

  private static final String TAG = NewGlossaryHandler.class.getSimpleName();

  // TODO MOVE TO FILEUTIL
  private static final String[] XML_FILE_ENDING = new String[] {"*.xml"};

  @Execute
  public void execute(@Named(IServiceConstants.ACTIVE_SHELL) final Shell shell) {
    System.out.println("Execute: " + TAG);

    final String[] fileNames = FileUtils.queryFileName(shell, "New Glossary", SWT.SAVE, XML_FILE_ENDING);

    if (fileNames == null || fileNames[0] == null) {
      return;
    }

    String fileName = fileNames[0];
    if (!fileName.endsWith(".xml")) {
      if (fileName.endsWith(".")) {
        fileName += "xml";
      } else {
        fileName += ".xml";
      }
    }

    if (new File(fileName).exists()) {
      final String recallPattern = "The file \"{0}\" already exists. Do you want to replace this file with an empty translation glossary?";
      if (!MessageDialog.openQuestion(shell, "File already exists!",
              new MessageFormat(recallPattern).format(new String[] {fileName}))) {
        return;
      }
    }

    if (fileName != null) {
      // IWorkbenchPage page = window.getActivePage();
      GlossaryManager.newGlossary(new File(fileName));
    }
  }
}
