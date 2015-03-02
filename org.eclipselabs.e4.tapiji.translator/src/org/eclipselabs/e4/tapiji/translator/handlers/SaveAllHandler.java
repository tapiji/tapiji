package org.eclipselabs.e4.tapiji.translator.handlers;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;


public class SaveAllHandler {

  private static final String TAG = SaveAllHandler.class.getSimpleName();

  @Execute
  public void execute() {
    System.out.println("Execute: " + TAG);
  }

  @CanExecute
  public boolean canExecute() {
    return false;
  }

}
