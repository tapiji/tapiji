package org.eclipselabs.e4.tapiji.translator.constant;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipselabs.e4.tapiji.utils.ColorUtils;
import org.eclipselabs.e4.tapiji.utils.FontUtils;


public final class TranslatorConstant {

    private TranslatorConstant() {
        super();
        // Hide constructor. Only static access allowed
    }

    public static final Color COLOR_GRAY = ColorUtils.getSystemColor(SWT.COLOR_GRAY);
    public static final Color COLOR_BLACK = ColorUtils.getSystemColor(SWT.COLOR_BLACK);
    public static final Color COLOR_INFO = ColorUtils.getSystemColor(SWT.COLOR_YELLOW);
    public static final Color COLOR_CROSSREFERENCE_BACKGROUND = ColorUtils.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
    public static final Color COLOR_CROSSREFERENCE_FOREGROUND = ColorUtils.getSystemColor(SWT.COLOR_INFO_FOREGROUND);
    public static final Color COLOR_WHITE = ColorUtils.getSystemColor(SWT.COLOR_WHITE);

    public static final Font FONT_BOLD = FontUtils.createFont(SWT.BOLD);
    public static final Font FONT_ITALIC = FontUtils.createFont(SWT.ITALIC);
}
