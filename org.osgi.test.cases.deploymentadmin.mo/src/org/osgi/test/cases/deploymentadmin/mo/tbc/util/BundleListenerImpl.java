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
 * ============  ==============================================================
 * Oct 10, 2005  Andre Assad
 * 162           Refactoring
 * ============  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.mo.tbc.util;

import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.test.cases.deploymentadmin.mo.tbc.DeploymentmoTestControl;

/**
 * @author Andre Assad
 * 
 */
public class BundleListenerImpl implements BundleListener {
	
	private DeploymentmoTestControl tbc;
	private Vector events;
	private int currentType;
	private Bundle currentBundle;

	/**
	 * @param control
	 */
	public BundleListenerImpl(DeploymentmoTestControl tbc) {
		this.tbc = tbc;
	}

	public void bundleChanged(BundleEvent event) {
		currentType = event.getType();
		currentBundle = event.getBundle();
		events.add(event);

		synchronized (tbc) {
			tbc.notifyAll();
		}
	}
  
  public void begin() {
    reset();
    tbc.getContext().addBundleListener(this);
  }
  
  public void end() {
    tbc.getContext().removeBundleListener(this);
  }
	
	public void reset() {
		currentType = 0;
		currentBundle = null;
		events = new Vector();
	}
	/**
	 * @return Returns the events.
	 */
	public Vector getEvents() {
		return events;
	}
	/**
	 * @return Returns the currentBundle.
	 */
	public Bundle getCurrentBundle() {
		return currentBundle;
	}
	/**
	 * @return Returns the currentType.
	 */
	public int getCurrentType() {
		return currentType;
	}
}
