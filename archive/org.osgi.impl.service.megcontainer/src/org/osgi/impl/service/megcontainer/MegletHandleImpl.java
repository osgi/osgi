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
package org.osgi.impl.service.megcontainer;

import java.util.*;
import java.io.*;
import java.lang.reflect.Method;
import java.security.*;
import org.osgi.framework.*;
import org.osgi.meglet.Meglet;
import org.osgi.service.application.*;
import org.osgi.service.application.meglet.*;


/**
 * This service represents a Meglet instance. It is a specialization of the
 * application handle and provides features specific to the Meglet model.
 */
public final class MegletHandleImpl implements MegletHandle.Delegate {
	private String								status;
	private Meglet								meglet;
	private ServiceReference			appDescRef;
	private ServiceRegistration		serviceReg;
	private File									suspendedFileName	= null;
	private String      					pid;
	private MegletHandle 					megletHandle;
	private MegletDescriptorImpl	megletDelegate;
	private MegletContainer 			megletContainer;
	private BundleContext					bc;
	private static Long						counter						= new Long(0);
	
	/**
	 * Returns the state of the Meglet instance specific to the Meglet model.
	 * 
	 * @throws IllegalStateException
	 *             if the Meglet handle is unregistered
	 * 
	 * @return the state of the Meglet instance
	 */
	public String getState() {
		if( status == null )
			throw new IllegalStateException();
		return status;
	}	



	public ServiceReference startHandle(Map args) throws Exception {
		AccessController.checkPermission(new ApplicationAdminPermission(pid, 
				                             ApplicationAdminPermission.LIFECYCLE));

		if (status != null )
			throw new IllegalStateException();

		if (meglet != null) {
			startApplication( meglet, args, null);
			setStatus( ApplicationHandle.RUNNING );
			registerAppHandle();

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
	public void destroySpecific() throws Exception {
		if ( status == null )
			throw new IllegalStateException();
		if ( meglet != null ) {
			stopApplication( meglet, null);
			meglet = null;
		}
		setStatus( null );
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
				ApplicationAdminPermission.LIFECYCLE));

		if (status != ApplicationHandle.RUNNING)
			throw new IllegalStateException();
		if (meglet != null) {
			synchronized (counter) {
				counter = new Long(counter.longValue() + 1);
				suspendedFileName = bc.getDataFile("SuspendedState-"
						+ counter.toString());
			}

			if (suspendedFileName.exists())
				suspendedFileName.delete();

			OutputStream os = new FileOutputStream(suspendedFileName);
			stopApplication( meglet, os);
			os.close();

			meglet = null;

			setStatus( MegletHandle.SUSPENDED );
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
				ApplicationAdminPermission.LIFECYCLE));

		if (status != MegletHandle.SUSPENDED)
			throw new IllegalStateException();

		meglet = megletContainer.createMegletInstance( megletDelegate, true);

		InputStream is = new FileInputStream(suspendedFileName);
		startApplication( meglet, null, is);
		is.close();

		setStatus(ApplicationHandle.RUNNING);
	}

	private void setStatus( String status ) {
		this.status = status;

		if( status != null && serviceReg != null )
			serviceReg.setProperties( properties() );
	}

	private Hashtable properties() {
		Hashtable props = new Hashtable();
		props.put( ApplicationHandle.APPLICATION_PID, megletHandle.getInstanceID() );
		props.put( ApplicationHandle.APPLICATION_STATE, status );
		props.put( ApplicationHandle.APPLICATION_DESCRIPTOR, appDescRef.getProperty( Constants.SERVICE_PID ) );		
		return props;
	}
	
	private void registerAppHandle() throws Exception {
		serviceReg = bc.registerService(
				"org.osgi.service.application.ApplicationHandle", megletHandle, properties() );
	}

	private void unregisterAppHandle() {
		if (serviceReg != null) {
			//unregistering the ApplicationHandle
			serviceReg.unregister();
			serviceReg = null;
		}
	}

	void startApplication( Meglet meglet, Map args, InputStream stream ) throws Exception {
		Class megletClass = Meglet.class;
		Method setupMethod = megletClass.getDeclaredMethod( "startApplication", new Class [] {
										Map.class, InputStream.class } );
		setupMethod.setAccessible( true );
		setupMethod.invoke( meglet, new Object [] { args, stream } );
	}	


	void stopApplication( Meglet meglet, OutputStream stream ) throws Exception {
		Class megletClass = Meglet.class;
		Method setupMethod = megletClass.getDeclaredMethod( "stopApplication", new Class [] {
										OutputStream.class } );
		setupMethod.setAccessible( true );
		setupMethod.invoke( meglet, new Object [] { stream } );
	}	

	public void init( BundleContext bc, MegletContainer megletContainer, Meglet meglet ) {
		this.bc = bc;
		this.megletContainer = megletContainer;
		this.meglet = meglet;

		appDescRef = megletContainer.getReference( megletDelegate );
		pid = (String)appDescRef.getProperty( ApplicationDescriptor.APPLICATION_PID );
	}
	
	public void setMegletHandle( MegletHandle handle, MegletDescriptor appDesc,
															 MegletDescriptor.Delegate delegate ) {
		megletHandle = handle;
		
		megletDelegate = (MegletDescriptorImpl) delegate;
		status = null;
	}
	
	public MegletHandle getMegletHandle() {
		return megletHandle;
	}
}
