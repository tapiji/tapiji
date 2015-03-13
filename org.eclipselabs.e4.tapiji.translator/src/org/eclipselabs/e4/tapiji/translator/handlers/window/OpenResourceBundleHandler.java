package org.eclipselabs.e4.tapiji.translator.handlers.window;


import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.utils.FileUtils;


public class OpenResourceBundleHandler {

  private static final String TAG = OpenResourceBundleHandler.class.getSimpleName();

  @Execute
  public void execute(final IWorkbench workbench, @Named(IServiceConstants.ACTIVE_SHELL) final Shell shell) {
    final String[] fileNames = FileUtils.queryFileName(shell, "Open Resource-Bundle", SWT.OPEN,
            FileUtils.PROPERTY_FILE_ENDINGS);
    if (fileNames != null) {
      final String fileName = fileNames[0];
      if (!FileUtils.isResourceBundle(fileName)) {
        MessageDialog.openError(shell, String.format("Cannot open Resource-Bundle %s", fileName),
                "The choosen file does not represent a Resource-Bundle!");
        return;
      }
    }


    /*
     * IWorkbenchPage page = window.getActivePage(); try { page.openEditor( new FileEditorInput(FileUtils.getResourceBundleRef(fileName, FileUtils.EXTERNAL_RB_PROJECT_NAME)), RESOURCE_BUNDLE_EDITOR); } catch (CoreException e) { e.printStackTrace(); }
     */


  }
}
