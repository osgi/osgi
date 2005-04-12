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

/**
 * DMT plugin for the Application Admin
 */
public class ApplicationPlugin implements BundleActivator, DmtDataPlugin,
		DmtExecPlugin {
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
		String[] ifs = new String[] {DmtDataPlugin.class.getName(),
				DmtExecPlugin.class.getName()};
		// unregistered by the OSGi framework
		pluginReg = bc.registerService(ifs, this, dict);
		// start track ApplicationAdmin
	}

	public void stop(BundleContext bc) throws Exception {
		pluginReg.unregister();
		this.bc = null;
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

	public DmtMetaNode getMetaNode( String nodeUri )
			throws DmtException {
		
		String[] path = prepareUri( nodeUri );
		
		if( path.length == 0 )
      throw new DmtException(nodeUri, DmtException.OTHER_ERROR, "Can not get metadata");
		
		/* ./OSGi/apps ./OSGi/app_instances */
		if ( path.length == 1 ) 
			return new ApplicationMetaNode(!ApplicationMetaNode.ISLEAF, ApplicationMetaNode.CANGET);
		
		/* ./OSGi/app_instances/<application_pid> */
		if ( path.length == 2 && path[ 0 ].equals( PREFIX_APPINST ) ) {       
				return new ApplicationMetaNode(!ApplicationMetaNode.CANDELETE,
						!ApplicationMetaNode.CANADD,
						ApplicationMetaNode.CANGET,
						!ApplicationMetaNode.CANREPLACE,
						ApplicationMetaNode.CANEXECUTE,
						!ApplicationMetaNode.ISLEAF);
		}
		if ( path.length == 3 && path[ 0 ].equals( PREFIX_APPINST ) ) {
				return new ApplicationMetaNode(ApplicationMetaNode.ISLEAF,
							ApplicationMetaNode.CANGET);
		}
		
		if( !path[ 0 ].equals( PREFIX_APPS ) ) {
			throw new DmtException(nodeUri, DmtException.OTHER_ERROR, "Can not get metadata");
		}
		
		String uid = path[1];
		ServiceReference sref = getApplicationDescriptorRef(uid);
		if ( sref == null )
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "Node (" + nodeUri + ") not found.");
		if ( path.length == 2 ) /* ./OSGi/apps/<unique_id> */
			return new ApplicationMetaNode(!ApplicationMetaNode.ISLEAF, ApplicationMetaNode.CANGET);
		if ( path.length == 3 && path[2].equals("launch")) /* ./OSGi/apps/<unique_id>/launch */
			return new ApplicationMetaNode(ApplicationMetaNode.CANDELETE,
					ApplicationMetaNode.CANADD, ApplicationMetaNode.CANGET,
					!ApplicationMetaNode.CANREPLACE,
					!ApplicationMetaNode.CANEXECUTE,
					!ApplicationMetaNode.ISLEAF);
		if ( path.length == 3 ) /* ./OSGi/apps/<unique_id>/singleton or ... */
			return new ApplicationMetaNode(ApplicationMetaNode.ISLEAF,
					ApplicationMetaNode.CANGET);
		if ( path.length == 4 && path[2].equals("launch") ) /* ./OSGi/apps/<unique_id>/launch/<exec_id> */
			return new ApplicationMetaNode(ApplicationMetaNode.CANDELETE,
					ApplicationMetaNode.CANADD, ApplicationMetaNode.CANGET,
					!ApplicationMetaNode.CANREPLACE,
					ApplicationMetaNode.CANEXECUTE, !ApplicationMetaNode.ISLEAF);
		if ( path.length == 5 && path[2].equals("launch") ) /* ./OSGi/apps/<unique_id>/launch/<exec_id>/<parameter> */
			return new ApplicationMetaNode(ApplicationMetaNode.ISLEAF,
					ApplicationMetaNode.CANGET);
		throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "Node ("
				+ nodeUri + ") not found.");
	}

	public boolean isLeafNode( String nodeUri ) throws DmtException {
		return getMetaNode( nodeUri ).isLeaf();
	}

	public void createInteriorNode(String nodeUri) throws DmtException {
		
		String[] path = prepareUri( nodeUri );

		/* ./OSGi/apps/<unique_id>/launch/<exec_id> */
		if ( path.length != 4 || !path[ 0 ].equals( PREFIX_APPS ) || !path[ 2 ].equals("launch") )
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Operation is not allowed.");
		
		checkUniqueID( nodeUri, path[ 1 ]);
		
		execIds.put( path[ 1 ] + "/" + path[ 3 ], new Hashtable());
	}

	public void createInteriorNode(String nodeUri, String type)
			throws DmtException {
		
		throw new DmtException( nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
														"Cannot set type property of application nodes!" );
	}

	public void createLeafNode(String nodeUri, DmtData value)
			throws DmtException {
		
		String[] path = prepareUri( nodeUri );
		
		/* ./OSGi/apps/<unique_id>/launch/<exec_id>/<parameter> */
		if ( path.length != 5 || !path[ 0 ].equals( PREFIX_APPS ) || !path[ 2 ].equals("launch")  )
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Operation is not allowed.");
		
		checkUniqueID( nodeUri, path[ 1 ]);
				
		String key = path[ 1 ] + "/" + path[ 3 ];
		Hashtable ht = (Hashtable) execIds.get( key );
		if (ht == null)
			throw new DmtException(nodeUri, DmtException.COMMAND_FAILED,
					"Parent node " + path[ 3 ] + " does not exist.");
		ht.put( path[ 4 ], value );
	}

	public boolean isNodeUri(String nodeUri) {
		
		try {
		
			String[] path = prepareUri( nodeUri );
		
			if ( path.length == 1 )  /* ./OSGi/apps   ./OSGi/app_instances */
				return true;
			ServiceReference appDescRef = null;
		
			/* ./OSGi/apps/<unique_id>/... */
			if ( path.length >= 2 && path[ 0 ].equals( PREFIX_APPS )) {
				String uid = path[ 1 ];
				appDescRef = getApplicationDescriptorRef(uid);
				if (appDescRef == null)
					return false;
			}
		
			/* ./OSGi/apps/<unique_id> */
			if ( path.length == 2 && path[ 0 ].equals( PREFIX_APPS ) )
				return true;
		
			/* ./OSGi/app_instances/<unique_id> */
			if ( path.length == 2 && path[ 0 ].equals( PREFIX_APPINST ) ) {
				return checkApplicationPid( path [ 1 ] );
			}
		
			/* ./OSGi/apps/<unique_id>/<property> */
			if ( path.length == 3 && path[ 0 ].equals( PREFIX_APPS ) ) {
				String key = path[ 2 ];
			
				if( Arrays.asList( propertyNames ).indexOf( key ) != -1 )
					return true;
			
				return Arrays.asList(appDescRef.getPropertyKeys()).indexOf(key) != -1;
			}
			/* ./OSGi/app_instances/<application_pid>/<property> */
			if ( path.length == 3 && path[ 0 ].equals( PREFIX_APPINST ) ) {
				if ( !checkApplicationPid( path [ 1 ] ) )
					return false;
				if ( path[ 2 ].equals( "state" ) || path[ 2 ].equals( "type" ))
					return true;
				return false;
			}
			/* ./OSGi/apps/<unique_id>/launch/<exec_id>/ */
			if ( path.length == 4 && path[ 0 ].equals( PREFIX_APPS ) && path[ 2 ].equals( "launch" )) {
				String key = path[ 1 ] + "/" + path[ 3 ];
			
				checkUniqueID( nodeUri, path[ 1 ]);			
				return execIds.get(key) != null;
			}
			/* ./OSGi/apps/<unique_id>/launch/<exec_id>/<property> */
			if ( path.length == 5 && path[ 0 ].equals( PREFIX_APPS ) && path[ 2 ].equals( "launch" ) ) {
				String key = path[ 1 ] + "/" + path[ 3 ];
			
				checkUniqueID( nodeUri, path[ 1 ]);
				
				Hashtable ht = (Hashtable)execIds.get( key );
				if( ht == null )
					return false;				
				Object data = ht.get( path[ 4 ] );
				return data != null;
			}
			return false;
		}catch( DmtException e ) {
			return false;
		}
	}

	public DmtData getNodeValue(String nodeUri) throws DmtException {
		String[] path = prepareUri( nodeUri );
		
		ServiceReference appDescRef = null;
		
		/* ./OSGi/apps/<unique_id>... */
		if ( path.length >= 2 && path[ 0 ].equals( PREFIX_APPS )) { 
			String uid = path[ 1 ];
			appDescRef = getApplicationDescriptorRef(uid);
			if ( appDescRef == null )
				throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
						"Node (" + nodeUri + ") not found.");
		}
		/* ./OSGi/apps/<unique_id>/<property> */
		if (path.length == 3 && path[ 0 ].equals( PREFIX_APPS ) ) { 
			String key = path[2];
			
			ApplicationDescriptor appDesc = (ApplicationDescriptor)bc.getService( appDescRef );			
			Map props = appDesc.getProperties( Locale.getDefault().getLanguage() );
			bc.ungetService( appDescRef );
			
			if ( key.equals( "localizedname" ) )
				return new DmtData((String) props.get( ApplicationDescriptor.APPLICATION_NAME ));
			else if ( key.equals( "version" ) )
				return new DmtData((String) props.get( ApplicationDescriptor.APPLICATION_VERSION ));
			else if ( key.equals( "vendor" ) )
				return new DmtData((String) props.get( ApplicationDescriptor.APPLICATION_VENDOR ));
			else if ( key.equals( "autostart" ) )
				return new DmtData( ((String)props.get( ApplicationDescriptor.APPLICATION_AUTOSTART ))
						                .equalsIgnoreCase("true"));
			else if ( key.equals( "locked" ) )
				return new DmtData( ((String)props.get( ApplicationDescriptor.APPLICATION_LOCKED ))
            								.equalsIgnoreCase("true"));
			else if ( key.equals( "singleton" ) )
				return new DmtData( ((String)props.get( ApplicationDescriptor.APPLICATION_SINGLETON ))
            								.equalsIgnoreCase("true"));
			else if ( key.equals( "bundle_id" ) )
				return new DmtData( (String) props.get( "application.bundle.id" ));
			else {
				Object prop = props.get(key);
				if ( prop == null )
					throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
												"Node (" + nodeUri + ") not found.");
				return new DmtData( (String)prop );
			}
		}
		/* ./OSGi/app_instances/<unique_id>/<property> */
		if ( path.length == 3 && path[ 0 ].equals( PREFIX_APPINST ) ) {
			ServiceReference[] hrefs;
			try {
				hrefs = bc.getServiceReferences(ApplicationHandle.class
						.getName(), "(" + ApplicationHandle.APPLICATION_PID + "=" + path[1] + ")");
			}
			catch (InvalidSyntaxException e) {
				throw new RuntimeException("Internal error.");
			}
			if (hrefs == null)
				throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
						"Node (" + nodeUri + ") not found.");
			ApplicationHandle handle = (ApplicationHandle) bc
					.getService(hrefs[0]);
			if (path[2].equals("state")) {
				String state = "INVALID";
				try {
					state = handle.getState();
				}catch( Exception e ) {}
				bc.ungetService(hrefs[0]);
				return new DmtData( state );
			}
			if (path[2].equals("type")) {
				bc.ungetService(hrefs[0]);
				return new DmtData(handle.getApplicationDescriptor().getPID());
			}

			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"Node (" + nodeUri + ") not found.");
		}
		/* ./OSGi/apps/<unique_id>/launch/<exec_id>/<property> */
		if ( path.length == 5 && path[ 0 ].equals( PREFIX_APPS ) && path[ 2 ].equals( "launch" ) ) {
			
			checkUniqueID( nodeUri, path[ 1 ] );
			
			String key = path[ 1 ] + "/" + path[ 3 ];
			String param = path[4];
			
			Hashtable ht = (Hashtable) execIds.get(key);
			if( ht == null )
				throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
						"Node (" + nodeUri + ") not found.");
			
			DmtData data = (DmtData) ht.get( param );
			if (data == null)
				throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
						"Node (" + nodeUri + ") not found.");
			else
				return data;
		}
		throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "Node ("
				+ nodeUri + ") not found.");
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
		String[] path = prepareUri( nodeUri );

		try {
			/* ./OSGi/apps */
			if (path.length == 1 && path[ 0 ].equals( PREFIX_APPS )) {
				return gatherChildren(); 
			}
			/* ./OSGi/app_instances */
			if (path.length == 1 && path[ 0 ].equals( PREFIX_APPINST ) ) {
				
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
			/* ./OSGi/apps/<unique_id> */
			if ( path.length == 2 && path[0].equals( PREFIX_APPS ) ) {
				
				checkUniqueID( nodeUri, path[ 1 ] );
				
				String uid = path[1];
				return propertyNames;
			}
			/* ./OSGi/app_instances/<application.pid> */
			if ( path.length == 2 && path[0].equals( PREFIX_APPINST ) ) {
				
				if( !checkApplicationPid( path[ 1 ] ))
					throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
							"Node (" + nodeUri + ") not found.");
					
				return new String[] {"state", "type"};  
			}
			/* ./OSGi/apps/<unique_id>/launch */
			if ( path.length == 3 && path[ 0 ].equals( PREFIX_APPS ) && path[ 2 ].equals( "launch" ) ) {
				
				checkUniqueID( nodeUri, path[ 1 ] );
				
				String[] keys = (String[]) execIds.keySet().toArray( new String[0] );
				
				String[] ret = new String[ keys.length ];
				for (int i = 0; i < keys.length; ++i)
					ret[i] = keys[i].substring(keys[i].lastIndexOf("/") + 1,
							keys[i].length());
				
				return ret;
			}
			/* ./OSGi/apps/<unique_id>/launch/<exec_id> */
			if ( path.length == 4 && path[ 0 ].equals( PREFIX_APPS ) && path[ 2 ].equals( "launch" ) ) {
				
				checkUniqueID( nodeUri, path[ 1 ] );
				
				String param = path[ 3 ];
				String key = path[ 1 ] + "/" + path[3];
				
				Hashtable ht = (Hashtable) execIds.get(key);
				if( ht == null )
					throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
							"Node (" + nodeUri + ") not found.");
				
				return (String[]) ht.keySet().toArray(new String[0]);
			}
			else {
				throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
						"Node (" + nodeUri + ") not found.");
			}
		}
		catch (Exception e) {
			throw new DmtException(nodeUri, DmtException.OTHER_ERROR, e
					.getMessage(), e);
		}
	}

  public boolean supportsAtomic()
  {
  	return false;
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
	
	public void execute(DmtSession session, String nodeUri, String data)
			throws DmtException {

		String[] path = prepareUri( nodeUri );

		/* ./OSGi/apps/<unique_id> */
		if( path.length == 2 && path[ 0 ].equals( PREFIX_APPINST )) {
			
			try 
			{
				ServiceReference[] hrefs = bc.getServiceReferences(
						ApplicationHandle.class.getName(), "(" + ApplicationHandle.APPLICATION_PID + "=" + path[ 1 ] + ")");
			
				if (hrefs == null)
					throw new DmtException(nodeUri,
							DmtException.NODE_NOT_FOUND, "Node (" + nodeUri + ") not found.");
				
				ApplicationHandle handle = (ApplicationHandle) bc.getService( hrefs[ 0 ] );
				if ( data.equalsIgnoreCase( "STOP" ))
					handle.destroy();
				bc.ungetService( hrefs[ 0 ] );
				return;
			}catch( Exception e ) {
				throw new DmtException(nodeUri, DmtException.OTHER_ERROR,
						"Execution of " + nodeUri + " is failed.");
			}
		}
		
		/* ./OSGi/apps/<unique_id>/launch/<exec_id> */
		if ( path.length != 4 || !path[ 0 ].equals( PREFIX_APPS ))
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Execution of " + nodeUri + " is not allowed.");
		
		ServiceReference appDescRef = getApplicationDescriptorRef( path[ 1 ] );
		if (appDescRef == null)
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"Node (" + nodeUri + ") not found.");		
		
		String key = path[ 1 ] + "/" + path[ 3 ];
		
		Hashtable dmtArgs = (Hashtable) execIds.get(key);
		if ( dmtArgs == null )
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"Node (" + nodeUri + ") not found.");
		
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
			throw new DmtException(nodeUri, DmtException.OTHER_ERROR, e.getMessage(), e);
		}
		bc.ungetService( appDescRef );
	}

	private static String[] prepareUri(String nodeUri) {
		if( !nodeUri.startsWith( URI_ROOT_APP ) && !nodeUri.startsWith( URI_ROOT_APPINST ))
			return new String[] {};
		
		if( nodeUri.endsWith( "/" ))
			nodeUri = nodeUri.substring( 0, nodeUri.length() -1 );
		
		nodeUri = nodeUri.substring( (URI_ROOT_OSGI + "/").length() );
				
		// relativeUri will not be null because the DmtAdmin only gives us nodes
		// in our subtree
		String[] path = Splitter.split(nodeUri, '/', -1);
		if (path.length == 1 && path[0].equals(""))
			return new String[] {};
		return path;
	}
	
	private void checkUniqueID( String nodeUri, String pid ) throws DmtException {
		String uid = pid;		
		ServiceReference appDescRef = getApplicationDescriptorRef( uid );
		if (appDescRef == null)
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"Node (" + nodeUri + ") not found.");		
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
		
  public void open(String subtreeUri, int lockMode, DmtSession session) throws DmtException {
	}

	public void close() throws DmtException {
	}

	public String getNodeTitle(String nodeUri) throws DmtException {
		throw new DmtException( nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				                    "Title property not supported!" ); 
	}

	public void setNodeTitle(String nodeUri, String title) throws DmtException {
		throw new DmtException( nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
														"Title property not supported!" ); 
	}

	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		throw new DmtException( nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
														"Timestamp property not supported!" ); 
	}

	public void rollback() throws DmtException {
	}

	public void commit() throws DmtException {
	}
	
	public int getNodeVersion(String nodeUri) throws DmtException {
		throw new DmtException( nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
														"Version property not supported!" ); 
	}

	public void setDefaultNodeValue(String nodeUri) throws DmtException {
		throw new DmtException( nodeUri, DmtException.METADATA_MISMATCH, 
		"The specified node has no default value." );		
	}

	public void createLeafNode(String nodeUri) throws DmtException {
		throw new DmtException( nodeUri, DmtException.METADATA_MISMATCH, 
														"The specified node has no default value." );		
	}

	public int getNodeSize(String nodeUri) throws DmtException {
		DmtData nodeValue = getNodeValue( nodeUri );
		
		switch( nodeValue.getFormat() )
		{
		case DmtData.FORMAT_BINARY:
			return nodeValue.getBinary().length;
		case DmtData.FORMAT_BOOLEAN:
			return 1;
		case DmtData.FORMAT_INTEGER:
			return 4;
		case DmtData.FORMAT_STRING:
		case DmtData.FORMAT_XML:
			return nodeValue.getString().length();
		default:
			return 0;
		}
	}
	
	public String getNodeType(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
		// TODO Auto-generated method stub		
	}

	public void setNodeType(String nodeUri, String type) throws DmtException {
		// TODO Auto-generated method stub		
	}

	public void deleteNode(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub		
	}

	public void createLeafNode(String nodeUri, DmtData value, String mimeType) throws DmtException {
		// TODO Auto-generated method stub		
	}

	public void copy(String nodeUri, String newNodeUri, boolean recursive) throws DmtException {
		// TODO Auto-generated method stub		
	}

	public void renameNode(String nodeUri, String newName) throws DmtException {
		// TODO Auto-generated method stub		
	}	
}
