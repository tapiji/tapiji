package org.eclipse.e4.tapiji.git.core.test;


import static org.junit.Assert.assertEquals;
import java.io.File;
import org.eclipse.e4.tapiji.git.core.internal.util.GitUtil;
import org.junit.Test;


public class GitUtilsTest {

    @Test
    public void gitDirectoryTest() {
        File dir = GitUtil.getGitDirectory("E:/cloni");
        assertEquals(new File("E:/cloni/.git"), dir);
    }

}
