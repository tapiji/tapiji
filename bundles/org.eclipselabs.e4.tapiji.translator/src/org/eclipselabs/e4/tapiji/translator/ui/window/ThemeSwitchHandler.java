package org.eclipselabs.e4.tapiji.translator.ui.window;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;

public class ThemeSwitchHandler {
    private static final String DEFAULT_THEME = "org.eclipselabs.e4.tapiji.translator.default";
    private static final String DARK_THEME = "org.eclipselabs.e4.tapiji.translator.dark";
    
    @Execute
    public void switchTheme(IThemeEngine engine) {
      if (!engine.getActiveTheme().getId().equals(DEFAULT_THEME)){
        engine.setTheme(DEFAULT_THEME, true);
      } else {
        engine.setTheme(DARK_THEME, true);
      }
    }
}
