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
package org.osgi.service.application;

import java.util.*;
import java.io.*;
import java.security.*;
import org.osgi.framework.*;

/**
 * This class realizes the Application Handle
 */
public final class MegletHandle extends ApplicationHandle {
	private int									status;
	private Meglet							meglet;
	private MegletContainer			megletContainer;
	private ServiceReference		appDescRef;
	private ServiceRegistration	serviceReg;
	private BundleContext				bc;
	private File								suspendedFileName	= null;
	private static Long					counter						= new Long(0);
	private Map									resumeArgs				= null;
	private String      				pid;

	private final static int 		NONEXISTENT = -1;
	public  final static int 		SUSPENDED = 2;
	
	public MegletHandle(MegletContainer megletContainer, Meglet meglet,
			MegletDescriptor appDesc, BundleContext bc) throws Exception {
		
		appDescRef = megletContainer.getReference( appDesc );		
		pid = appDesc.getApplicationPID();
		
		status = MegletHandle.NONEXISTENT;
		this.megletContainer = megletContainer;
		this.bc = bc;
		this.meglet = meglet;
	}

	public int getApplicationState() throws Exception {
		if( status == MegletHandle.NONEXISTENT )
			throw new Exception( "Invalid state!" );
		return status;
	}
	
	public String getInstanceID() {
		return pid;
	}

	public ServiceReference getApplicationDescriptor() {
		return appDescRef;
	}

	public ServiceReference startHandle(Map args) throws Exception {
		AccessController.checkPermission(new ApplicationAdminPermission(pid, 
				                             ApplicationAdminPermission.LAUNCH));

		if (args == null)
			resumeArgs = null;
		else
			resumeArgs = new Hashtable(args);

		if (status != MegletHandle.NONEXISTENT)
			throw new Exception("Invalid State");

		if (meglet != null) {
			meglet.startApplication(args, null);
			registerAppHandle();
			setStatus(ApplicationHandle.RUNNING);

			return serviceReg.getReference();
		}
		else
			throw new Exception("Invalid meglet handle!");		
	}

	public void destroySpecific() throws Exception {

		AccessController.checkPermission(new ApplicationAdminPermission(pid, 
				ApplicationAdminPermission.MANIPULATE));

		if (status == MegletHandle.NONEXISTENT
				|| status == ApplicationHandle.STOPPING)
			throw new Exception("Invalid State");
		if (meglet != null) {
			setStatus(ApplicationHandle.STOPPING);
			meglet.stopApplication(null);
			meglet = null;
		}
		setStatus(MegletHandle.NONEXISTENT);
		unregisterAppHandle();
	}

	public void suspend() throws Exception {

		AccessController.checkPermission(new ApplicationAdminPermission(pid, 
				ApplicationAdminPermission.MANIPULATE));

		if (status != ApplicationHandle.RUNNING)
			throw new Exception("Invalid State");
		if (meglet != null) {
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

			setStatus(MegletHandle.SUSPENDED);
		}
		else
			throw new Exception("Invalid meglet handle!");
	}

	public void resume() throws Exception {

		AccessController.checkPermission(new ApplicationAdminPermission(pid, 
				ApplicationAdminPermission.MANIPULATE));

		if (status != MegletHandle.SUSPENDED)
			throw new Exception("Invalid State");

		MegletDescriptor appDesc = (MegletDescriptor)bc.getService( appDescRef );
		meglet = megletContainer.createMegletInstance(appDesc, true);
		appDesc.initMeglet(meglet, this);
		bc.ungetService( appDescRef );

		InputStream is = new FileInputStream(suspendedFileName);
		meglet.startApplication(resumeArgs, is);
		is.close();

		setStatus(ApplicationHandle.RUNNING);
	}

	private void setStatus(int status) {
		this.status = status;

		serviceReg.setProperties( properties() );
	}

	private Hashtable properties() {
		Hashtable props = new Hashtable();
		props.put( "application.pid", pid );
		props.put( "application.state", new Integer( status ) );
		props.put( "descriptor.pid", appDescRef.getProperty( Constants.SERVICE_PID ) );		
		return props;
	}
	
	private void registerAppHandle() throws Exception {
		serviceReg = bc.registerService(
				"org.osgi.service.application.ApplicationHandle", this, properties() );
	}

	private void unregisterAppHandle() {
		if (serviceReg != null) {
			//unregistering the ApplicationHandle
			serviceReg.unregister();
			serviceReg = null;
		}
	}
}