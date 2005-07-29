package org.osgi.impl.service.deploymentadmin.plugin;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.framework.Bundle;
import org.osgi.impl.service.deploymentadmin.DAConstants;
import org.osgi.impl.service.deploymentadmin.DeploymentAdminImpl;
import org.osgi.impl.service.deploymentadmin.DeploymentPackageImpl;
import org.osgi.impl.service.deploymentadmin.plugin.DeploymentThread.ListenerBundle;
import org.osgi.impl.service.deploymentadmin.plugin.DeploymentThread.ListenerDp;

public class UndeployThread extends Thread {

    private DeploymentAdminImpl   da;
    private DeploymentPackageImpl dp;
    private Bundle                bundle;
    private Listener              listener;
    
    public interface Listener {
        void onFinish(Exception exception);
    }

    private UndeployThread(DeploymentAdminImpl da) {
        this.da = da;
    }
    
    public UndeployThread(DeploymentAdminImpl da, DeploymentPackageImpl dp) {
        this(da);
        this.dp = dp;
    }
    
    public UndeployThread(DeploymentAdminImpl da, Bundle bundle) {
        this(da);
        this.bundle = bundle;
    }
    
    public void setListener(Listener l) {
        listener = l;
    }
    
    public void run() {
        if (dp != null) {
            try {
                dp.uninstall();
                listener.onFinish(null);
            } catch (Exception e) {
                listener.onFinish(e);
            }
        } else {
            try {
                if (bundle.getState() == Bundle.ACTIVE)
                    bundle.stop();
                bundle.uninstall();
                listener.onFinish(null);
            } catch (Exception e) {
                listener.onFinish(e);
            }
        }
    }

}
