package org.eclipselabs.e4.tapiji.translator.handlers;


import java.io.File;
import javax.inject.Named;
import org.eclipse.babel.editor.widgets.suggestion.exception.InvalidConfigurationSetting;
import org.eclipse.babel.editor.widgets.suggestion.provider.StringConfigurationSetting;
import org.eclipse.babel.editor.widgets.suggestion.provider.SuggestionProviderUtils;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.translator.core.GlossaryManager;
import org.eclipselabs.e4.tapiji.translator.utils.FileUtils;


public class OpenGlossaryHandler {

  private static final String TAG = OpenGlossaryHandler.class.getSimpleName();

  // TODO MOVE TO FILEUTIL
  private static final String[] XML_FILE_ENDING = new String[] {"*.xml"};

  @Execute
  public void execute(@Named(IServiceConstants.ACTIVE_SHELL) final Shell shell) {
    System.out.println("Execute: " + TAG);
    final String[] fileNames = FileUtils.queryFileName(shell, "Open Glossary", SWT.OPEN, XML_FILE_ENDING);
    final String fileName = fileNames[0];

    if (!FileUtils.isGlossary(fileName)) {
      MessageDialog.openError(shell, "Cannot open Glossary", "The choosen file does not represent a Glossary!");
      return;
    }

    if (fileName != null) {
      if (fileName != null) {
        GlossaryManager.loadGlossary(new File(fileName));
      }
    }

    try {
      SuggestionProviderUtils.updateConfigurationSetting("glossaryFile", new StringConfigurationSetting(fileName));
    } catch (final InvalidConfigurationSetting e) {
      System.out.println("WAA");
    }
  }
}
