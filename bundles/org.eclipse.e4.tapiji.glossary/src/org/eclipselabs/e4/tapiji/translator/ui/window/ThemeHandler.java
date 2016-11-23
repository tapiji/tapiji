package org.eclipselabs.e4.tapiji.translator.ui.window;


import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;

@SuppressWarnings("restriction")
public class ThemeHandler {

    private static final String TAG = ThemeHandler.class.getSimpleName();

    @Execute
    public void execute(IThemeEngine engine, @Named("theme_id") String themeId) {
        System.out.println("Execute: " + TAG + " Theme id: " +themeId);
        engine.setTheme(themeId, true);
    }
}
