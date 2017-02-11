package org.eclipse.e4.tapiji.git.util;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

    public static String[] unpackArray(final String str, final String sep) {
        return ListUtil.unpackList(str, sep).toArray(new String[0]);
    }
}
