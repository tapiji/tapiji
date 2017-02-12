package org.eclipse.e4.tapiji.git.util;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.e4.tapiji.git.model.GitRepository;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;


public class ListUtil {

    private ListUtil() {
        // only static access
    }

    public static String packList(final List<String> strs, final String sep) {
        if (strs == null || strs.size() <= 0) {
            return "";
        } else {
            return strs.stream().collect(Collectors.joining(","));
        }
    }

    public static List<String> unpackList(final String string, final String sep) {
        if (string.length() == 0) {
            return new ArrayList<>(0);
        } else {
            return Stream.of(string.split(sep)).collect(Collectors.toList());
        }
    }

    public static String packArray(final String[] strs, final String sep) {
        if (strs == null || strs.length <= 0) {
            return "";
        } else {
            return Stream.of(strs).collect(Collectors.joining(","));
        }
    }

    public static String packGitRepositoryList(List<GitRepository> repos) {
        final JsonArray jsonRepos = new JsonArray();
        repos.stream()
            .flatMap(repo -> Stream.of(Json.object().add("url", repo.getUrl()).add("directory", repo.getDirectory())))
            .forEach(jsonObject -> jsonRepos.add(jsonObject.toString()));
        return jsonRepos.toString();
    }

    public static List<GitRepository> unpackGitRepositoryList(String repos) {
        return Json.parse(repos)
            .asArray()
            .values()
            .stream()
            .map(value -> Json.parse(value.asString()).asObject())
            .map(obj -> new GitRepository(obj.get("url").asString(), obj.get("directory").asString()))
            .collect(Collectors.toList());
    }

    public static String[] unpackArray(final String str, final String sep) {
        return ListUtil.unpackList(str, sep).toArray(new String[0]);
    }
}
