package org.eclipse.e4.tapiji.git.ui.preferences;


import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.tapiji.git.model.GitRepository;
import org.eclipse.e4.tapiji.git.util.JsonParserUtil;
import org.eclipse.e4.tapiji.git.util.ListUtil;
import org.eclipse.e4.tapiji.logger.Log;


@Creatable
@Singleton
public class Preferences {

    public static String TEST_REPO = "[{\"url\":\"https://github.com/tapiji/tapiji.git\",\"directory\":\"E:/asdasdadsd/.git\"},{\"url\":\"https://github.com/tapiji/git.extension.test.git\",\"directory\":\"E:/cloni/.git\"}]";
    public static String DEFAULT_REPO = "{\"url\":\"https://github.com/tapiji/git.extension.test.git\",\"directory\":\"E:/cloni/.git\"}";

    private static final String KEY_REPOSITORIES = Preferences.class.getPackage() + "_KEY_REPOSITORIES";
    private static final String KEY_SELECTED_REPOSITORY = Preferences.class.getPackage() + "_KEY_SELECTED_REPOSITORY";

    @Inject
    @SuppressWarnings("restriction")
    @Preference(nodePath = "org.eclipse.e4.tapiji.git")
    IEclipsePreferences preferences;

    public void addRepository(String name, String path) {
        Log.d("NEW REPO: ", name + " == " + path);
        GitRepository repository = new GitRepository(name, path);
        List<GitRepository> repos = ListUtil.unpackGitRepositoryList(preferences.get(KEY_REPOSITORIES, TEST_REPO));
        if (!repos.contains(repository)) {
            Log.d("ADD NEW REPO: ", name + " == " + path);
            repos.add(repository);
            preferences.put(KEY_REPOSITORIES, ListUtil.packGitRepositoryList(repos));

            if (repos.size() >= 1) {
                setSelectedRepository(repository);
            } else if (repos.size() == 0) {
                setSelectedRepository(JsonParserUtil.parseGitRepository(DEFAULT_REPO));
            }
        }
    }

    public List<GitRepository> getRepositories() {
        return ListUtil.unpackGitRepositoryList(preferences.get(KEY_REPOSITORIES, TEST_REPO));
    }

    public void setSelectedRepository(GitRepository repository) {
        preferences.put(KEY_SELECTED_REPOSITORY, JsonParserUtil.parseGitRepositoryString(repository));
    }

    public GitRepository getSelectedRepository() {
        return JsonParserUtil.parseGitRepository(preferences.get(KEY_SELECTED_REPOSITORY, DEFAULT_REPO));
    }
}
