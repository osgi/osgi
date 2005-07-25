package org.osgi.impl.service.deploymentadmin.plugin;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.impl.service.deploymentadmin.DeploymentPackageImpl;
import org.osgi.service.deploymentadmin.DeploymentAdmin;

public class DeploymentThread extends Thread {
    
    private InputStream           is;
    private DeploymentAdmin       da;
    private Listener              listener;
    
    public interface Listener {
        void onFinish(DeploymentPackageImpl dp, Exception exception);
    }
    
    public DeploymentThread(DeploymentAdmin da, InputStream inputStream, Listener listener) {
        this.da = da;
        this.is = inputStream;
        this.listener = listener;
    }
    
    public void run() {
        DeploymentPackageImpl dp = null;
        try {
            dp = (DeploymentPackageImpl) da.installDeploymentPackage(is);
            listener.onFinish(dp, null);
        } catch (Exception e) {
            listener.onFinish(dp, e);
        } finally {
            if (null != is) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}