package org.osgi.impl.service.deploymentadmin.plugin;

import org.osgi.framework.Bundle;
import org.osgi.impl.service.deploymentadmin.DeploymentPackageImpl;

public class UndeployThread extends Thread {

    private DeploymentPackageImpl dp;
    private Bundle                bundle;
    private Listener              listener;
    
    public interface Listener {
        void onFinish(Exception exception);
    }

    public UndeployThread(DeploymentPackageImpl dp) {
        this.dp = dp;
    }
    
    public UndeployThread(Bundle bundle) {
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
