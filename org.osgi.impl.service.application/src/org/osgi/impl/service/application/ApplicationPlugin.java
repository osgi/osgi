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

import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.application.*;
import org.osgi.service.dmt.*;
import org.osgi.service.dmt.spi.*;
import org.osgi.service.log.LogService;


interface ArgumentInterface {
	public HashMap getHash( String path[] ) throws DmtException;
	public void changed( String path[] );
}

class ArgumentVariableNode extends ApplicationPluginBaseNode {
	private ArgumentIDNode argIDRef;
	
	ArgumentVariableNode( ArgumentIDNode argIDRef, String name ) {
		super( name, "text/plain" );
		
		this.argIDRef = argIDRef;		
		canReplace = true;
	}
	public String[] getNames( String []path ) {
		if( !name.equals( "Value" ) )
			return new String [] {name};
		try {
			argIDRef.checkVariableValue( path );
			return new String [] {name};			
		}catch( Exception e ) {
			return new String [ 0 ];
		}
	}
		
	public void setNodeValue(String path[], DmtData value) throws DmtException {
		argIDRef.setVariableValue( path, value );		
	}
	
	public DmtData getNodeValue(String path[]) throws DmtException {
		return argIDRef.getVariableValue( path );		
	}	
}

class ArgumentIDNode extends ApplicationPluginBaseNode {
	private ArgumentInterface callerRef;
	private int treeDepth;
			
	ArgumentIDNode( ArgumentInterface callerRef, int treeDepth ) {
		super();
		
		this.callerRef = callerRef;
		this.treeDepth = treeDepth;
		
		addChildNode( new ArgumentVariableNode(this, "Name" ) );
		addChildNode( new ArgumentVariableNode(this, "Value" ) );
		
		canAdd = canDelete = true;
	}

	public void deleteNode(String path[]) throws DmtException {
		String argID = path[ treeDepth ];
		
		HashMap argHash = callerRef.getHash( path );
		argHash.remove( argID );
		
		callerRef.changed( path );
	}

	public void createInteriorNode(String path[], String type) throws DmtException {
		String argID = path[ treeDepth ];
		
		HashMap argHash = callerRef.getHash( path );
		argHash.put( argID, new NameValuePair() );

		callerRef.changed( path );
	}
	
	public HashMap getArguments( HashMap itemHash ) {
		HashMap hashmap = new HashMap();
		
		Iterator iter = itemHash.keySet().iterator();
		while( iter.hasNext() ) {
			String key = (String)iter.next();
			NameValuePair nvp = (NameValuePair)itemHash.get( key );
			hashmap.put( nvp.name, nvp.value );
		}
		
		return hashmap;
	}
	
	public HashMap toArgIDHash( Map arguments ) {
		HashMap hashmap = new HashMap();
		
		int argCnt = 1;
		
		Iterator iter = arguments.keySet().iterator();
		while( iter.hasNext() ) {
			String key = (String)iter.next();
			Object value = arguments.get( key );
			
			NameValuePair nvp = new NameValuePair();
			nvp.name = key;
			nvp.value = value;
			
			hashmap.put( "ARG" + argCnt++, nvp );
		}
		
		return hashmap;
	}
	
	DmtData getVariableValue( String path[] ) throws DmtException {
		String var = path[ treeDepth + 1 ];
		String argID = path[ treeDepth ];
		
		HashMap argHash = callerRef.getHash( path );
		NameValuePair nvp = (NameValuePair)argHash.get( argID );
		
		if( nvp == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");		
		
		if( var.equals( "Name" ) ) {
			return new DmtData( nvp.name );
		}else{
			if( nvp.value == null )
				return DmtData.NULL_VALUE;
			if( nvp.value instanceof String )
				return new DmtData( (String)nvp.value );
			else if( nvp.value instanceof Boolean )
				return new DmtData( ((Boolean)nvp.value).booleanValue() );
			else if( nvp.value instanceof byte [] )
				return new DmtData( (byte [])nvp.value );
			else if( nvp.value instanceof Integer )
				return new DmtData( ((Integer)nvp.value).intValue() );
			else if( nvp.value instanceof Float )
				return new DmtData( ((Float)nvp.value).floatValue() );
			
			throw new DmtException(path, DmtException.METADATA_MISMATCH, "Cannot map the value to a DMT type!" );
		}		
	}

	void checkVariableValue( String path[] ) throws DmtException {
		String argID = path[ treeDepth ];
		
		HashMap argHash = callerRef.getHash( path );
		NameValuePair nvp = (NameValuePair)argHash.get( argID );
		
		if( nvp == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");		
		
		if( nvp.value == null || nvp.value instanceof String || nvp.value instanceof Boolean ||
				nvp.value instanceof byte [] || nvp.value instanceof Integer || 
				nvp.value instanceof Float )
			return;
			
		throw new DmtException(path, DmtException.METADATA_MISMATCH, "Cannot map the value to a DMT type!" );
	}
	
	public String[] getNames( String []path ) {
		try {
			HashMap ht = callerRef.getHash( path );
		  String result[] = new String[ ht.size() ];
		  Iterator iter = ht.keySet().iterator();
		  int i=0;
		  while( iter.hasNext() )
			  result[ i++ ] = (String)iter.next();		 
		
		  return result;
		}catch( Exception e ) {
			return new String [ 0 ];
		}
	}
	
	void setVariableValue( String path[], DmtData value ) throws DmtException {
		String var = path[ treeDepth + 1 ];
		String argID = path[ treeDepth ];
		
		HashMap argHash = callerRef.getHash( path );
		NameValuePair nvp = (NameValuePair)argHash.get( argID );
		
		if( nvp == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");		
		
		if( var.equals( "Name" ) ) {
			nvp.name = value.getString();			
		} else {
			switch( value.getFormat() ) {
				case DmtData.FORMAT_BINARY:
					nvp.value = value.getBinary();
				  break;
				case DmtData.FORMAT_STRING:
					nvp.value = value.getString();
				  break;
				case DmtData.FORMAT_INTEGER:
					nvp.value = new Integer( value.getInt() );
				  break;
				case DmtData.FORMAT_FLOAT:
					nvp.value = new Float( value.getFloat() );
				  break;
				case DmtData.FORMAT_BOOLEAN:
					nvp.value = new Boolean( value.getBoolean() );
				  break;
				case DmtData.FORMAT_NULL:
					nvp.value = null;
				  break;
				default:
					return;
			}
		}		
		callerRef.changed( path );
	}
	
	class NameValuePair {
		String name = "";
		Object value = null;
	}
}

class ScheduleItemNode extends ApplicationPluginBaseNode {
	private int kind;
	private ScheduleIDNode schedIDNode;
	
	ScheduleItemNode( ScheduleIDNode schedIDNode, String name, int kind ) {
		super( name, "text/plain" );
		this.kind = kind;
	  this.schedIDNode = schedIDNode;
		canReplace = true;
	}
	
	public DmtData getNodeValue( String path[] ) throws DmtException {
	  return schedIDNode.getItemValue( path, kind );
	}

	public void setNodeValue(String path[], DmtData value) throws DmtException {
		schedIDNode.setItemValue( path, kind, value );		
	}
}

class ScheduleIDNode extends ApplicationPluginBaseNode implements ArgumentInterface {
	private Hashtable  schedulesByPidHash = new Hashtable();
	private ArgumentIDNode argIDNode;
	
	ScheduleIDNode() {
		super();		
		canAdd = canDelete = true;
		
		addChildNode( new ScheduleItemNode( this, "Enabled", 0 ));
		addChildNode( new ScheduleItemNode( this, "TopicFilter", 1 ));
		addChildNode( new ScheduleItemNode( this, "EventFilter", 2 ));
		addChildNode( new ScheduleItemNode( this, "Recurring", 3 ));
		
		addChildNode( new ApplicationPluginBaseNode( "Arguments",
				          argIDNode = new ArgumentIDNode( this, 7 )));
	}
	
	public String[] getNames( String []path ) {
		String pid = ApplicationPlugin.getPID( path );
		Hashtable scheduleHash = (Hashtable)schedulesByPidHash.get( pid );
		if( scheduleHash == null ) {
			scheduleHash = new Hashtable();
			schedulesByPidHash.put( pid, scheduleHash );
		}
		
		synchronizeHashWithRegistry( pid, scheduleHash );
		
		String result[] = new String [ scheduleHash.size() ];
		Enumeration enum = scheduleHash.keys();
		int i=0;
		while( enum.hasMoreElements() )
			result[ i++ ] = (String)enum.nextElement();
		
		return result;
	}	
	
	public DmtData getItemValue( String path[], int kind ) throws DmtException {
		String pid = ApplicationPlugin.getPID( path );
    String key = path[ path.length - 2 ];    		
		Hashtable scheduleHash = (Hashtable)schedulesByPidHash.get( pid );
		if( scheduleHash == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
	  ScheduledItem item = (ScheduledItem)scheduleHash.get( key );
		if( item == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
		switch( kind ) {
			case 0:
				return new DmtData( item.enabled );
			case 1:
				return new DmtData( item.topicFilter );
			case 2:
				return new DmtData( item.eventFilter );
			case 3:
				return new DmtData( item.recurring );
		}
		throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");		
	}

	public void setItemValue(String path[], int kind, DmtData value) throws DmtException {
		String pid = ApplicationPlugin.getPID( path );
    String key = path[ path.length - 2 ];    		
		Hashtable scheduleHash = (Hashtable)schedulesByPidHash.get( pid );
		if( scheduleHash == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
	  ScheduledItem item = (ScheduledItem)scheduleHash.get( key );
		if( item == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
		switch( kind ) {
			case 0:
				if( value.getBoolean() )
					enable( item, path );				
				else
					disable( item );
				break;
			case 1:
				item.topicFilter = value.getString();
				disable( item );
				break;
			case 2:
				item.eventFilter = value.getString();
				disable( item );
				break;
			case 3:
				item.recurring = value.getBoolean();
				disable( item );
				break;
		}
	}

	public HashMap getHash(String[] path) throws DmtException {
		String pid = ApplicationPlugin.getPID( path );
		String key = path[ 5 ];
		Hashtable scheduleHash = (Hashtable)schedulesByPidHash.get( pid );
		if( scheduleHash == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
				
		ScheduledItem item = (ScheduledItem) scheduleHash.get( key );
		if( item == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
		
		return item.arguments;
	}

	public void changed(String[] path) {
		String pid = ApplicationPlugin.getPID( path );
		String key = path[ 5 ];
		Hashtable scheduleHash = (Hashtable)schedulesByPidHash.get( pid );
		if( scheduleHash == null )
			return;
				
		ScheduledItem item = (ScheduledItem) scheduleHash.get( key );
		if( item == null )
			return;
		
		disable( item );
	}
	
	public void createInteriorNode(String path[], String type) throws DmtException {
		String pid = ApplicationPlugin.getPID( path );
    String key = path[ path.length - 1 ];    
		Hashtable scheduleHash = (Hashtable)schedulesByPidHash.get( pid );
		if( scheduleHash == null ) {
			scheduleHash = new Hashtable();
			schedulesByPidHash.put( pid, scheduleHash );
		}
		schedulesByPidHash.put( key, new ScheduledItem() );
	}
	
	public void deleteNode(String path[]) throws DmtException {
		String pid = ApplicationPlugin.getPID( path );
    String key = path[ path.length - 1 ];    
		Hashtable scheduleHash = (Hashtable)schedulesByPidHash.get( pid );
		if( scheduleHash == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
		scheduleHash.remove( key );
    if( scheduleHash.size() == 0 )
    	schedulesByPidHash.remove( pid );
	}	
	
	void synchronizeHashWithRegistry( String pid, Hashtable scheduleHash ) {
		try {			
			ServiceReference refs[] = ApplicationPlugin.bc.getServiceReferences( ScheduledApplication.class.getName(),
                                "(" + ApplicationDescriptor.APPLICATION_PID + "=" + pid + ")");
			
			if( refs == null )
				refs = new ServiceReference[ 0 ];

			boolean findInHash[] = new boolean [ refs.length ];
			for( int w=0; w != refs.length; w++ )
				findInHash[ w ] = false;
			
			Enumeration enum = scheduleHash.keys();
			while( enum.hasMoreElements() ) {
				String key = (String)enum.nextElement();
				ScheduledItem item = (ScheduledItem)scheduleHash.get( key );
				
				boolean foundReference = false;
				for( int i=0; i != refs.length; i++ )
					if( item.servRef == refs[ i ] ) {
						foundReference = true;
						findInHash[ i ] = true;
						break;
					}
				
				if( !foundReference && item.enabled ) {  /* a real reference was deleted ? */
					scheduleHash.remove( key ); /* remove the item from the hash as well */
					enum = scheduleHash.keys(); /* restart check because of delete */
				}
			}
			
			for( int j=0; j != findInHash.length; j++ ) {
				if( !findInHash[ j ] ) {       /* the reference is missing from the hash table? */
					String key = generateKey( scheduleHash );  /* place it into the hash */
					ScheduledItem item = new ScheduledItem();
					item.servRef = refs[ j ];
					item.enabled = true;
					
					ScheduledApplication schedApp = (ScheduledApplication)ApplicationPlugin.bc
					                                      .getService( refs[ j ] );
					
					item.eventFilter = schedApp.getEventFilter();
					item.topicFilter = schedApp.getTopic();
					item.arguments = argIDNode.toArgIDHash( schedApp.getArguments() );
					item.recurring = schedApp.isRecurring();
					
					ApplicationPlugin.bc.ungetService( refs[ j ] );
					
					scheduleHash.put( key, item );					
				}
			}
			
		}catch( InvalidSyntaxException e ) {}
	}
	
	void disable( ScheduledItem item ) {
		ServiceReference ref = item.servRef;
		if( ref != null && ref.getBundle() != null ) {
			ScheduledApplication schedApp = (ScheduledApplication)ApplicationPlugin.bc
			                                 .getService( ref );
			schedApp.remove();
			ApplicationPlugin.bc.ungetService( ref );
		}		
		item.servRef = null;
		item.enabled = false;
	}
	
	void enable( ScheduledItem item, String path[] ) {
		if( item.enabled )
			disable( item );
		
		try {
			ServiceReference appDescRef = ApplicationPlugin.getApplicationDescriptor( path );
			ApplicationDescriptor appDesc = (ApplicationDescriptor)ApplicationPlugin.bc.getService( appDescRef );
		  appDesc.schedule( argIDNode.getArguments( item.arguments ), item.topicFilter, item.eventFilter, item.recurring );
		  item.enabled = true;
		  ApplicationPlugin.bc.ungetService( appDescRef );
		}catch( Exception e ) {}
	}
	
	String generateKey( Hashtable scheduleHash ) {
		int schedNum = 1;
		while( scheduleHash.containsKey( "S" + schedNum ) )
			schedNum++;
		return "S"+schedNum;
	}
	
	class ScheduledItem {
		public boolean          enabled = false;
		public String           topicFilter = "";
		public String           eventFilter = "";
		public boolean          recurring = false;		
		public HashMap          arguments = new HashMap();
		
		public ServiceReference servRef = null;
	}
}

class LaunchResultNode extends ApplicationPluginBaseNode {
	private LaunchIDNode launchIDRef;
	private int          kind;
	
	LaunchResultNode( LaunchIDNode launchIDRef, String name, int kind ) {
		super( name, "text/plain" );
		
		this.launchIDRef = launchIDRef;
		this.kind = kind;
	}
	
	public DmtData getNodeValue( String path[] ) throws DmtException {
	  return new DmtData( launchIDRef.getResultValue( path, kind ) );
	}
}


class LaunchIDNode extends ApplicationPluginBaseNode implements ArgumentInterface {

	private Hashtable launchIDsHash = new Hashtable();
	private ArgumentIDNode argIDNode;
	
	LaunchIDNode() {
		super();
		
		addChildNode( new ApplicationPluginBaseNode( "Arguments", argIDNode = new ArgumentIDNode( this, 8 ) ) );
		addChildNode( new ApplicationPluginBaseNode( "Result", 
				new LaunchResultNode( this, "InstanceID", 0 ),
				new LaunchResultNode( this, "Status", 1 ),
				new LaunchResultNode( this, "Message", 2 ) ) );
		
		canAdd = canDelete = canExecute = true;
	}
	
	public String[] getNames( String []path ) {
		String pid = ApplicationPlugin.getPID( path );
		Hashtable launchIDHash = (Hashtable)launchIDsHash.get( pid );
		if( launchIDHash == null )
			return new String[ 0 ];
		
		String elems[] = new String[ launchIDHash.size() ];
		Enumeration enum = launchIDHash.keys();
		int i=0;
		while( enum.hasMoreElements() )
			elems[ i++ ] = (String)enum.nextElement();
		return elems;
	}
		
	public void createInteriorNode(String path[], String type) throws DmtException {
		String pid = ApplicationPlugin.getPID( path );
    String key = path[ path.length - 1 ];    
		Hashtable launchIDHash = (Hashtable)launchIDsHash.get( pid );
		if( launchIDHash == null ) {
			launchIDHash = new Hashtable();
			launchIDsHash.put( pid, launchIDHash );
		}
    launchIDHash.put( key, new LaunchableItem() );
	}
	
	public void deleteNode(String path[]) throws DmtException {
		String pid = ApplicationPlugin.getPID( path );
    String key = path[ path.length - 1 ];    
		Hashtable launchIDHash = (Hashtable)launchIDsHash.get( pid );
		if( launchIDHash == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
    launchIDHash.remove( key );
    if( launchIDHash.size() == 0 )
    	launchIDsHash.remove( pid );
	}	
	
	public void execute(DmtSession session, String path[], String correlator, String data) throws DmtException {
		String pid = ApplicationPlugin.getPID( path );
    String key = path[ path.length - 1 ];
		Hashtable launchIDHash = (Hashtable)launchIDsHash.get( pid );
		if( launchIDHash == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
    
		LaunchableItem item = (LaunchableItem) launchIDHash.get( key );
		if( item == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
		
		HashMap args = argIDNode.getArguments( item.argumentHash );
		
		ServiceReference appDescRef = ApplicationPlugin.getApplicationDescriptor( path );
		ApplicationDescriptor appDesc = (ApplicationDescriptor)Activator.bc.getService( appDescRef );
		
		item.resultInstanceID = "";
		item.resultMessage = "";
		item.resultStatus = "";
		try {
			ApplicationHandle appHnd = appDesc.launch( args );
			item.resultInstanceID = appHnd.getInstanceID();
			item.resultStatus = "OK";
		}catch( Exception e ) {
			item.resultMessage = e.getMessage();
			item.resultStatus = e.getClass().getName();
		}
		
		Activator.bc.ungetService( appDescRef );
	}

	public String getResultValue( String path[], int kind ) throws DmtException  {
		String pid = ApplicationPlugin.getPID( path );
		String key = path[ 6 ];
		Hashtable launchIDHash = (Hashtable)launchIDsHash.get( pid );
		if( launchIDHash == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
		
		LaunchableItem item = (LaunchableItem) launchIDHash.get( key );
		if( item == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");		
	
		String result = "";
		
		switch( kind ) {
		case 0:
			result = item.resultInstanceID;
			break;
		case 1:
			result = item.resultStatus;
			break;
		case 2:
			result = item.resultMessage;
			break;			
		}		
		return result;
	}
	
	public HashMap getHash( String path[] ) throws DmtException {
		String pid = ApplicationPlugin.getPID( path );
		String key = path[ 6 ];
		Hashtable launchIDHash = (Hashtable)launchIDsHash.get( pid );
		if( launchIDHash == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
				
		LaunchableItem item = (LaunchableItem) launchIDHash.get( key );
		if( item == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
		
		return item.argumentHash;
	}
	
	class LaunchableItem {
		public String    resultInstanceID = "";
		public String    resultStatus = "";
		public String    resultMessage = "";
    public HashMap   argumentHash = new HashMap();
	}

	public void changed(String[] path) {
		// do nothing		
	}
}

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
		
		addChildNode( new ApplicationPluginBaseNode("Operations", 
				                                        new ApplicationPluginBaseNode("Ext"),
																								new InstanceOperationsStopNode()) );
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
		
		addChildNode( new ApplicationPluginBaseNode( "Schedules", new ScheduleIDNode() ) );
		
		addChildNode( new ApplicationPluginBaseNode( "Operations",
				new ApplicationPluginBaseNode("Launch", new LaunchIDNode() ),
				new LockerNode("Unlock",false), new LockerNode("Lock",true)) );
		
		addChildNode( new ApplicationPluginBaseNode( "Instances", new InstanceIDNode() ) );
		
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
		
		rootNode = new ApplicationPluginBaseNode( "Application", new ApplicationIDNode() );
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
		ApplicationPluginBaseNode node = getParentNode( path ).getAdditiveChild();
		if( node == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
		
		node.createInteriorNode( path, type );		
	}

	public void createLeafNode(String[] path, DmtData value, String type) throws DmtException {
		ApplicationPluginBaseNode node = getParentNode( path ).getAdditiveChild();
		if( node == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");		
		
    node.createLeafNode( path, value, type );		
	}

	public void deleteNode(String[] path) throws DmtException {
		ApplicationPluginBaseNode node = getNode( path );
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
	}

	public String[] getChildNodeNames(String[] path) throws DmtException {		
		ApplicationPluginBaseNode node = getNode( path );
    return node.getChildNodeNames( path );
	}

	public MetaNode getMetaNode(String[] path) throws DmtException {
		ApplicationPluginBaseNode node = rootNode.search( path, 2 );
		if( node != null )
			return node;
		
		node = getParentNode( path ).getAdditiveChild();
		if( node == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");		
		
		return node;
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

	static String getPID( String path[] ) {
		String appUID = path[ 3 ];
		try {
			ServiceReference[] refs = bc.getServiceReferences( ApplicationDescriptor.class.getName(), 
					"(" + Constants.SERVICE_PID + "=" + appUID + ")");
			if ( refs == null || refs.length !=  1)
				return null;
			
			return (String)refs[ 0 ].getProperty( Constants.SERVICE_PID );
		}catch( Exception e ) {
			return null;
		}				
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
