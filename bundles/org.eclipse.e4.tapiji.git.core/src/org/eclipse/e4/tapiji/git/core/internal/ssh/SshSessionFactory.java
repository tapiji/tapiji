package org.eclipse.e4.tapiji.git.core.internal.ssh;


import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.util.FS;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


public class SshSessionFactory extends JschConfigSessionFactory {

    private static final String TAG = SshSessionFactory.class.getSimpleName();
    private String privateKeyPath;

    @Override
    protected void configure(Host hc, Session session) {
        Log.d(TAG, "configure called with [" + hc + ", " + session + "]");
        // no-op
    }

    @Override
    protected JSch getJSch(Host hc, FS fs) throws JSchException {
        Log.d(TAG, "createDefaultJSch called with [" + hc + ", " + fs + "]");
        return super.getJSch(hc, fs);
    }

    @Override
    protected JSch createDefaultJSch(FS fs) throws JSchException {
        Log.d(TAG, "createDefaultJSch called with [" + fs + "]");
        return super.createDefaultJSch(fs);
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }
}
