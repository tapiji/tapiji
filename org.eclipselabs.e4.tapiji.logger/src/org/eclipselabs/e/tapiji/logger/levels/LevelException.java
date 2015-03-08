package org.eclipselabs.e.tapiji.logger.levels;


import java.util.logging.Level;


public final class LevelException extends Level {

  private static final long serialVersionUID = -7672586952237402659L;

  public LevelException() {
    super(LogLevels.EXCEPTION.getName(), LogLevels.EXCEPTION.getValue());
  }
}
