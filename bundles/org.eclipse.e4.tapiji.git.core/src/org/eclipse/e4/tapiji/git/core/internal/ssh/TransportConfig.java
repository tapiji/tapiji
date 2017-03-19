package org.eclipse.e4.tapiji.git.core.internal.ssh;


import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.Transport;


public class TransportConfig implements TransportConfigCallback {

    private static final String TAG = TransportConfig.class.getSimpleName();

    @Override
    public void configure(Transport transport) {
        Log.d(TAG, "configure called with [" + transport + "]");
        //SshTransport sshTransport = (SshTransport) transport;
        //sshTransport.setSshSessionFactory(sshFactory);
    }
}
