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
package org.osgi.impl.service.megcontainer;

import java.util.*;
import java.io.*;
import java.security.*;
import org.osgi.framework.*;
import org.osgi.service.application.*;
import org.osgi.service.event.*;

/**
 * This class realizes the Application Handle
 */
public class MegletHandle implements ApplicationHandle {
	private int					status;
	private Meglet				meglet;
	private MegletContainer		megletContainer;
	private MegletDescriptor	appDesc;
	private ServiceRegistration	serviceReg;
	private BundleContext		bc;
	private Long				appHndServiceID;
	private Long				appDescServiceID;
	private File				suspendedFileName	= null;
	private static Long			counter				= new Long(0);
	private Map					resumeArgs			= null;

	public MegletHandle(MegletContainer megletContainer, Meglet meglet,
			MegletDescriptor desc, BundleContext bc) throws Exception {
		appDesc = desc;
		status = ApplicationHandle.NONEXISTENT;
		this.megletContainer = megletContainer;
		this.bc = bc;
		this.meglet = meglet;
		appHndServiceID = appDescServiceID = null;
		setStatus(status);
	}

	public int getAppStatus() {
		return status;
	}

	void setStatus(int status) {
		int lastStatus = this.status;
		this.status = status;

		Hashtable props = new Hashtable();
		props.put("name", (String) appDesc.getProperties("").get(
				ApplicationDescriptor.APPLICATION_NAME));
		if (appHndServiceID != null)
			props.put("application_instance.id", new Long(appHndServiceID
					.longValue()));
		if (appDescServiceID != null)
			props.put("application_descriptor.id", new Long(appDescServiceID
					.longValue()));

		String topic = "org/osgi/application/";

		switch (status) {
			case ApplicationHandle.RUNNING :
				if (lastStatus == ApplicationHandle.RESUMING)
					topic += "resumed";
				else if (lastStatus == ApplicationHandle.NONEXISTENT)
					topic += "started";
				break;
			case ApplicationHandle.SUSPENDING :
				topic += "suspending";
				break;
			case ApplicationHandle.SUSPENDED :
				topic += "suspended";
				break;
			case ApplicationHandle.RESUMING :
				topic += "resuming";
				break;
			case ApplicationHandle.STOPPING :
				topic += "stopping";
				break;
			case ApplicationHandle.NONEXISTENT :
				if (lastStatus == ApplicationHandle.STOPPING || lastStatus == ApplicationHandle.SUSPENDED)
					topic += "stopped";
				else if (lastStatus == ApplicationHandle.NONEXISTENT)
					topic += "starting";
				break;
		}

		sendEvent(new Event(topic, props), false);
	}

	boolean sendEvent(Event event, boolean asynchron) {
		ServiceReference eventRef = bc
				.getServiceReference("org.osgi.service.event.EventAdmin");
		if (eventRef != null) {
			EventAdmin eventAdmin = (EventAdmin) bc.getService(eventRef);
			if (eventAdmin != null) {
				try {
					if (asynchron)
						eventAdmin.postEvent(event);
					else
						eventAdmin.sendEvent(event);
					return true;
				}
				finally {
					bc.ungetService(eventRef);
				}
			}
		}
		return false;
	}

	public ApplicationDescriptor getAppDescriptor() {
		return appDesc;
	}

	public void startHandle(Map args) throws Exception {
		AccessController.checkPermission(new ApplicationAdminPermission(appDesc
				.getApplicationPID(), ApplicationAdminPermission.LAUNCH));

		if (args == null)
			resumeArgs = null;
		else
			resumeArgs = new Hashtable(args);

		if (status != ApplicationHandle.NONEXISTENT)
			throw new Exception("Invalid State");

		if (meglet != null) {
			meglet.startApplication(args, null);
			setStatus(ApplicationHandle.RUNNING);
			registerAppHandle();
		}
		else
			throw new Exception("Invalid meglet handle!");
	}

	public void destroyApplication() throws Exception {

		AccessController.checkPermission(new ApplicationAdminPermission(appDesc
				.getApplicationPID(), ApplicationAdminPermission.MANIPULATE));

		if (status == ApplicationHandle.NONEXISTENT
				|| status == ApplicationHandle.STOPPING)
			throw new Exception("Invalid State");
		if (meglet != null) {
			setStatus(ApplicationHandle.STOPPING);
			meglet.stopApplication(null);
			meglet = null;
		}
		setStatus(ApplicationHandle.NONEXISTENT);
		unregisterAppHandle();
	}

	public void suspendApplication() throws Exception {

		AccessController.checkPermission(new ApplicationAdminPermission(appDesc
				.getApplicationPID(), ApplicationAdminPermission.MANIPULATE));

		if (status != ApplicationHandle.RUNNING)
			throw new Exception("Invalid State");
		if (meglet != null) {
			setStatus(ApplicationHandle.SUSPENDING);

			synchronized (counter) {
				counter = new Long(counter.longValue() + 1);
				suspendedFileName = bc.getDataFile("SuspendedState-"
						+ counter.toString());
			}

			if (suspendedFileName.exists())
				suspendedFileName.delete();

			OutputStream os = new FileOutputStream(suspendedFileName);
			meglet.stopApplication(os);
			os.close();

			meglet = null;

			setStatus(ApplicationHandle.SUSPENDED);
		}
		else
			throw new Exception("Invalid meglet handle!");
	}

	public void resumeApplication() throws Exception {

		AccessController.checkPermission(new ApplicationAdminPermission(appDesc
				.getApplicationPID(), ApplicationAdminPermission.MANIPULATE));

		if (status != ApplicationHandle.SUSPENDED)
			throw new Exception("Invalid State");

		setStatus(ApplicationHandle.RESUMING);

		meglet = megletContainer.createMegletInstance(appDesc, true);
		appDesc.initMeglet(meglet, this);

		InputStream is = new FileInputStream(suspendedFileName);
		meglet.startApplication(resumeArgs, is);
		is.close();

		setStatus(ApplicationHandle.RUNNING);
	}

	void registerAppHandle() throws Exception {
		//registering the ApplicationHandle
		Hashtable props = new Hashtable();
		props.put(Constants.SERVICE_PID, appDesc.getApplicationPID());
		serviceReg = bc.registerService(
				"org.osgi.service.application.ApplicationHandle", this, props);
		appHndServiceID = (Long) serviceReg.getReference().getProperty(
				Constants.SERVICE_ID);
		ServiceReference[] appDescRefs = bc.getServiceReferences(
				"org.osgi.service.application.ApplicationDescriptor", "("
						+ Constants.SERVICE_PID + "="
						+ appDesc.getApplicationPID() + ")");
		if (appDescRefs != null && appDescRefs.length != 0)
			appDescServiceID = (Long) appDescRefs[0]
					.getProperty(Constants.SERVICE_ID);
	}

	void unregisterAppHandle() {
		if (serviceReg != null) {
			//unregistering the ApplicationHandle
			serviceReg.unregister();
			serviceReg = null;
		}
	}
}