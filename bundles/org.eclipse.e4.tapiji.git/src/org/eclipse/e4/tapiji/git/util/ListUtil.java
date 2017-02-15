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
        if (repos == null) {
            return "";
        } else {
            final JsonArray jsonRepos = new JsonArray();
            repos.stream().flatMap(repo -> Stream.of(JsonParserUtil.parseGitRepository(repo))).forEach(jsonObject -> jsonRepos.add(jsonObject.toString()));
            return jsonRepos.toString();
        }
    }

    public static List<GitRepository> unpackGitRepositoryList(String repos) {
        if (repos == null) {
            return new ArrayList<>();
        } else {
            return Json.parse(repos).asArray().values().stream().map(value -> {
                if (value.isString()) {
                    return Json.parse(value.asString()).asObject();
                } else {
                    return value.asObject();
                }
            }).map(obj -> JsonParserUtil.parseGitRepository(obj)).collect(Collectors.toList());
        }
    }

    public static String[] unpackArray(final String str, final String sep) {
        return ListUtil.unpackList(str, sep).toArray(new String[0]);
    }
}
