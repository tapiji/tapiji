package org.eclipselabs.e.tapiji.logger;


import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipselabs.e.tapiji.logger.formatter.SimpleLogFormatter;
import org.eclipselabs.e.tapiji.logger.handler.SimpleConsoleHandler;
import org.eclipselabs.e.tapiji.logger.levels.LevelDebug;
import org.eclipselabs.e.tapiji.logger.levels.LevelException;
import org.eclipselabs.e.tapiji.logger.levels.LevelWhatATerribleFailure;


public final class Log {

  private static final boolean DEBUG = true;

  private static Logger LOGGER;

  public final static LevelDebug LEVEL_DEBUG = new LevelDebug();

  public final static LevelException LEVEL_EXCEPTION = new LevelException();

  public final static LevelWhatATerribleFailure LEVEL_WTF = new LevelWhatATerribleFailure();

  static {
    LOGGER = Logger.getLogger(Log.class.getName());
    try {
      LOGGER.setLevel(Level.ALL);
      LOGGER.setUseParentHandlers(false);
      final Handler[] handlers = LOGGER.getHandlers();
      for (final Handler handler : handlers) {
        if (handler.getClass() == ConsoleHandler.class) {
          LOGGER.removeHandler(handler);
        }
      }
      final FileHandler fileHandler = new FileHandler("log_output.html", true);
      fileHandler.setFormatter(new SimpleLogFormatter());
      LOGGER.addHandler(fileHandler);
      LOGGER.addHandler(new SimpleConsoleHandler());
    } catch (final IOException e) {
      LOGGER.warning(stackTraceToString(e));
    }
  }

  private Log() {

  }

  public static void setLogLevel(final Level logLevel) {
    LOGGER.setLevel(logLevel);
  }

  public static Level getLogLevel() {
    return LOGGER.getLevel();
  }

  /**
   * Warning
   * 
   * @param tag Identifier of the source e.g. class or name
   * @param message Message to log
   */
  public static void w(final String tag, final String message) {
    if (DEBUG) {
      LOGGER.warning(String.format("%1s: %2s", tag, message));
    }
  }

  /**
   * Info
   * 
   * @param tag Identifier of the source e.g. class or name
   * @param message Message to log
   */
  public static void i(final String tag, final String message) {
    if (DEBUG) {
      LOGGER.info(String.format("%1s: %2s", tag, message));
    }
  }

  /**
   * Debug
   * 
   * @param tag Identifier of the source e.g. class or name
   * @param message Message to log
   */
  public static void d(final String tag, final String message) {
    if (DEBUG) {
      LOGGER.log(LEVEL_DEBUG, String.format("%1s: %2s", tag, message));
    }
  }

  /**
   * Error
   * 
   * @param tag Identifier of the source e.g. class or name
   * @param exception Exception to log
   */
  public static void e(final String tag, final Exception e) {
    if (DEBUG) {
      LOGGER.log(LEVEL_EXCEPTION, String.format("%1s:\n %2s", tag, stackTraceToString(e)));
    }
  }

  /**
   * What a terrible failure. <br/>
   * Condition that should never happen
   * 
   * @param tag Identifier of the source e.g. class or name
   * @param message Message to log
   * @param exception Exception to log
   */
  public static void wtf(final String tag, final String message, final Exception exception) {
    if (DEBUG) {
      LOGGER.log(LEVEL_WTF, String.format("%1s:\n %2s", tag, stackTraceToString(exception)));
    }
  }

  public static String stackTraceToString(final Throwable e) {
    final StringBuilder sb = new StringBuilder();
    for (final StackTraceElement element : e.getStackTrace()) {
      sb.append(element.toString());
      sb.append("\n");
    }
    return sb.toString();
  }
}
