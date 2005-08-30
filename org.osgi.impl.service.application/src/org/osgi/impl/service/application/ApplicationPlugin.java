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

import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.application.*;
import org.osgi.service.dmt.*;
import org.osgi.service.dmt.spi.*;

/**
 * DMT plugin for the Application Admin
 */
public class ApplicationPlugin implements BundleActivator, DataPluginFactory,
		ExecPlugin, ReadWriteDataSession {

	// URI constants
	static final String					URI_ROOT_OSGI			= "./OSGi";
	static final String					URI_ROOT_APP			= "./OSGi/apps";
	static final String					URI_ROOT_APPINST	= "./OSGi/app_instances";
	static final String   			PREFIX_APPS				= "apps";
	static final String					PREFIX_APPINST   	= "app_instances";
	
	static final String         propertyNames[]   = { "localizedname", "version", "vendor", "autostart", 
																										"locked", "singleton", "bundle_id", "required_services", 
																										"launch" };
	
	private BundleContext				bc;
	private Hashtable						execIds						= new Hashtable();
	
	private ServiceRegistration pluginReg;

	public void start(BundleContext bc) throws Exception {
		this.bc = bc;
		// registers the data and exec DMT plugin
		Dictionary dict = new Hashtable();
		dict.put("dataRootURIs", new String[] {URI_ROOT_APP, URI_ROOT_APPINST});
		dict.put("execRootURIs", new String[] {URI_ROOT_APP, URI_ROOT_APPINST});
		String[] ifs = new String[] {DataPluginFactory.class.getName(),
				DataPluginFactory.class.getName()};
		// unregistered by the OSGi framework
		pluginReg = bc.registerService(ifs, this, dict);
		// start track ApplicationAdmin
	}

	public void stop(BundleContext bc) throws Exception {
		pluginReg.unregister();
		this.bc = null;
	}

	public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
		return this;
	}

	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
		return this;
	}

	public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
    return null; /* no supported */
	}

	public void execute(DmtSession session, String[] path, String correlator, String data) throws DmtException {
		// ./OSGi/apps/<unique_id> 
		if( path.length == 4 && path[ 2 ].equals( PREFIX_APPINST )) {
			
			try 
			{
				ServiceReference[] hrefs = bc.getServiceReferences(
						ApplicationHandle.class.getName(), "(" + ApplicationHandle.APPLICATION_PID + "=" + path[ 3 ] + ")");
			
				if (hrefs == null)
					throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
				
				ApplicationHandle handle = (ApplicationHandle) bc.getService( hrefs[ 0 ] );
				if ( data.equalsIgnoreCase( "STOP" ))
					handle.destroy();
				bc.ungetService( hrefs[ 0 ] );
				return;
			}catch( Exception e ) {
				throw new DmtException(path, DmtException.COMMAND_FAILED,
						"Execution of the node failed.");
			}
		}
		
		// ./OSGi/apps/<unique_id>/launch/<exec_id> 
		if ( path.length != 6 || !path[ 2 ].equals( PREFIX_APPS ))
            // should never happen because of meta-data
			throw new DmtException(path, DmtException.METADATA_MISMATCH,
					"Execution of the node is not allowed.");
		
		ServiceReference appDescRef = getApplicationDescriptorRef( path[ 3 ] );
		if (appDescRef == null)
			throw new DmtException(path, DmtException.NODE_NOT_FOUND,
					"Node not found.");		
		
		String key = path[ 3 ] + "/" + path[ 5 ];
		
		Hashtable dmtArgs = (Hashtable) execIds.get(key);
		if ( dmtArgs == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND,
					"Node not found.");
		
		ApplicationDescriptor appDesc = (ApplicationDescriptor) bc.getService( appDescRef );
		
		Hashtable args = new Hashtable();
		
		Iterator it = dmtArgs.keySet().iterator();
		while( it.hasNext() ) {
			Object argKey = it.next();
			DmtData argValue = (DmtData) dmtArgs.get( argKey );
			
			switch( argValue.getFormat() ) {
				case DmtData.FORMAT_BINARY:
					args.put( argKey, argValue.getBinary() );
					break;
				case DmtData.FORMAT_BOOLEAN:
					args.put( argKey, new Boolean( argValue.getBoolean() ) );
					break;
				case DmtData.FORMAT_INTEGER:
					args.put( argKey, new Integer( argValue.getInt() ) );
					break;
				case DmtData.FORMAT_FLOAT:
					args.put( argKey, new Float( argValue.getInt() ) );
					break;
				case DmtData.FORMAT_STRING:
				case DmtData.FORMAT_XML:
					args.put( argKey, argValue.getString() );
					break;
			}
		}
						
		try {
			appDesc.launch( args );
			
			execIds.remove( key );
		}
		catch (Exception e) {
			throw new DmtException(path, DmtException.COMMAND_FAILED, e.getMessage(), e);
		}
		bc.ungetService( appDescRef );
	}

	public void copy(String[] path1, String[] path2, boolean recursive) throws DmtException {
		throw new DmtException( path1, DmtException.FEATURE_NOT_SUPPORTED,
                            "Copy not supported!" ); 
	}

	public void createInteriorNode(String[] path, String type) throws DmtException {
		if( type != null )
		  throw new DmtException( path, DmtException.COMMAND_FAILED, 
                              "Cannot set type property of application nodes!" );
		
		// ./OSGi/apps/<unique_id>/launch/<exec_id> 
		if ( path.length != 6 || !path[ 2 ].equals( PREFIX_APPS ) || !path[ 4 ].equals("launch") )
            // should never happen because of meta-data
			throw new DmtException(path, DmtException.METADATA_MISMATCH, "Operation is not allowed.");
		
		checkUniqueID( path );
		
		execIds.put( path[ 1 ] + "/" + path[ 3 ], new Hashtable());
	}

	public void createLeafNode(String[] path, DmtData value, String type) throws DmtException {

		if( type != null ) {
		  throw new DmtException( path, DmtException.METADATA_MISMATCH, 
		                          "Cannot set type property of application nodes!" );
		}
		
		// ./OSGi/apps/<unique_id>/launch/<exec_id>/<parameter> 
		if ( path.length != 7 || !path[ 2 ].equals( PREFIX_APPS ) || !path[ 4 ].equals("launch")  )
            // should never happen because of meta-data
		    throw new DmtException(path, DmtException.METADATA_MISMATCH,
					"Operation is not allowed.");
		
		checkUniqueID( path );
				
		String key = path[ 3 ] + "/" + path[ 5 ];
		Hashtable ht = (Hashtable) execIds.get( key );
		if (ht == null)
			throw new DmtException(path, DmtException.COMMAND_FAILED,
					"Parent node " + path[ 5 ] + " does not exist.");
		ht.put( path[ 6 ], value );		
	}

	public void deleteNode(String[] path) throws DmtException {

		// ./OSGi/apps/<unique_id>/launch/<exec_id> 
		if ( path.length == 6 && path[ 2 ].equals( PREFIX_APPS ) && path[ 4 ].equals( "launch" ) ) {
			checkUniqueID( path );
			
 			execIds.remove( path[ 3 ] + "/" + path[ 5 ] );
			return;
		}
		// ./OSGi/apps/<unique_id>/launch/<exec_id>/<property> 
		if ( path.length == 7 && path[ 2 ].equals( PREFIX_APPS ) && path[ 4 ].equals( "launch" ) ) {
		
			checkUniqueID( path );
			
			Hashtable ht = (Hashtable)execIds.get( path[ 3 ] + "/" + path[ 5 ] );
			if( ht == null )
				throw new DmtException(path, DmtException.NODE_NOT_FOUND,
						"Node not found.");				

			ht.remove( path[ 6 ] );
			return;
		}
		
        // should never happen because of meta-data
		throw new DmtException( path, DmtException.METADATA_MISMATCH, "Cannot delete the specified node!" );
	}

	public void renameNode(String[] path, String name) throws DmtException {
		throw new DmtException(path, DmtException.COMMAND_FAILED,
                       		"Node rename not supported!");
	}

	public void setNodeTitle(String[] path, String title) throws DmtException {
		throw new DmtException( path, DmtException.FEATURE_NOT_SUPPORTED,
		                        "Title property not supported!" ); 
	}

	public void setNodeType(String[] arg0, String arg1) throws DmtException {
		// TODO Auto-generated method stub		
	}

	public void setNodeValue(String[] path, DmtData data) throws DmtException {

		if( data == null )
		  throw new DmtException( path, DmtException.METADATA_MISMATCH, 
		                          "The specified node has no default value." );		

		// ./OSGi/apps/<unique_id>/locked 
		if( path.length == 5 && path[ 4 ].equals( "locked" ) ) {
			
			if( data.getFormat() != DmtData.FORMAT_BOOLEAN )
                // should not happen once the format check in implemented in MetaNode.isValidValue
				throw new DmtException( path, DmtException.METADATA_MISMATCH, "Only boolean is supported here!" );
			
			String uid = path [ 3 ];		
			ServiceReference appDescRef = getApplicationDescriptorRef( uid );
			if (appDescRef == null)
				throw new DmtException(path, DmtException.NODE_NOT_FOUND,
						"Node not found.");		
			
			ApplicationDescriptor appDesc = (ApplicationDescriptor) bc.getService( appDescRef );
			
			if( data.getBoolean() )
				appDesc.lock();
			else
				appDesc.unlock();
			
			bc.ungetService( appDescRef );
			return;
		}
		
		// ./OSGi/apps/<unique_id>/launch/<exec_id>/<parameter> 
		if ( path.length == 7 && path[ 2 ].equals( PREFIX_APPS ) && path[ 4 ].equals("launch")  )
		{
			checkUniqueID( path );
			
			Hashtable ht = (Hashtable) execIds.get( path[ 3 ] + "/" + path[ 5 ] );
			if (ht == null)
				throw new DmtException(path, DmtException.COMMAND_FAILED,
						"Parent node does not exist.");
			
			ht.put( path[ 6 ], data );
			return;
		}
		
        // should never happen because of meta-data
		throw new DmtException( path, DmtException.METADATA_MISMATCH, "Cannot change the value of the node!" );
	}

	public void nodeChanged(String[] arg0) throws DmtException {
    // do nothing - the version and timestamp properties are not supported		
	}

	public void close() throws DmtException {
	}

	public String[] getChildNodeNames(String[] path) throws DmtException {
		try {
			// ./OSGi/apps 
			if (path.length == 3 && path[ 2 ].equals( PREFIX_APPS )) {
				return gatherChildren(); 
			}
			// ./OSGi/app_instances 
			if (path.length == 3 && path[ 2 ].equals( PREFIX_APPINST ) ) {
				
				ServiceReference[] hrefs;
				try {
					hrefs = bc.getServiceReferences(
							ApplicationHandle.class.getName(), null);
				}
				catch (InvalidSyntaxException e) {
					throw new RuntimeException("Internal error.");
				}
				
				if (hrefs == null)
					return new String[0];

				String[] ret = new String[ hrefs.length ];
				for (int i = 0; i < hrefs.length; ++i) {
					ret[i] = (String) hrefs[i].getProperty( ApplicationHandle.APPLICATION_PID );
				}
				return ret;
			}
			// ./OSGi/apps/<unique_id> 
			if ( path.length == 4 && path[2].equals( PREFIX_APPS ) ) {
				
				checkUniqueID( path );
				
				return propertyNames;
			}
			// ./OSGi/app_instances/<application.pid> 
			if ( path.length == 4 && path[2].equals( PREFIX_APPINST ) ) {
				
				if( !checkApplicationPid( path[ 3 ] ))
					throw new DmtException(path, DmtException.NODE_NOT_FOUND,
							"Node not found.");
					
				return new String[] {"state", "type"};  
			}
			// ./OSGi/apps/<unique_id>/launch 
			if ( path.length == 5 && path[ 2 ].equals( PREFIX_APPS ) && path[ 4 ].equals( "launch" ) ) {
				
				checkUniqueID( path );
				
				String[] keys = (String[]) execIds.keySet().toArray( new String[0] );
				
				String[] ret = new String[ keys.length ];
				for (int i = 0; i < keys.length; ++i)
					ret[i] = keys[i].substring(keys[i].lastIndexOf("/") + 1,
							keys[i].length());
				
				return ret;
			}
			// ./OSGi/apps/<unique_id>/launch/<exec_id> 
			if ( path.length == 6 && path[ 2 ].equals( PREFIX_APPS ) && path[ 4 ].equals( "launch" ) ) {
				
				checkUniqueID( path );
				
				String key = path[ 3 ] + "/" + path[5];
				
				Hashtable ht = (Hashtable) execIds.get(key);
				if( ht == null )
					throw new DmtException(path, DmtException.NODE_NOT_FOUND,
							"Node not found.");
				
				return (String[]) ht.keySet().toArray(new String[0]);
			}
			else {
				throw new DmtException(path, DmtException.NODE_NOT_FOUND,
						"Node not found.");
			}
		}
		catch (Exception e) {
			throw new DmtException(path, DmtException.COMMAND_FAILED, e
					.getMessage(), e);
		}
	}

	public MetaNode getMetaNode(String[] path) throws DmtException {
		
		if( path.length < 3 )
		    throw new DmtException(path, DmtException.COMMAND_FAILED, "Can not get metadata");
		
		// ./OSGi/apps ./OSGi/app_instances
		if ( path.length == 3 ) 
			return new ApplicationMetaNode(!ApplicationMetaNode.ISLEAF, ApplicationMetaNode.CANGET);
		
		// ./OSGi/app_instances/<application_pid> 
		if ( path.length == 4 && path[ 2 ].equals( PREFIX_APPINST ) ) {       
				return new ApplicationMetaNode(!ApplicationMetaNode.CANDELETE,
						!ApplicationMetaNode.CANADD,
						ApplicationMetaNode.CANGET,
						!ApplicationMetaNode.CANREPLACE,
						ApplicationMetaNode.CANEXECUTE,
						!ApplicationMetaNode.ISLEAF);
		}
		if ( path.length == 5 && path[ 2 ].equals( PREFIX_APPINST ) ) {
				return new ApplicationMetaNode(ApplicationMetaNode.ISLEAF,
							ApplicationMetaNode.CANGET);
		}
		
		if( !path[ 2 ].equals( PREFIX_APPS ) ) {
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Can not get metadata");
		}
		
		if ( path.length == 4 ) // ./OSGi/apps/<unique_id> 
			return new ApplicationMetaNode(!ApplicationMetaNode.ISLEAF, ApplicationMetaNode.CANGET);
		if ( path.length == 5 && path[4].equals("launch")) // ./OSGi/apps/<unique_id>/launch 
			return new ApplicationMetaNode(!ApplicationMetaNode.CANDELETE,
					ApplicationMetaNode.CANADD, ApplicationMetaNode.CANGET,
					!ApplicationMetaNode.CANREPLACE,
					!ApplicationMetaNode.CANEXECUTE,
					!ApplicationMetaNode.ISLEAF);
        if ( path.length == 5 && path[4].equals("locked")) // ./OSGi/apps/<unique_id>/locked 
            return new ApplicationMetaNode(!ApplicationMetaNode.CANDELETE,
                    !ApplicationMetaNode.CANADD, ApplicationMetaNode.CANGET,
                    ApplicationMetaNode.CANREPLACE,
                    !ApplicationMetaNode.CANEXECUTE,
                    ApplicationMetaNode.ISLEAF);
		if ( path.length == 5 ) // ./OSGi/apps/<unique_id>/singleton or ... 
			return new ApplicationMetaNode(ApplicationMetaNode.ISLEAF,
					ApplicationMetaNode.CANGET);
		if ( path.length == 6 && path[4].equals("launch") ) // ./OSGi/apps/<unique_id>/launch/<exec_id> 
			return new ApplicationMetaNode(ApplicationMetaNode.CANDELETE,
					ApplicationMetaNode.CANADD, ApplicationMetaNode.CANGET,
					!ApplicationMetaNode.CANREPLACE,
					ApplicationMetaNode.CANEXECUTE, !ApplicationMetaNode.ISLEAF);
		if ( path.length == 7 && path[4].equals("launch") ) // ./OSGi/apps/<unique_id>/launch/<exec_id>/<parameter> 
			return new ApplicationMetaNode(ApplicationMetaNode.CANDELETE,
					ApplicationMetaNode.CANADD, ApplicationMetaNode.CANGET,
					ApplicationMetaNode.CANREPLACE,
					!ApplicationMetaNode.CANEXECUTE,
					ApplicationMetaNode.ISLEAF);
		throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
	}

	public int getNodeSize(String[] path) throws DmtException {
		return getNodeValue( path ).getSize();
	}

	public Date getNodeTimestamp(String[] path) throws DmtException {
		throw new DmtException( path, DmtException.FEATURE_NOT_SUPPORTED,
                        		"Timestamp property not supported!" ); 
	}

	public String getNodeTitle(String[] path) throws DmtException {
		throw new DmtException( path, DmtException.FEATURE_NOT_SUPPORTED,
                            "Title property not supported!" ); 
	}

	public String getNodeType(String[] arg0) throws DmtException {
		return null;
	}

	public boolean isNodeUri(String[] path) {
		
		try {		
			if ( path.length <= 3 )  // ./OSGi/apps   ./OSGi/app_instances 
				return true;
			ServiceReference appDescRef = null;
		
			// ./OSGi/apps/<unique_id>/... 
			if ( path.length >= 4 && path[ 2 ].equals( PREFIX_APPS )) {
				String uid = path[ 3 ];
				appDescRef = getApplicationDescriptorRef(uid);
				if (appDescRef == null)
					return false;
			}
		
			// ./OSGi/apps/<unique_id> 
			if ( path.length == 4 && path[ 2 ].equals( PREFIX_APPS ) )
				return true;
		
			// ./OSGi/app_instances/<unique_id> 
			if ( path.length == 4 && path[ 2 ].equals( PREFIX_APPINST ) ) {
				return checkApplicationPid( path [ 3 ] );
			}
		
			// ./OSGi/apps/<unique_id>/<property> 
			if ( path.length == 5 && path[ 2 ].equals( PREFIX_APPS ) ) {
				String key = path[ 4 ];
			
				if( Arrays.asList( propertyNames ).indexOf( key ) != -1 )
					return true;
			
				return Arrays.asList(appDescRef.getPropertyKeys()).indexOf(key) != -1;
			}
			// ./OSGi/app_instances/<application_pid>/<property> 
			if ( path.length == 5 && path[ 2 ].equals( PREFIX_APPINST ) ) {
				if ( !checkApplicationPid( path [ 3 ] ) )
					return false;
				if ( path[ 4 ].equals( "state" ) || path[ 4 ].equals( "type" ))
					return true;
				return false;
			}
			// ./OSGi/apps/<unique_id>/launch/<exec_id>/ 
			if ( path.length == 6 && path[ 2 ].equals( PREFIX_APPS ) && path[ 4 ].equals( "launch" )) {
				String key = path[ 3 ] + "/" + path[ 5 ];
			
				checkUniqueID( path );			
				return execIds.get(key) != null;
			}
			// ./OSGi/apps/<unique_id>/launch/<exec_id>/<property> 
			if ( path.length == 7 && path[ 2 ].equals( PREFIX_APPS ) && path[ 4 ].equals( "launch" ) ) {
				String key = path[ 3 ] + "/" + path[ 5 ];
			
				checkUniqueID( path );
				
				Hashtable ht = (Hashtable)execIds.get( key );
				if( ht == null )
					return false;				
				Object data = ht.get( path[ 6 ] );
				return data != null;
			}
			return false;
		}catch( DmtException e ) {
			return false;
		}
	}

	public boolean isLeafNode(String[] path) throws DmtException {
		return getMetaNode( path ).isLeaf();
	}

	public DmtData getNodeValue(String[] path) throws DmtException {
		ServiceReference appDescRef = null;
		
		// ./OSGi/apps/<unique_id>... 
		if ( path.length >= 4 && path[ 2 ].equals( PREFIX_APPS )) { 
			String uid = path[ 3 ];
			appDescRef = getApplicationDescriptorRef(uid);
			if ( appDescRef == null )
				throw new DmtException(path, DmtException.NODE_NOT_FOUND,
						"Node not found.");
		}
		// ./OSGi/apps/<unique_id>/<property> 
		if (path.length == 5 && path[ 2 ].equals( PREFIX_APPS ) ) { 
			String key = path[4];
			
			ApplicationDescriptor appDesc = (ApplicationDescriptor)bc.getService( appDescRef );			
			Map props = appDesc.getProperties( Locale.getDefault().getLanguage() );
			bc.ungetService( appDescRef );
			
			if ( key.equals( "localizedname" ) )
				return new DmtData((String) props.get( ApplicationDescriptor.APPLICATION_NAME ));
			else if ( key.equals( "version" ) )
				return new DmtData((String) props.get( ApplicationDescriptor.APPLICATION_VERSION ));
			else if ( key.equals( "vendor" ) )
				return new DmtData((String) props.get( ApplicationDescriptor.APPLICATION_VENDOR ));
			else if ( key.equals( "locked" ) )
				return new DmtData( ((String)props.get( ApplicationDescriptor.APPLICATION_LOCKED ))
            								.equalsIgnoreCase("true"));
			else if ( key.equals( "bundle_id" ) )
				return new DmtData( (String) props.get( "application.bundle.id" ));
			else {
				Object prop = props.get(key);
				if ( prop == null )
					throw new DmtException(path, DmtException.NODE_NOT_FOUND,
												"Node not found.");
				return new DmtData( (String)prop );
			}
		}
		// ./OSGi/app_instances/<unique_id>/<property> 
		if ( path.length == 5 && path[ 2 ].equals( PREFIX_APPINST ) ) {
			ServiceReference[] hrefs;
			try {
				hrefs = bc.getServiceReferences(ApplicationHandle.class
						.getName(), "(" + ApplicationHandle.APPLICATION_PID + "=" + path[3] + ")");
			}
			catch (InvalidSyntaxException e) {
				throw new RuntimeException("Internal error.");
			}
			if (hrefs == null)
				throw new DmtException(path, DmtException.NODE_NOT_FOUND,
						"Node not found.");
			ApplicationHandle handle = (ApplicationHandle) bc
					.getService(hrefs[0]);
			if (path[4].equals("state")) {
				String state = "INVALID";
				try {
					state = handle.getState();
				}catch( Exception e ) {}
				bc.ungetService(hrefs[0]);
				return new DmtData( state );
			}
			if (path[4].equals("type")) {
				bc.ungetService(hrefs[0]);
				
				return new DmtData( (String)hrefs[ 0 ].getProperty( ApplicationHandle.APPLICATION_DESCRIPTOR ) );
			}

			throw new DmtException(path, DmtException.NODE_NOT_FOUND,
					"Node not found.");
		}
		// ./OSGi/apps/<unique_id>/launch/<exec_id>/<property> 
		if ( path.length == 7 && path[ 2 ].equals( PREFIX_APPS ) && path[ 4 ].equals( "launch" ) ) {
			
			checkUniqueID( path );
			
			String key = path[ 3 ] + "/" + path[ 5 ];
			String param = path[6];
			
			Hashtable ht = (Hashtable) execIds.get(key);
			if( ht == null )
				throw new DmtException(path, DmtException.NODE_NOT_FOUND,
						"Node not found.");
			
			DmtData data = (DmtData) ht.get( param );
			if (data == null)
				throw new DmtException(path, DmtException.NODE_NOT_FOUND,
						"Node not found.");
			else
				return data;
		}
		throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
	}

	public int getNodeVersion(String[] path) throws DmtException {
		throw new DmtException( path, DmtException.FEATURE_NOT_SUPPORTED,
                         		"Version property not supported!" ); 
	}
	
	private ServiceReference getApplicationDescriptorRef(String uid) {
		try {
			ServiceReference[] refs = bc.getServiceReferences(
					ApplicationDescriptor.class.getName(), "(" + Constants.SERVICE_PID + "=" + uid
							+ ")");
			if (null == refs || refs.length < 1)
				return null;
			else
				return refs[0];
		}
		catch (InvalidSyntaxException e) {
			throw new RuntimeException("Internal error.");
		}
	}

	private void checkUniqueID( String path[] ) throws DmtException {
		String uid = path[ 3 ];		
		ServiceReference appDescRef = getApplicationDescriptorRef( uid );
		if (appDescRef == null)
			throw new DmtException(path, DmtException.NODE_NOT_FOUND,
					"Node not found.");		
	}
	
	private boolean checkApplicationPid( String pid ) {
		try {
			ServiceReference[] hrefs = bc.getServiceReferences(
					ApplicationHandle.class.getName(), null);
			if (hrefs == null)
				return false;
			for (int i = 0; i < hrefs.length; ++i) {
				if ( pid.equals((String) hrefs[i].getProperty( ApplicationHandle.APPLICATION_PID ) ) )
					return true;
			}
			return false;
		}catch( Exception e ) {
			return false;			
		}
	}
	
	private String[] gatherChildren() throws Exception {
		ServiceReference[] refs = bc.getServiceReferences(
				ApplicationDescriptor.class.getName(), null);
		
		if (refs == null)
			return new String[0];
		
		String[] ret = new String[refs.length];
		
		for ( int i = 0; i < refs.length; ++i )
			ret[ i ] = (String) refs[i].getProperty( Constants.SERVICE_PID );
		
		return ret;
	}
}
