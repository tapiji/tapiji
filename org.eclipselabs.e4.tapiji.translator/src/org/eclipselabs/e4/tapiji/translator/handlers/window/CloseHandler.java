package org.eclipselabs.e4.tapiji.translator.handlers.window;


import org.eclipse.e4.core.di.annotations.Execute;


public class CloseHandler {

  private static final String TAG = CloseHandler.class.getSimpleName();

  @Execute
  public void execute() {
    System.out.println("Execute: " + TAG);
  }

}
