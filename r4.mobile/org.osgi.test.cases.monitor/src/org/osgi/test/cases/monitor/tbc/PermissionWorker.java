/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ===========   ==============================================================
 * 23 Sep, 2005  Andre Assad
 * 179           Implement TCK Review 
 * ===========   ==============================================================
 */
package org.osgi.test.cases.monitor.tbc;

import org.osgi.service.permissionadmin.PermissionInfo;


/**
 * Worker that helps permission settings for test bundles, due to permission
 * behavior in the CORE framework.
 * 
 * @author Andre Assad
 */
public class PermissionWorker extends Thread {
    
    private MonitorTestControl tbc;
    private PermissionInfo[] permissions;
    private String location;
    private boolean running;
    
    public PermissionWorker(MonitorTestControl tbc) {
        this.tbc = tbc;
    }

    public synchronized void run() {
    	running = true;
        while (running) {
			try {
				this.wait();
				tbc.getPermissionAdmin().setPermissions(location, permissions);
				this.notifyAll();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

    /**
	 * @return Returns the location.
	 */
    public String getLocation() {
        return location;
    }
    /**
     * @param location The location to set.
     */
    public void setLocation(String location) {
        this.location = location;
    }
    /**
     * @return Returns the permissions.
     */
    public PermissionInfo[] getPermissions() {
        return permissions;
    }
    /**
     * @param permissions The permissions to set.
     */
    public void setPermissions(PermissionInfo[] permissions) {
        this.permissions = permissions;
    }
    
	public void setRunning(boolean running) {
		this.running = running;
	}
}
