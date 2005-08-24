package org.osgi.impl.service.deploymentadmin.plugin;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.framework.Bundle;
import org.osgi.impl.service.deploymentadmin.DeploymentPackageImpl;
import org.osgi.impl.service.deploymentadmin.PluginCtx;

public class DeploymentThread extends Thread {
    
    private InputStream           is;
    private PluginCtx             pluginCtx;
    private String                mimeType;
    private String                location;
    private ListenerDp            listenerDp;
    private ListenerBundle        listenerBundle;
    
    public interface ListenerDp {
        void onFinish(DeploymentPackageImpl dp, Exception exception);
    }
    
    public interface ListenerBundle {
        void onFinish(Bundle b, Exception exception);
    }
    
    public DeploymentThread(String mimeType, PluginCtx pluginCtx, InputStream inputStream, 
            String dwnlID) 
    {
        this.pluginCtx = pluginCtx;
        this.is = inputStream;
        this.mimeType = mimeType;
        this.location = dwnlID;
    }
    
    public void setDpListener(ListenerDp l) {
        listenerDp = l;
    }
    
    public void setBundleListener(ListenerBundle l) {
        listenerBundle = l;
    }
    
    public void run() {
        if (mimeType.equals(PluginConstants.MIME_DP)) {
            try {
                DeploymentPackageImpl dp = (DeploymentPackageImpl) pluginCtx.
                        getDeploymentAdmin().installDeploymentPackage(is);
                listenerDp.onFinish(dp, null);
            } catch (Exception e) {
                listenerDp.onFinish(null, e);
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
        } else if (mimeType.equals(PluginConstants.MIME_BUNDLE)) {
            try {
                Bundle b = pluginCtx.getBundleContext().installBundle(location, is);
                b.start();
                listenerBundle.onFinish(b, null);
            } catch (Exception e) {
                listenerBundle.onFinish(null, e);
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
    
}