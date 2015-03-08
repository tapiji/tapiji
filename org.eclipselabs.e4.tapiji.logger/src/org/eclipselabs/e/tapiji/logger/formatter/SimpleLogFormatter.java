package org.eclipselabs.e.tapiji.logger.formatter;


import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.eclipselabs.e.tapiji.logger.levels.LogLevels;


public final class SimpleLogFormatter extends Formatter {

  @Override
  public String format(final LogRecord logRecord) {
    final StringBuffer sb = new StringBuffer(1000);
    String color = "";

    if (logRecord.getLevel().getName().equalsIgnoreCase(LogLevels.EXCEPTION.toString())
            || logRecord.getLevel().getName().equalsIgnoreCase(LogLevels.WHAT_A_TERRIBLE_FAILURE.toString())) {
      color = "#FF0000";
    } else if (logRecord.getLevel().getName().equalsIgnoreCase(LogLevels.DEBUG.toString())) {
      color = "#32CD32";
    } else if (logRecord.getLevel().getName().equalsIgnoreCase(Level.INFO.toString())) {
      color = "#FFA500";
    } else {
      color = "#000000";
    }

    sb.append("<font color=\"" + color + "\">");
    sb.append(logRecord.getLevel());
    sb.append(' ');
    sb.append(formatMessage(logRecord));
    sb.append("</font>");
    sb.append("<br/>");
    return sb.toString();
  }

  @Override
  public String getHead(final Handler h) {
    return "<HTML><HEAD></HEAD><BODY><H1>Logs from " + (new Date()) + "</H1><PRE>\n";
  }

  @Override
  public String getTail(final Handler h) {
    return "</PRE></BODY></HTML>\n";
  }
}
