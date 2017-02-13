package org.eclipse.e4.tapiji.git.util;


import org.eclipse.e4.tapiji.git.model.GitRepository;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;


public class JsonParserUtil {

    public static GitRepository parseGitRepository(JsonObject obj) {
        return new GitRepository(obj.get("url").asString(), obj.get("directory").asString());
    }

    public static GitRepository parseGitRepository(String repo) {
        return parseGitRepository(Json.parse(repo).asObject());
    }

    public static JsonObject parseGitRepository(GitRepository repository) {
        return Json.object().add("url", repository.getUrl()).add("directory", repository.getDirectory());
    }

    public static String parseGitRepositoryString(GitRepository repo) {
        return parseGitRepository(repo).asObject().toString();
    }
}
