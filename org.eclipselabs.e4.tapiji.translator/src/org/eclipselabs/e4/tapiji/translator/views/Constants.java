package org.eclipselabs.e4.tapiji.translator.views;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipselabs.e4.tapiji.utils.FontUtils;


public final class Constants {

    private Constants() {
        super();
    }

    public static final Color COLOR_GRAY = FontUtils.getSystemColor(SWT.COLOR_GRAY);
    public static final Color COLOR_BLACK = FontUtils.getSystemColor(SWT.COLOR_BLACK);
    public static final Color COLOR_INFO = FontUtils.getSystemColor(SWT.COLOR_YELLOW);
    public static final Color COLOR_CROSSREFERENCE_BACKGROUND = FontUtils.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
    public static final Color COLOR_CROSSREFERENCE_FOREGROUND = FontUtils.getSystemColor(SWT.COLOR_INFO_FOREGROUND);
    public static final Color COLOR_WHITE = FontUtils.getSystemColor(SWT.COLOR_WHITE);

    public static final Font FONT_BOLD = FontUtils.createFont(SWT.BOLD);
    public static final Font FONT_ITALIC = FontUtils.createFont(SWT.ITALIC);
}
