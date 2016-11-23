package org.eclipselabs.e4.tapiji.logger.handler;


import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;

import org.eclipse.e4.tapiji.logger.Log;


public class SimpleConsoleHandler extends ConsoleHandler {

  @Override
  public void publish(final LogRecord record) {

    if (record.getLevel().getName().equalsIgnoreCase(Log.LEVEL_EXCEPTION.getName())) {
      System.err.println(record.getMessage());
    } else {
      System.out.println(record.getMessage());
    }
  };
}
