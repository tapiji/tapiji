package org.eclipselabs.e4.tapiji.logger.levels;


import java.util.logging.Level;


public final class LevelDebug extends Level {

  private static final long serialVersionUID = -7954913641520000036L;

  public LevelDebug() {
    super(LogLevels.DEBUG.getName(), LogLevels.DEBUG.getValue());
  }
}
