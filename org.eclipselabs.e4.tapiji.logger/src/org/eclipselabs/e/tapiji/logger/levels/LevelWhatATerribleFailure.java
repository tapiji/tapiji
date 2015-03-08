package org.eclipselabs.e.tapiji.logger.levels;


import java.util.logging.Level;


public final class LevelWhatATerribleFailure extends Level {

  private static final long serialVersionUID = 4120923185473198740L;

  public LevelWhatATerribleFailure() {
    super(LogLevels.WHAT_A_TERRIBLE_FAILURE.getName(), LogLevels.WHAT_A_TERRIBLE_FAILURE.getValue());
  }
}
