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
 * 28/04/2005    Andre Assad
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.tbc.Event;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * @author Andre Assad
 * 
 * This class handles events raised during installation and uninstallation of
 * bundles in a deployment package.
 */
public class BundleEventHandlerImpl implements EventHandler {
	
	public static final String TOPIC = "event.topic";
	
    public static final String TOPIC_INSTALL = "org/osgi/service/deployment/INSTALL";
	public static final String TOPIC_UNINSTALL = "org/osgi/service/deployment/UNINSTALL";
	public static final String TOPIC_COMPLETE = "org/osgi/service/deployment/COMPLETE";

	private boolean install;
	private boolean uninstall;
	private boolean complete;
	
	private Event event;
	
	private boolean sychronizing;
	private boolean handlingComplete;

	/**
	 * @return Returns the complete.
	 */
	public boolean isComplete() {
		return complete;
	}
	/**
	 * @return Returns the install.
	 */
	public boolean isInstall() {
		return install;
	}
	/**
	 * @return Returns the uninstall.
	 */
	public boolean isUninstall() {
		return uninstall;
	}

	public void handleEvent(Event event) {
		if (sychronizing) {
			String topic = event.getTopic();
			this.event = event;

			if (!handlingComplete) {
				if (topic.equals(TOPIC_INSTALL)) {
					install = true;
				} else if (topic.equals(TOPIC_UNINSTALL)) {
					uninstall = true;
				}
			} else if (handlingComplete) {
				if (topic.equals(TOPIC_COMPLETE)) {
					complete = true;
				}
			}
			notifyAll();
		}
	}

	/**
	 * @return Returns the topic.
	 */
	public String getTopic() {
		return (event!=null)?event.getTopic():"";
	}
	/**
	 * @return Returns the properties.
	 */
	public String[] getProperties() {
		return (event!=null)?event.getPropertyNames():null;
	}
	
	/**
	 * @return Returns an specific property
	 */
	public Object getProperty(String name) {
		return (event!=null)?event.getProperty(name):"";
	}

	/**
	 * @return Returns the sychronizing.
	 */
	public boolean isSychronizing() {
		return sychronizing;
	}
	/**
	 * @param sychronizing The sychronizing to set.
	 */
	public void setSychronizing(boolean sychronizing) {
		this.sychronizing = sychronizing;
	}
	/**
	 * @return Returns the handlingComplete.
	 */
	public boolean isHandlingComplete() {
		return handlingComplete;
	}
	/**
	 * @param handlingComplete The handlingComplete to set.
	 */
	public void setHandlingComplete(boolean handlingComplete) {
		this.handlingComplete = handlingComplete;
	}
	/**
	 * Reset the condition varibles
	 */
	public void reset() {
		handlingComplete = false;
		sychronizing = false;
		install = false;
		uninstall = false;
		complete = false;
	}
}
