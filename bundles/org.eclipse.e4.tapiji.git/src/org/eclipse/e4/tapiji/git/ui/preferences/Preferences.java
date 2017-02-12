package org.eclipse.e4.tapiji.git.ui.preferences;


import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.tapiji.git.util.ListUtil;


@Creatable
@Singleton
public class Preferences {

    private static final String KEY_REPOSITORIES = Preferences.class.getPackage() + "_KEY_REPOSITORIES";
    private static final String KEY_SELECTED_REPOSITORY = Preferences.class.getPackage() + "_KEY_SELECTED_REPOSITORY";

    @Inject
    @Preference(nodePath = "org.eclipse.e4.tapiji.git")
    IEclipsePreferences preferences;

    public void addRepository(String path) {

        List<String> repos = ListUtil.unpackList(preferences.get(KEY_REPOSITORIES, ""), ",");
        if (!repos.contains(path)) {
            repos.add(path);
            preferences.put(KEY_SELECTED_REPOSITORY, ListUtil.packList(repos, ","));

            if (repos.size() == 1) {
                setSelectedRepository(path);
            } else if (repos.size() == 0) {
                setSelectedRepository("E:/cloni/.git");
            }
        }
    }

    public List<String> getRepositories() {
        return ListUtil.unpackList(preferences.get(KEY_REPOSITORIES, "E:/cloni/.git"), ",");
    }

    public void setSelectedRepository(String path) {

        preferences.put(KEY_SELECTED_REPOSITORY, path);
    }

    public String getSelectedRepository() {
        return preferences.get(KEY_SELECTED_REPOSITORY, "E:/cloni/.git");
    }
}
