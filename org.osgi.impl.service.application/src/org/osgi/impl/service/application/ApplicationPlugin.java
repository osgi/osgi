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

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.*;
import org.osgi.service.application.*;
import org.osgi.service.dmt.*;
import org.osgi.service.dmt.spi.*;
import org.osgi.service.log.LogService;

class LockerNode extends ApplicationPluginBaseNode {
	private boolean isLock;
	
	LockerNode( String name, boolean isLock ) {
		super( name );
		this.isLock = isLock;
		isLeaf = canExecute = true;
		canGet = false;
	}
	
	public void execute(DmtSession session, String path[], String correlator, String data) throws DmtException {
		ServiceReference ref = ApplicationPlugin.getApplicationDescriptor( path );
		if( ref == null )
			throw new DmtException(path, DmtException.METADATA_MISMATCH, "Cannot execute the node!" );
		ApplicationDescriptor appDesc = (ApplicationDescriptor)ApplicationPlugin.bc.getService( ref );
		
		if( isLock )
			appDesc.lock();
		else
			appDesc.unlock();
		
		ApplicationPlugin.bc.ungetService( ref );
	}
}

class OperationsNode extends ApplicationPluginBaseNode {
	OperationsNode() {
		super( "Operations" );

		/* TODO */
		
		addChildNode( new LockerNode("Unlock",false) );
		addChildNode( new LockerNode("Lock",true) );
	}
}

class InstanceOperationsStopNode extends ApplicationPluginBaseNode {
	InstanceOperationsStopNode() {
		super( "Stop" );
		
		isLeaf = canExecute = true;
		canGet = false;
	}
	
	public void execute(DmtSession session, String path[], String correlator, String data) throws DmtException {
		ServiceReference appHandle = ApplicationPlugin.getApplicationHandle( path );
		if( appHandle == null )
			throw new DmtException(path, DmtException.COMMAND_FAILED, "Cannot execute the node.");
		
		final ApplicationHandle appHnd = (ApplicationHandle)ApplicationPlugin.bc.getService( appHandle );

		class DestroyerThread extends Thread {
			public void run() {
        
				try {
					if( appHnd != null )
						appHnd.destroy();
				}catch( Exception e ) {
				}          
			};
		}
		
		DestroyerThread destroyerThread = new DestroyerThread();
		destroyerThread.start();

		try {
			destroyerThread.join( 5000 );
		}catch(InterruptedException e) {}
		
		if( destroyerThread.isAlive() )
			Activator.log( LogService.LOG_ERROR, "Stop method of the application didn't finish at 5s!", null );				
	}
}

class InstanceOperationsNode extends ApplicationPluginBaseNode {
	InstanceOperationsNode() {
		super( "Operations" );
		
		addChildNode( new ApplicationPluginBaseNode("Ext") );
		addChildNode( new InstanceOperationsStopNode() );
	}
}

class InstanceStateNode extends ApplicationPluginBaseNode {
	InstanceStateNode() {
		super( "State", "text/plain" );
	}
	
	public DmtData getNodeValue( String path[] ) throws DmtException {
		ServiceReference appHndRef = ApplicationPlugin.getApplicationHandle( path );
	  if( appHndRef == null )
	  	throw new DmtException(path, DmtException.METADATA_MISMATCH, "Cannot get node value!" );
		  
	  String state = (String)appHndRef.getProperty( ApplicationHandle.APPLICATION_STATE );
	  if( state == null )
	  	throw new DmtException(path, DmtException.METADATA_MISMATCH, "Cannot get node value!" );
		  	
	  return new DmtData( state );
	}
}

class InstanceIDNode extends ApplicationPluginBaseNode {
	InstanceIDNode() {
		super();
		
		addChildNode( new InstanceOperationsNode() );
		addChildNode( new InstanceStateNode() );
	}

	public String[]  getNames( String []path ) {
		
		ServiceReference[] refs = null;
		
		try {
		  refs = ApplicationPlugin.bc.getServiceReferences( ApplicationHandle.class.getName(), 
                         "(" + ApplicationHandle.APPLICATION_DESCRIPTOR + "=" + path[ 3 ] + ")" );
		}catch( InvalidSyntaxException e) {}
		
		if( refs == null || refs.length == 0 )
			return new String [ 0 ];
		
		String result[] = new String[ refs.length ];
		for( int i=0; i != refs.length; i++ )
			result[ i ] = (String)refs[ i ].getProperty( Constants.SERVICE_PID );
		
		return result;
	}
}

class InstancesNode extends ApplicationPluginBaseNode {
	InstancesNode() {
		super("Instances");
		
		addChildNode( new InstanceIDNode() );
	}
}

class ApplicationPropertyNode extends ApplicationPluginBaseNode {
	private String  name;
	private String  propertyName;
	private boolean isBoolean;
	
	ApplicationPropertyNode( String name, String propertyName ) {
		super( name, "text/plain" );
		
		this.name = name;
		this.propertyName = propertyName;
		this.isBoolean = false;
	}
	
	ApplicationPropertyNode( String name, String propertyName, boolean isBoolean ) {
		super( name, "text/plain" );
		
		this.name = name;
		this.propertyName = propertyName;
		this.isBoolean = isBoolean;
	}
	
	public String [] getNames( String path[] ) {
		try {
		  if( getProperty( path ) != null )
			  return new String [] { name }; // if the property is missing, the variable is hidden
		  else
			  return new String [ 0 ];
		}catch( Exception e ) {
		  return new String [ 0 ];			
		}
	}
	
	public DmtData getNodeValue( String path[] ) throws DmtException {
		if( !isBoolean )
		  return new DmtData( getProperty( path ) );
		else
			return new DmtData( Boolean.valueOf( getProperty( path ) ).booleanValue() );
	}
	
	String getProperty( String []path )  throws DmtException {
		ServiceReference ref = ApplicationPlugin.getApplicationDescriptor( path );
		if( ref == null )
			throw new DmtException(path, DmtException.METADATA_MISMATCH, "Cannot get node value!" );
		return (String)ref.getProperty( propertyName );
	}
}

class ApplicationIDNode extends ApplicationPluginBaseNode {
	ApplicationIDNode() {
		super();
		
		/* TODO */
		
		addChildNode( new OperationsNode() );
		
		addChildNode( new InstancesNode() );
		
		addChildNode( new ApplicationPropertyNode( "Name",        ApplicationDescriptor.APPLICATION_NAME ) );
		addChildNode( new ApplicationPropertyNode( "IconURI",     ApplicationDescriptor.APPLICATION_ICON ) );
		addChildNode( new ApplicationPropertyNode( "Version",     ApplicationDescriptor.APPLICATION_VERSION ) );
		addChildNode( new ApplicationPropertyNode( "Vendor",      ApplicationDescriptor.APPLICATION_VENDOR ) );
		addChildNode( new ApplicationPropertyNode( "Locked",      ApplicationDescriptor.APPLICATION_LOCKED, true ) );
		addChildNode( new ApplicationPropertyNode( "PackageID",   ApplicationDescriptor.APPLICATION_PACKAGE ) );
		addChildNode( new ApplicationPropertyNode( "ContainerID", ApplicationDescriptor.APPLICATION_CONTAINER ) );
		
		addChildNode( new ApplicationPluginBaseNode("Ext") );
	}
	
	public String[]  getNames( String []path ) {
		
		ServiceReference[] refs = null;
		
		try {
		  refs = ApplicationPlugin.bc.getServiceReferences( ApplicationDescriptor.class.getName(), null );
		}catch( InvalidSyntaxException e) {}
		
		if( refs == null || refs.length == 0 )
			return new String [ 0 ];
		
		String result[] = new String[ refs.length ];
		for( int i=0; i != refs.length; i++ )
			result[ i ] = (String)refs[ i ].getProperty( Constants.SERVICE_PID );
		
		return result;
	}
}

class ApplicationRootNode extends ApplicationPluginBaseNode {
	ApplicationRootNode() {
		super( "Application" );
		
		addChildNode( new ApplicationIDNode() );
	}	
}

public class ApplicationPlugin implements BundleActivator, DataPluginFactory,
                                          ExecPlugin, ReadWriteDataSession {

	static final String			          URI_ROOT_APP = "./OSGi/Application";

	static BundleContext		          bc;
	private ServiceRegistration       pluginReg;
	
	private ApplicationPluginBaseNode rootNode;

	public void start(BundleContext bc) throws Exception {
		ApplicationPlugin.bc = bc;
		// registers the data and exec DMT plugin
		Dictionary dict = new Hashtable();
		dict.put("dataRootURIs", new String[] {URI_ROOT_APP});
		dict.put("execRootURIs", new String[] {URI_ROOT_APP});
		String[] ifs = new String[] {DataPluginFactory.class.getName(),
				DataPluginFactory.class.getName()};
		// unregistered by the OSGi framework
		pluginReg = bc.registerService(ifs, this, dict);
		
		rootNode = new ApplicationRootNode();
	}

	public void stop(BundleContext context) throws Exception {
		pluginReg.unregister();
		ApplicationPlugin.bc = null;
	}

	public ReadableDataSession openReadOnlySession(String[] path, DmtSession session) throws DmtException {
		return this;
	}

	public ReadWriteDataSession openReadWriteSession(String[] path, DmtSession session) throws DmtException {
		return this;
	}

	public TransactionalDataSession openAtomicSession(String[] path, DmtSession session) throws DmtException {
		return null; /* not supported */
	}

	private ApplicationPluginBaseNode getNode( String path[] ) throws DmtException {
		ApplicationPluginBaseNode node = rootNode.search( path, 2 );
		if( node != null )
			return node;
		
		throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");		
	}
	
	private ApplicationPluginBaseNode getParentNode( String path[] ) throws DmtException {
		if( path.length < 3 )
			return null;
			
		String pathNew[] = new String[ path.length - 1 ];
		for( int i=0; i != pathNew.length; i++ )
			pathNew[ i ] = path[ i ];
		
		ApplicationPluginBaseNode node = rootNode.search( pathNew, 2 );
		if( node != null )
			return node;
		
		throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");		
	}
	
	public void execute(DmtSession session, String[] path, String correlator, String data) throws DmtException {
		ApplicationPluginBaseNode node = getNode( path );
		node.execute( session, path, correlator, data );
	}

	public void copy(String[] path1, String[] path2, boolean resursive) throws DmtException {
		throw new DmtException( path1, DmtException.FEATURE_NOT_SUPPORTED,
                            "Copy not supported!" ); 
	}

	public void createInteriorNode(String[] path, String type) throws DmtException {
		ApplicationPluginBaseNode node = getParentNode( path );
		node.createInteriorNode( path, type );		
	}

	public void createLeafNode(String[] path, DmtData value, String type) throws DmtException {
		ApplicationPluginBaseNode node = getParentNode( path );
    node.createLeafNode( path, value, type );		
	}

	public void deleteNode(String[] path) throws DmtException {
		ApplicationPluginBaseNode node = getParentNode( path );
    node.deleteNode( path );				
	}

	public void renameNode(String[] path, String name) throws DmtException {
		throw new DmtException(path, DmtException.COMMAND_FAILED,
 		                       "Node rename not supported!");
	}

	public void setNodeTitle(String[] path, String title) throws DmtException {
		throw new DmtException( path, DmtException.FEATURE_NOT_SUPPORTED,
                            "Title property not supported!" ); 
	}

	public void setNodeType(String[] path, String type) throws DmtException {
		ApplicationPluginBaseNode node = getNode( path );
    node.setNodeType( path, type );
	}

	public void setNodeValue(String[] path, DmtData value) throws DmtException {
		ApplicationPluginBaseNode node = getNode( path );
    node.setNodeValue( path, value );		
	}

	public void nodeChanged(String[] arg0) throws DmtException {
    // do nothing - the version and timestamp properties are not supported		
	}

	public void close() throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public String[] getChildNodeNames(String[] path) throws DmtException {		
		ApplicationPluginBaseNode node = getNode( path );
    return node.getChildNodeNames( path );
	}

	public MetaNode getMetaNode(String[] path) throws DmtException {
		ApplicationPluginBaseNode node = rootNode.search( path, 2 );
		if( node != null )
			return node;
		
		node = getParentNode( path );
		return node.getChildMetaData( path );
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

	public String getNodeType(String[] path) throws DmtException {
		ApplicationPluginBaseNode node = getNode( path );
    return node.getNodeType( path );		
	}

	public boolean isNodeUri(String[] path) {
		try {
		  ApplicationPluginBaseNode node = getNode( path );
		  return true;
		}catch( DmtException e ) {
			return false;			
		}
	}

	public boolean isLeafNode(String[] path) throws DmtException {
		return getMetaNode( path ).isLeaf();
	}

	public DmtData getNodeValue(String[] path) throws DmtException {
		ApplicationPluginBaseNode node = getNode( path );
    return node.getNodeValue( path );		
	}

	public int getNodeVersion(String[] path) throws DmtException {
		throw new DmtException( path, DmtException.FEATURE_NOT_SUPPORTED,
                        		"Version property not supported!" ); 
	}
	
	static ServiceReference getApplicationDescriptor( String path[] ) {
		String appUID = path[ 3 ];
		try {
			ServiceReference[] refs = bc.getServiceReferences( ApplicationDescriptor.class.getName(), 
					"(" + Constants.SERVICE_PID + "=" + appUID + ")");
			if ( refs == null || refs.length !=  1)
				return null;
			
			return refs[ 0 ];
		}catch( Exception e ) {
			return null;
		}		
	}

	static ServiceReference getApplicationHandle( String path[] ) {
		try {
  	  ServiceReference refs[] = bc.getServiceReferences( ApplicationHandle.class.getName(), 
                                      "(" + Constants.SERVICE_PID + "=" + path[ 5 ] + ")" );
  	  
      if( refs == null || refs.length != 1 )
        return null;

      return refs[ 0 ];
		}catch( Exception e ) {
			return null;
		}
	}
}
