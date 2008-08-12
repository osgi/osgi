/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.deploymentadmin.plugin;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.framework.Bundle;
import org.osgi.impl.service.deploymentadmin.DeploymentInputStream;
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
            	//DeploymentInputStream dis = new DeploymentInputStream(is);
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
                // ??? is the update correct in this way ???
                Bundle[] bs = pluginCtx.getBundleContext().getBundles();
                Bundle b = null;
                for (int i = 0; i < bs.length; i++) {
                    if (bs[i].getLocation().equals(location)) {
                        b = bs[i];
                        break;
                    }
                }
                DeploymentInputStream dis = new DeploymentInputStream(is);
                if (null == b) {
                    b = pluginCtx.getBundleContext().installBundle(location, dis);
                    b.start();
                } else {
                    b.update(dis);
                }
                
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