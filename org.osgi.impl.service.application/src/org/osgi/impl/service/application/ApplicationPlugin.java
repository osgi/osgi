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

	public void open(String subtreeUri, DmtSession session)
			throws DmtException {
		// TODO Auto-generated method stub
	}

	public DmtMetaNode getMetaNode( String nodeUri )
			throws DmtException {
		
		String[] path = prepareUri( nodeUri );
		
		if( path.length == 0 )
      throw new DmtException(nodeUri, DmtException.OTHER_ERROR, "Can not get metadata");
		
		if ( path.length == 1 ) /* ./OSGi/apps ./OSGi/app_instances */
			return new ApplicationMetaNode(!ApplicationMetaNode.ISLEAF, ApplicationMetaNode.CANGET);
		
		if ( isAppInstUri( path ) ) { 
			if ( path.length == 2 ) {       /* ./OSGi/app_instances/<service_pid> */
				return new ApplicationMetaNode(!ApplicationMetaNode.CANDELETE,
						!ApplicationMetaNode.CANADD,
						ApplicationMetaNode.CANGET,
						!ApplicationMetaNode.CANREPLACE,
						ApplicationMetaNode.CANEXECUTE,
						!ApplicationMetaNode.ISLEAF);
			}
			else /* ./OSGi/app_instances/<service_pid>/type or state */
				if ( path.length == 3 ) {
					return new ApplicationMetaNode(ApplicationMetaNode.ISLEAF,
							ApplicationMetaNode.CANGET);
				}
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
	
	/* ------------------------------------------------------------------------------ */
	/* ------------------------------------------------------------------------------ */
	/* -------------------------------- UNCHECKED FROM HERE ------------------------- */
	/* ------------------------------------------------------------------------------ */
	/* ------------------------------------------------------------------------------ */
	
	public void createInteriorNode(String nodeUri) throws DmtException {
		String[] sarr = Splitter.split(nodeUri, '/', 0);
		if (6 != sarr.length)
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Operation is not allowed.");
		String uid = sarr[3];
		ServiceReference sref = getApplicationDescriptorRef(uid);
		if (null == sref)
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"Node (" + nodeUri + ") not found.");
		String key = new String(sarr[0] + "/" + sarr[1] + "/" + sarr[2] + "/"
				+ sarr[3] + "/" + sarr[4] + "/" + sarr[5]);
		execIds.put(key, new Hashtable());
	}

	public void createInteriorNode(String nodeUri, String type)
			throws DmtException {
		// TODO Auto-generated method stub
	}

	public void createLeafNode(String nodeUri, DmtData value)
			throws DmtException {
		String[] sarr = Splitter.split(nodeUri, '/', 0);
		if (7 != sarr.length)
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Operation is not allowed.");
		String execId = sarr[5];
		String key = new String(sarr[0] + "/" + sarr[1] + "/" + sarr[2] + "/"
				+ sarr[3] + "/" + sarr[4] + "/" + sarr[5]);
		Hashtable ht = (Hashtable) execIds.get(key);
		if (null == ht)
			throw new DmtException(nodeUri, DmtException.COMMAND_FAILED,
					"Parent node " + execId + " does not exist.");
		ht.put(sarr[6], value);
	}

	public void close() throws DmtException {
		// TODO Auto-generated method stub
	}

	public boolean isNodeUri(String nodeUri) {
		String[] path = prepareUri( nodeUri );
		
		if ( path.length == 1 )  /* ./OSGi/apps   ./OSGi/app_instances */
			return true;
		ServiceReference appDescRef = null;
		if ( path.length >= 2 && path[ 0 ].equals( PREFIX_APPS )) {
			String uid = path[ 1 ];
			appDescRef = getApplicationDescriptorRef(uid);
			if (appDescRef == null)
				return false;
		}
		if ( path.length == 2 && path[ 0 ].equals( PREFIX_APPS ) )
			return true;
		if ( path.length == 2 && path[ 0 ].equals( PREFIX_APPINST ) ) {
			try {
				ServiceReference[] hrefs = bc.getServiceReferences(
						ApplicationHandle.class.getName(), null);
				if (hrefs == null)
					return false;
				for (int i = 0; i < hrefs.length; ++i) {
					if ( path[ 1 ].equals((String) hrefs[i].getProperty("application.pid") ) )

						return true;
				}
				return false;
			}
			catch (InvalidSyntaxException e) {
				throw new RuntimeException("Internal error.");
			}
		}
		if ( path.length == 3 && path[ 0 ].equals( PREFIX_APPS ) ) {
			String key = path[ 2 ];
			
			if( Arrays.asList( propertyNames ).indexOf( key ) != -1 )
				return true;
			
			return Arrays.asList(appDescRef.getPropertyKeys()).indexOf(key) != -1;
		}
		if ( path.length == 3 && path[ 0 ].equals( PREFIX_APPINST ) ) {
			if ( path[ 2 ].equals( "state" ) || path[ 2 ].equals( "type" ))
				return true;
			return false;
		}
		if ( path.length == 4 && path[ 0 ].equals( PREFIX_APPS ) ) {
			String key = new String( path[0] + "/" + path[1] + "/" + path[2]
					+ "/" + path[3]);
			return execIds.get(key) != null;
		}
		if ( path.length != 5 && path[ 0 ].equals( PREFIX_APPS ) ) {
			String key = new String(path[0] + "/" + path[1] + "/" + path[2] + "/" + path[3]);
			String param = path[ 4 ];
			Object data = ((Hashtable) execIds.get(key)).get(param);
			return data != null;
		}
		return false;
	}

	public DmtData getNodeValue(String nodeUri) throws DmtException {
		String[] path = prepareUri( nodeUri );
		
		ServiceReference appDescRef = null;
		
		if ( path.length >= 2 && path[ 0 ].equals( PREFIX_APPS )) { /* ./OSGi/apps/<unique_id>... */
			String uid = path[ 1 ];
			appDescRef = getApplicationDescriptorRef(uid);
			if ( appDescRef == null )
				throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
						"Node (" + nodeUri + ") not found.");
		}
		if (path.length == 3 && path[ 0 ].equals( PREFIX_APPS ) ) { /* ./OSGi/apps/<unique_id>/<property> */
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
				return new DmtData( Boolean.getBoolean(((String) props
											.get( ApplicationDescriptor.APPLICATION_AUTOSTART ))));
			else if ( key.equals( "locked" ) )
				return new DmtData( Boolean.getBoolean(((String) props
											.get( ApplicationDescriptor.APPLICATION_LOCKED ))));
			else if ( key.equals( "singleton" ) )
				return new DmtData( Boolean.getBoolean(((String) props
											.get( ApplicationDescriptor.APPLICATION_SINGLETON ))));
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
		if ( path.length == 3 && path[ 0 ].equals( PREFIX_APPINST ) ) {
			ServiceReference[] hrefs;
			try {
				hrefs = bc.getServiceReferences(ApplicationHandle.class
						.getName(), "(application.pid=" + path[1] + ")");
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
				int state = -1;
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
		if ( path.length == 5 && path[ 0 ].equals( PREFIX_APPS ) && path[ 3 ].equals( "launch" ) ) {
			String key = new String(path[0] + "/" + path[1] + "/" + path[2] + "/" + path[3] );
			String param = path[4];
			DmtData data = (DmtData) ((Hashtable) execIds.get(key)).get(param);
			if (data == null)
				throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
						"Node (" + nodeUri + ") not found.");
			else
				return data;
		}
		throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "Node ("
				+ nodeUri + ") not found.");
	}

	public String getNodeTitle(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNodeType(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNodeVersion(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNodeSize(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
		String[] path = prepareUri( nodeUri );

		try {
			if (path.length == 1 && path[0].equals( PREFIX_APPS )) {   /* ./OSGi/apps */
				return gatherChildren(); 
			}
			if (path.length == 1 && path[0].equals( PREFIX_APPINST ) ) { /* ./OSGi/app_instances */
				
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

				String[] ret = new String[hrefs.length];
				for (int i = 0; i < hrefs.length; ++i) {
					ret[i] = (String) hrefs[i].getProperty( "application.pid" );
				}
				return ret;
			}
			if ( path.length == 2 && path[0].equals( PREFIX_APPS ) ) { /* ./OSGi/apps/<unique_id> */
				String uid = path[1];
				return propertyNames;
			}
			if ( path.length == 2 && path[0].equals( PREFIX_APPINST ) ) {
				return new String[] {"state", "type"};  /* ./OSGi/app_instances/<service.pid> */
			}
			if ( path.length == 3 && path[0].equals( PREFIX_APPS ) ) {
				String[] keys = (String[]) execIds.keySet().toArray(
						new String[0]);
				String[] ret = new String[keys.length];
				for (int i = 0; i < keys.length; ++i)
					ret[i] = keys[i].substring(keys[i].lastIndexOf("/") + 1,
							keys[i].length());
				return ret;
			}
			if ( path.length == 4 ) {
				String param = path[3];
				String key = new String(path[0] + "/" + path[1] + "/" + path[2]
						+ "/" + path[3]);
				return (String[]) ((Hashtable) execIds.get(key)).keySet()
						.toArray(new String[0]);
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
	
  public void open(String subtreeUri, int lockMode, DmtSession session) throws DmtException
	{
		// TODO Auto-generated method stub
	}

  public boolean supportsAtomic()
  {
  	return false;
  }

	private String getUidFromUri(String nodeUri) {
		String uid = null;
		int s = nodeUri.indexOf('/', URI_ROOT_APP.length());
		if (-1 == s)
			return uid;
		int ss = nodeUri.indexOf('/', s + 1);
		if (-1 == ss) {
			uid = nodeUri.substring(URI_ROOT_APP.length() + 1);
			if ("".equals(uid))
				return null;
		}
		else {
			uid = nodeUri.substring(s + 1, ss);
		}
		return uid;
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

  public void clone(String nodeUri, String newNodeUri, boolean recursive)
			throws DmtException {
		// TODO Auto-generated method stub
	}

	public void execute(DmtSession session, String nodeUri, String data)
			throws DmtException {
		String[] sarr = Splitter.split(nodeUri, '/', 0);
		if ("application_instances".equals(sarr[2])) {
			try {
				ServiceReference[] hrefs = bc.getServiceReferences(
						ApplicationHandle.class.getName(), "(service.id="
								+ sarr[3] + ")");
				if (null == hrefs)
					throw new DmtException(nodeUri,
							DmtException.NODE_NOT_FOUND, "Node (" + nodeUri
									+ ") not found.");
				ApplicationHandle handle = (ApplicationHandle) bc
						.getService(hrefs[0]);
				if ("STOP".equalsIgnoreCase(data))
					handle.destroy();
				bc.ungetService(hrefs[0]);
			}
			catch (Exception e) {
				throw new RuntimeException("Internal error.");
			}
		}
		if (6 != sarr.length
				&& !(4 == sarr.length && "application_instances"
						.equals(sarr[2])))
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Execution of " + nodeUri + " is not allowed.");
		if (6 == sarr.length) {
			String uid = sarr[3];
			ServiceReference sref = getApplicationDescriptorRef(uid);
			if (null == sref)
				throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
						"Node (" + nodeUri + ") not found.");
			String key = new String(sarr[0] + "/" + sarr[1] + "/" + sarr[2]
					+ "/" + sarr[3] + "/" + sarr[4] + "/" + sarr[5]);
			Hashtable args = (Hashtable) execIds.get(key);
			if (null == args)
				throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
						"Node (" + nodeUri + ") not found.");
			ApplicationDescriptor descr = (ApplicationDescriptor) bc
					.getService(sref);
						
			try {
				descr.launch( args );
			}
			catch (Exception e) {
				throw new DmtException(nodeUri, DmtException.OTHER_ERROR, e
						.getMessage());
			}
			bc.ungetService(sref);
		}
	}

	private boolean isAppInstUri( String[] uri ) {
		return ( uri.length > 0 && uri[0].equals( PREFIX_APPINST ) );
	}
	
	private static String[] prepareUri(String nodeUri) {
		if( !nodeUri.startsWith( URI_ROOT_APP ) && !nodeUri.startsWith( URI_ROOT_APPINST ))
			return new String[] {};
		
		nodeUri = nodeUri.substring( (URI_ROOT_OSGI + "/").length() );
				
		// relativeUri will not be null because the DmtAdmin only gives us nodes
		// in our subtree
		String[] path = Splitter.split(nodeUri, '/', -1);
		if (path.length == 1 && path[0].equals(""))
			return new String[] {};
		return path;
	}

	public void rollback() throws DmtException {
		// TODO Auto-generated method stub		
	}

	public void commit() throws DmtException {
		// TODO Auto-generated method stub		
	}

	public void setNodeTitle(String nodeUri, String title) throws DmtException {
		// TODO Auto-generated method stub		
	}

	public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
		// TODO Auto-generated method stub		
	}

	public void setDefaultNodeValue(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub		
	}

	public void setNodeType(String nodeUri, String type) throws DmtException {
		// TODO Auto-generated method stub		
	}

	public void deleteNode(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub		
	}

	public void createLeafNode(String nodeUri) throws DmtException {
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
