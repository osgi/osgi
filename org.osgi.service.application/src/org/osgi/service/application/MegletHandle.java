/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
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
 */
package org.osgi.service.application;

import java.util.*;
import java.io.*;
import java.security.*;
import org.osgi.framework.*;


/**
 * This service represents a Meglet instance. It is a specialization of the
 * application handle and provides features specific to the Meglet model.
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
	
	/**
	 * The Meglet instance is suspended.
	 * 
	 * @modelguid {8EBD44E3-883B-4515-8EEA-8469F6F16408}
	 */
	public  final static int 		SUSPENDED = 2;

	public MegletHandle(MegletContainer megletContainer, Meglet meglet,
			MegletDescriptor appDesc, BundleContext bc) throws Exception {
		
		appDescRef = megletContainer.getReference( appDesc );		
		pid = appDesc.getPID();
		
		status = MegletHandle.NONEXISTENT;
		this.megletContainer = megletContainer;
		this.bc = bc;
		this.meglet = meglet;
	}

	/**
	 * Returns the state of the Meglet instance specific to the Meglet model.
	 * 
	 * @throws IllegalStateException
	 *             if the Meglet handle is unregistered
	 * 
	 * @return the state of the Meglet instance
	 */
	public int getState() throws Exception {
		if( status == MegletHandle.NONEXISTENT )
			throw new Exception( "Invalid state!" );
		return status;
	}	

	/**
	 * Returns the instance id of the Meglet instance. Must be unique on the
	 * device.
	 * 
	 * @throws IllegalStateException
	 *             if the Meglet handle is unregistered
	 * 
	 * @return the instance id of the Meglet instance
	 */
	public String getInstanceID() {
		return pid;
	}

	/**
	 * Returns service reference to the application descriptor of the Meglet to
	 * which this Meglet instance belongs to.
	 * 
	 * @return the application descriptor of the Meglet to which this Meglet
	 *         instance belongs to
	 * 
	 * @throws IllegalStateException
	 *             if the Meglet handle is unregistered
	 */
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

	/**
	 * Destroys a Meglet according to the Meglet model. It calls the associated
	 * Meglet instance's stop() method with null parameter.
	 *  
	 */
	protected void destroySpecific() throws Exception {
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

	/**
	 * Suspends the Melet instance. It calls the associated Meglet instance's
	 * stop() method and passes a non-null output stream as a parameter. It must
	 * preserve the contents of the output stream written by the Meglet instance
	 * even across device restarts. The same content must be provided to a
	 * resuming Meglet instance.
	 * 
	 * @throws SecurityException
	 *             if the caller doesn't have "manipulate"
	 *             ApplicationAdminPermission for the corresponding application.
	 * @throws IllegalStateException
	 *             if the Meglet handle is unregistered
	 */
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

	/**
	 * Resumes the Meglet instance. It calls the associated Meglet instance's
	 * start() method and passes a non-null input stream as a parameter. It must
	 * have the same contents that was saved by the suspending Meglet instance.
	 * The same startup arguments must also be passed to the resuming Meglet
	 * instance that was passed to the first starting instance.
	 * 
	 * @throws SecurityException
	 *             if the caller doesn't have "manipulate"
	 *             ApplicationAdminPermission for the corresponding application.
	 * @throws IllegalStateException
	 *             if the Meglet handle is unregistered
	 */
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

		if( status != NONEXISTENT )
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