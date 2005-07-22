package org.osgi.impl.service.deploymentadmin.plugin;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.impl.service.deploymentadmin.DeploymentPackageImpl;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;

public class DeploymentThread extends Thread {
    
    private InputStream           is;
    private DeploymentAdmin       da;
    private Listener              listener;
    
    public interface Listener {
        void onFinish(DeploymentPackageImpl dp, DeploymentException exception);
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
        } catch (DeploymentException e) {
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