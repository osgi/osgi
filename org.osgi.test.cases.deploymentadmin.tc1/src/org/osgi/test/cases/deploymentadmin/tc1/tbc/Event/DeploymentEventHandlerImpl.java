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
 * Jul 15, 2005  Andre Assad
 * 145           Implement spec review issues
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tbc.Event;

import java.util.Vector;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;

/**
 * @author Andre Assad
 *
 */

public class DeploymentEventHandlerImpl implements EventHandler {
	
	public static final String TOPIC = "event.topic";
	
	private DeploymentTestControl tbc;
	
    public static final String TOPIC_INSTALL		= "org/osgi/service/deployment/INSTALL";
    public static final String TOPIC_UNINSTALL		= "org/osgi/service/deployment/UNINSTALL";
    public static final String TOPIC_COMPLETE		= "org/osgi/service/deployment/COMPLETE";

	private boolean install;
	private boolean uninstall;
	private boolean complete;
	
	private Event event;
    private Vector events;
	private boolean handlingComplete;
	private boolean handlingUninstall;
    private boolean verifying;
	
	public DeploymentEventHandlerImpl(DeploymentTestControl tbc) {
		this.tbc = tbc;
        this.events = new Vector();
	}

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
		String topic = event.getTopic();
        this.event = event;
        events.add(event);
        
        if (verifying) {
            if (!handlingComplete && !handlingUninstall && topic.equals(TOPIC_INSTALL)) {
                install = true;
                synchronized (tbc) {
                    tbc.notifyAll();
                }
            } else if (!handlingComplete && handlingUninstall && topic.equals(TOPIC_UNINSTALL)) {
                uninstall = true;
                synchronized (tbc) {
                    tbc.notifyAll();
                }
            } else if ((handlingComplete && topic.equals(TOPIC_COMPLETE))) {
                complete = true;
                synchronized (tbc) {
                    tbc.notifyAll();
                }
            }
        } else
            ; // do nothing
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
		this.handlingComplete = false;
		this.handlingUninstall = false;
		this.install = false;
		this.uninstall = false;
		this.complete = false;
		this.event = null;
		this.verifying = false;
        this.events = new Vector();
	}
	/**
	 * @return Returns the handlingUninstall.
	 */
	public boolean isHandlingUninstall() {
		return handlingUninstall;
	}
	/**
	 * @param handlingUninstall The handlingUninstall to set.
	 */
	public void setHandlingUninstall(boolean handlingUninstall) {
		this.handlingUninstall = handlingUninstall;
	}
    /**
     * @return Returns the verifying.
     */
    public boolean isVerifying() {
        return verifying;
    }
    /**
     * @param verifying The verifying to set.
     */
    public void setVerifying(boolean verifying) {
        this.verifying = verifying;
    }
    /**
     * @return Returns the events.
     */
    public Vector getEvents() {
        return events;
    }
}
