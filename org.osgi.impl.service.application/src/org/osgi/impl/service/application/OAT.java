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

import org.osgi.framework.*;
import java.lang.reflect.*;
import java.util.*;

public class OAT implements OATContainerInterface {
	
	private Hashtable oatHashtable = null;
	private Hashtable bundleMainClassHash = null;
	private ServiceRegistration containerService = null;

	public OAT() {
		bundleMainClassHash = new Hashtable();
	}
	
	public void start( BundleContext bc ) throws Exception {
		oatHashtable = getApplicationContextHashRef();
		
		containerService = bc.registerService( OATContainerInterface.class.getName(), this, new Hashtable() );
	}
	
	public void stop( BundleContext bc ) throws Exception {
		containerService.unregister();
		
		oatHashtable.clear();
		oatHashtable = null;
	}
	
	public void createApplicationContext(Bundle bundle, Map args, Object mainClass)
			throws Exception {
		
		OATApplicationContextImpl appCtx = new OATApplicationContextImpl( bundle, args );
		oatHashtable.put( mainClass, appCtx );
    
		bundleMainClassHash.put( bundle, mainClass );
	}

	public void removeApplicationContext(Bundle bundle) throws Exception {
		Object mainClass = bundleMainClassHash.remove( bundle );
		OATApplicationContextImpl appCtx = (OATApplicationContextImpl)oatHashtable.remove( mainClass );
    appCtx.ungetServiceReferences();
	}
	
	private Hashtable getApplicationContextHashRef() throws Exception {
		Class appFrameworkClass = org.osgi.application.Framework.class;
		Field field = appFrameworkClass.getDeclaredField( "appContextHash" );
		field.setAccessible(true);
		
		return (Hashtable)field.get( null );
	}
}
