/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.impl.service.application;

import java.lang.reflect.*;
import java.security.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.application.*;
import org.osgi.service.log.LogService;

public class OATApplicationContextImpl implements ApplicationContext {
	
	private BundleContext bc = null;
	private Map startupParams = null;
	private LinkedList serviceList = null;
	
	public OATApplicationContextImpl( Bundle bundle, Map startupParams ) {
		bc = frameworkHook( bundle );
		this.startupParams = startupParams;
		serviceList = new LinkedList();
	}

	public void addBundleListener(BundleListener listener) {
	  bc.addBundleListener( listener );
	}
	public void addFrameworkListener(FrameworkListener listener) {
    bc.addFrameworkListener( listener );
	}
	public void addServiceListener(ServiceListener listener, String filter)
			throws InvalidSyntaxException {
		bc.addServiceListener( listener, filter );
	}
	public void addServiceListener(ServiceListener listener) {
    bc.addServiceListener( listener );
	}
	public Map getStartupParameters() {
		return startupParams;
	}
	public Object locateService(String referenceName) { /* TODO TODO TODO */
		ServiceReference ref = bc.getServiceReference( referenceName );
		if( ref == null )
		  return null;
		Object service =  bc.getService( ref );
		serviceList.add( ref );
		return service;
	}
	
	public Object[] locateServices(String referenceName) { /* TODO TODO TODO */
		try {
  		ServiceReference refs [] = bc.getServiceReferences( referenceName, null /* TODO */ );
	  	if( refs == null || refs.length == 0)
		    return null;
		
  		Object []objArray = new Object [ refs.length ];
		
	  	for( int i=0; i!=refs.length; i++ ) {
		  	objArray[ i ] = bc.getService( refs[ i ] );
		  	serviceList.add( refs[ i ] );
	  	}
		
		  return objArray;
		}catch( Exception e ) {
			return null;
		}
	}
	public ServiceRegistration registerService(String clazz, Object service,
			Dictionary properties) {
		return bc.registerService( clazz, service, properties );
	}
	public ServiceRegistration registerService(String[] clazzes,
			Object service, Dictionary properties) {
		return bc.registerService( clazzes, service, properties );
	}
	public void removeBundleListener(BundleListener listener) {
		bc.removeBundleListener( listener );
	}
	public void removeFrameworkListener(FrameworkListener listener) {
    bc.removeFrameworkListener( listener );
	}
	public void removeServiceListener(ServiceListener listener) {
		bc.removeServiceListener( listener );
	}
	
	void ungetServiceReferences()
	{
		while( !serviceList.isEmpty() )
			bc.ungetService( (ServiceReference)serviceList.removeFirst() );
	}
	
	private BundleContext frameworkHook( final Bundle bundle ) {
		if (System.getSecurityManager() == null) {
			return (BundleContext) invokeMethod(bundle, "getContext", null, null);
		}
		return (BundleContext) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return invokeMethod(bundle, "getContext", null, null);
			}
		});
	}
	
	private Object invokeMethod(Object object, String methodName, Class[] params, Object[] args) {
		Class clazz;
		if (object instanceof Class) {
			clazz = (Class) object;
			object = null;
		} else {
			clazz = object.getClass();
		}

		Method method = getMethod(clazz, methodName, params);
		if( method == null )
			return null;
		
		try {
			return method.invoke(object, args);
		} catch (IllegalAccessException e) {
			Activator.log( LogService.LOG_ERROR,
					"No access rights to method '" + methodName+ "' in class '" +
					clazz.getName() + "' !", e);
		} catch (InvocationTargetException e) {
			Activator.log( LogService.LOG_ERROR,
					"InvocationTargetException at method '" + methodName+ "' in class '" +
					clazz.getName() + "' !", e);
		}
		return null;
	}

	private Method getMethod(Class clazz, String methodName, Class[] params) {
		String origClassName = clazz.getName();
		
		Exception exception = null;
		for (; clazz != null; clazz = clazz.getSuperclass()) {
			try {
				Method method = clazz.getDeclaredMethod(methodName, params);
				// enable us to access the method if not public
				method.setAccessible(true);
				return method;
			} catch (NoSuchMethodException e) {
				exception = e;
				continue;
			}
		}

		Activator.log( LogService.LOG_ERROR,
				"Method '" + methodName + "' not found in class '" + 
				origClassName + "' !", exception);
		return null;
	}
}
