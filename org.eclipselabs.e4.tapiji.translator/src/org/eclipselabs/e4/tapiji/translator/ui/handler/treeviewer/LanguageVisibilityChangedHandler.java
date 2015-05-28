package org.eclipselabs.e4.tapiji.translator.ui.handler.treeviewer;


import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.constant.TranslatorConstant;
import org.eclipselabs.e4.tapiji.translator.preference.StoreInstanceState;


public final class LanguageVisibilityChangedHandler {

    private static final String TAG = LanguageVisibilityChangedHandler.class.getSimpleName();

    @Execute
    public void execute(final MMenuItem menuItem, final StoreInstanceState storeInstanceState, final IEventBroker eventBroker) {
        Log.d(TAG, String.format("Store reference language: %s", menuItem.isSelected()));
        if (menuItem.isSelected()) {
            storeInstanceState.showLocale(menuItem.getContainerData());
            eventBroker.post(TranslatorConstant.TOPIC_SHOW_LANGUAGE, LanguageViewHolder.create(true, menuItem.getContainerData()));
        } else {
            storeInstanceState.hideLocale(menuItem.getContainerData());
            eventBroker.post(TranslatorConstant.TOPIC_SHOW_LANGUAGE, LanguageViewHolder.create(false, menuItem.getContainerData()));
        }
    }


    public static class LanguageViewHolder {

        public boolean isVIsible;
        public String locale;

        private LanguageViewHolder(final boolean isVIsible, final String locale) {
            super();
            this.isVIsible = isVIsible;
            this.locale = locale;
        }

        public static LanguageViewHolder create(final boolean isVIsible, final String locale) {
            return new LanguageViewHolder(isVIsible, locale);
        }

        @Override
        public String toString() {
            return "LanguageViewHolder [isVIsible=" + isVIsible + ", locale=" + locale + "]";
        }
    }
}
