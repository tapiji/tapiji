package org.eclipselabs.e4.tapiji.translator.handlers;


import org.eclipse.e4.core.di.annotations.Execute;


public class NewGlossaryHandler {

  private static final String TAG = NewGlossaryHandler.class.getSimpleName();

  @Execute
  public void execute() {
    System.out.println("Execute: " + TAG);
  }

}
