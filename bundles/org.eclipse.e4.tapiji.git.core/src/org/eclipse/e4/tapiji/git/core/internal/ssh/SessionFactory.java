package org.eclipse.e4.tapiji.git.core.internal.ssh;


import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.util.FS;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


public class SessionFactory extends JschConfigSessionFactory {

    private String privateKeyPath;

    @Override
    protected void configure(Host hc, Session session) {
        // no-op
    }

    @Override
    protected JSch createDefaultJSch(FS fs) throws JSchException {
        JSch defaultJSch = super.createDefaultJSch(fs);
        defaultJSch.addIdentity(privateKeyPath);
        return defaultJSch;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }
}
