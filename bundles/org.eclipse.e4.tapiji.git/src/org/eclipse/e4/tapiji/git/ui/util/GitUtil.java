package org.eclipse.e4.tapiji.git.ui.util;


import org.eclipse.e4.tapiji.git.model.Reference;


public class GitUtil {

    public static String parseBranchName(Reference branch) {
        return replaceOrigin(branch.getName());
    }

    public static String parseBranchName(String branch) {
        return replaceOrigin(branch);
    }

    private static String replaceOrigin(String branchName) {
        if (branchName.contains("origin/")) {
            return branchName.replaceAll("origin/", "");
        }
        return branchName;
    }
}
