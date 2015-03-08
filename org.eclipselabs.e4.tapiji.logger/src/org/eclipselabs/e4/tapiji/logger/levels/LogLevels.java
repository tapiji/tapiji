package org.eclipselabs.e4.tapiji.logger.levels;


import java.util.logging.Level;


public enum LogLevels {
  DEBUG(Level.WARNING.intValue() + 1),
  EXCEPTION(Level.WARNING.intValue() + 2),
  WHAT_A_TERRIBLE_FAILURE(Level.WARNING.intValue() + 3);

  private final int value;

  private LogLevels(final int value) {
    this.value = value;
  }

  public String getName() {
    return this.toString();
  }

  public int getValue() {
    return value;
  }
}
