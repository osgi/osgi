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
import org.osgi.service.log.LogService;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class OAT implements OATContainerInterface {
	
	private Hashtable oatHashtable = null;
	private ServiceRegistration containerService = null;
	private BundleContext bc = null;

	public OAT() {
		oatHashtable = new Hashtable();
	}
	
	public void start( BundleContext bc ) throws Exception {
		this.bc = bc;
		setApplicationContextHashRef();
		
		containerService = bc.registerService( OATContainerInterface.class.getName(), this, new Hashtable() );
	}
	
	public void stop( BundleContext bc ) throws Exception {
		containerService.unregister();
		
		oatHashtable = null;
		bc = null;
	}
	
	public void createApplicationContext(Object mainClass, Map args, Bundle bundle )
			throws Exception {
		
		parseOATXML( bundle );
		OATApplicationContextImpl appCtx = new OATApplicationContextImpl( bundle, args );		
		
		oatHashtable.put( mainClass, appCtx );    
	}

	public void removeApplicationContext(Object mainClass) throws Exception {
		OATApplicationContextImpl appCtx = (OATApplicationContextImpl)oatHashtable.remove( mainClass );
    appCtx.ungetServiceReferences();
	}
	
	private void setApplicationContextHashRef() throws Exception {
		Class appFrameworkClass = org.osgi.application.Framework.class;
		Field field = appFrameworkClass.getDeclaredField( "appContextHash" );
		field.setAccessible(true);
    field.set( null, oatHashtable);		
	}
	
	private void parseOATXML( Bundle bundle ) throws Exception {		
		URL url = bc.getBundle( bundle.getBundleId() ).getResource(	"OSGI-INF/app/apps.xml");
		if( url == null ) {
			Activator.log(bc, LogService.LOG_ERROR, "Cannot open the OAT XML file!", null );
			throw new Exception( "Cannot open the OAT XML file!" );			
		}
		
		InputStream in = url.openStream();
		if ( in == null ) {
  			Activator.log(bc, LogService.LOG_ERROR, "Cannot open the OAT XML file!", null );
				throw new Exception( "Cannot open the OAT XML file!" );
		}
		
		ServiceReference domParserReference = bc.getServiceReference( DocumentBuilderFactory.class.getName() );
		if (domParserReference == null) {
			Activator.log(bc, LogService.LOG_ERROR, "Cannot find the DOM parser factory!", null );
			throw new Exception( "Cannot find the DOM parser factory!" );
		}
			
		DocumentBuilderFactory domFactory = (DocumentBuilderFactory) bc.getService( domParserReference );
    boolean validating = domFactory.isValidating();
    domFactory.setValidating( false );
	  DocumentBuilder domParser = domFactory.newDocumentBuilder();
    domFactory.setValidating( validating );
		
		if( domParser == null ) {
				Activator.log(bc, LogService.LOG_ERROR, "Cannot create DOM parser!", null );
				bc.ungetService( domParserReference );
				throw new Exception( "Cannot create DOM parser!" );
		}

		Document doc = domParser.parse( in );
		
		bc.ungetService( domParserReference );
	}
}
