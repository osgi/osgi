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
package org.osgi.impl.service.application;

import java.util.Hashtable;
import org.osgi.framework.*;
import org.osgi.service.application.*;

/**
 * This class realizes the Application Handle
 */
public class ApplicationHandleImpl implements ApplicationHandle {
	private int						status;
	private ApplicationDescriptor	appDesc;
	private Application				application;
	private ServiceRegistration		serviceReg;
	private BundleContext			bc;

	public ApplicationHandleImpl(ApplicationDescriptor desc, BundleContext bc)
			throws Exception {
		application = null;
		appDesc = desc;
		status = ApplicationHandle.NONEXISTENT;
		this.bc = bc;
	}

	public int getAppStatus() {
		return status;
	}

	public ApplicationDescriptor getAppDescriptor() {
		return appDesc;
	}

	public void destroyApplication() throws Exception {
		if (status == ApplicationHandle.NONEXISTENT
				|| status == ApplicationHandle.STOPPING)
			throw new Exception("Invalid State");
		if (application != null) {
			status = ApplicationHandle.STOPPING;
			application.stopApplication();
			status = ApplicationHandle.NONEXISTENT;
			unregisterAppHandle();
		}
		else
			throw new Exception("Invalid application handle!");
	}

	public void suspendApplication() throws Exception {
		if (status != ApplicationHandle.RUNNING)
			throw new Exception("Invalid State");
		if (application != null) {
			status = ApplicationHandle.SUSPENDING;
			application.suspendApplication();
			status = ApplicationHandle.SUSPENDED;
		}
		else
			throw new Exception("Invalid application handle!");
	}

	public void resumeApplication() throws Exception {
		if (status != ApplicationHandle.SUSPENDED)
			throw new Exception("Invalid State");
		if (application != null) {
			status = ApplicationHandle.RESUMING;
			application.resumeApplication();
			status = ApplicationHandle.RUNNING;
		}
		else
			throw new Exception("Invalid application handle!");
	}

	void applicationStarted() {
		status = ApplicationHandle.RUNNING;
	}

	void setApplication(Application app) {
		application = app;
	}

	void registerAppHandle() {
		//registering the ApplicationHandle
		Hashtable props = new Hashtable();
		props.put("unique_id", appDesc.getUniqueID());
		serviceReg = bc.registerService(
				"org.osgi.service.application.ApplicationHandle", this, props);
	}

	void unregisterAppHandle() {
		if (serviceReg != null) {
			//unregistering the ApplicationHandle
			serviceReg.unregister();
			serviceReg = null;
		}
	}
}
