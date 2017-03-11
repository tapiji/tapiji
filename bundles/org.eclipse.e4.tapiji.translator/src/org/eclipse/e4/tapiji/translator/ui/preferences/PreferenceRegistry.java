package org.eclipse.e4.tapiji.translator.ui.preferences;


import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferencePage;


@Creatable
public class PreferenceRegistry {

    private static String PREFERENCE_EXTENSION_POINT_ID = "org.eclipse.e4.tapiji.translator.preferences";
    private static final String ELEMENT_PAGE = "page";
    private static final String ATTRIBUTE_CLASS = "class";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_ORDER = "order";
    private static final String ATTRIBUTE_CATEGORY = "category";
    private static final String ATTRIBUTE_TITLE = "title";
    private static final String TAG = PreferenceRegistry.class.getSimpleName();

    private PreferenceManager preferenceManager;

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    @PostConstruct
    public void onCreate(IEclipseContext context) {
        this.preferenceManager = new PreferenceManager();
        SortedMap<Integer, IConfigurationElement> elements = getConfigurationElements();
        elements.values().forEach(element -> {
            PreferencePage page = null;
            if (element.getAttribute(ATTRIBUTE_CLASS) != null) {
                Object obj;
                try {
                    obj = element.createExecutableExtension(ATTRIBUTE_CLASS);
                    if (obj instanceof PreferencePage) {
                        page = (PreferencePage) obj;
                        ContextInjectionFactory.inject(page, context);

                        if ((page.getTitle() == null || page.getTitle().isEmpty()) && element.getAttribute(ATTRIBUTE_TITLE) != null) {
                            page.setTitle(element.getAttribute(ATTRIBUTE_TITLE));
                        }
                        page.setPreferenceStore(new PreferenceScopeContext(InstanceScope.INSTANCE, "aasas"));
                        String category = element.getAttribute(ATTRIBUTE_CATEGORY);
                        if (category != null) {
                            preferenceManager.addTo(category, new PreferenceNode(element.getAttribute(ATTRIBUTE_ID), page));
                        } else {
                            preferenceManager.addToRoot(new PreferenceNode(element.getAttribute(ATTRIBUTE_ID), page));
                        }
                    } else {
                        Log.i(TAG, "Object must extend FieldEditorPreferencePage or PreferencePage");
                    }
                } catch (CoreException exception) {
                    Log.e(TAG, exception);
                }
            } else {
                Log.i(TAG, "Attribute class may not be null");
            }
        });

        Log.d("TAG", "" + elements.size());
    }

    private SortedMap<Integer, IConfigurationElement> getConfigurationElements() {
        return sortElements(filterElementByTag(Platform.getExtensionRegistry().getConfigurationElementsFor(PREFERENCE_EXTENSION_POINT_ID)));
    }

    private Predicate<IConfigurationElement> isPreference = config -> ELEMENT_PAGE.equals(config.getName());

    private IConfigurationElement[] filterElementByTag(IConfigurationElement[] configurationElements) {
        return Stream.of(configurationElements)
            .filter(isPreference)
            .collect(Collectors.collectingAndThen(Collectors.toList(), list -> list.toArray(new IConfigurationElement[list.size()])));
    }

    private SortedMap<Integer, IConfigurationElement> sortElements(IConfigurationElement[] iConfigurationElements) {
        SortedMap<Integer, IConfigurationElement> sortedElements = new TreeMap<>();
        for (int i = 0, lastOrder = 0; i < iConfigurationElements.length; i++) {
            String attribute = iConfigurationElements[i].getAttribute(ATTRIBUTE_ORDER);
            Integer order = null;
            try {
                order = new Integer(attribute);
            } catch (Exception e) {
                order = new Integer(++lastOrder);
            }
            while (sortedElements.containsKey(order))
                order = new Integer(lastOrder = order.intValue() + 1);
            sortedElements.put(order, iConfigurationElements[i]);
        }
        return sortedElements;
    }
}
