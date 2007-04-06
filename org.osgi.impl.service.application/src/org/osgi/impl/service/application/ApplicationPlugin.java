/*
 * $Id$
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

import info.dmtree.DmtAdmin;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import info.dmtree.Uri;
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ExecPlugin;
import info.dmtree.spi.ReadWriteDataSession;
import info.dmtree.spi.ReadableDataSession;
import info.dmtree.spi.TransactionalDataSession;

import java.net.URL;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.application.ScheduledApplication;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;


interface ArgumentInterface {
	public HashMap getHash( String path[] ) throws DmtException;
	public void changed( String path[] );
}

class ArgumentVariableNode extends ApplicationPluginBaseNode {
	private ArgumentIDNode argIDRef;
	
	ArgumentVariableNode( ArgumentIDNode argIDRef, String name, int format ) {
		super( name, "text/plain", format );
		
		this.argIDRef = argIDRef;		
		canReplace = true;
		
		if( name.equals( "Value" ) )
			setZeroOccurrenceAllowed( true );
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
		
		setMaxOccurrence( Integer.MAX_VALUE );
		setZeroOccurrenceAllowed( true );
		
		setScope( MetaNode.DYNAMIC );
		
		this.callerRef = callerRef;
		this.treeDepth = treeDepth;
		
		addChildNode( new ArgumentVariableNode(this, "Name", DmtData.FORMAT_STRING ) );
		addChildNode( new ArgumentVariableNode(this, "Value", DmtData.FORMAT_NULL | DmtData.FORMAT_BINARY |
				                                              DmtData.FORMAT_STRING | DmtData.FORMAT_INTEGER |
				                                              DmtData.FORMAT_FLOAT | DmtData.FORMAT_BOOLEAN ) );
		
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
	
	ScheduleItemNode( ScheduleIDNode schedIDNode, String name, int kind, int format ) {
		super( name, "text/plain", format );
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

		setMaxOccurrence( Integer.MAX_VALUE );
		setZeroOccurrenceAllowed( true );
		
		setScope( MetaNode.DYNAMIC );
		
		canAdd = canDelete = true;
		
		addChildNode( new ScheduleItemNode( this, "Enabled",     0, DmtData.FORMAT_BOOLEAN ));
		addChildNode( new ScheduleItemNode( this, "TopicFilter", 1, DmtData.FORMAT_STRING ));
		addChildNode( new ScheduleItemNode( this, "EventFilter", 2, DmtData.FORMAT_STRING ));
		addChildNode( new ScheduleItemNode( this, "Recurring",   3, DmtData.FORMAT_BOOLEAN ));
		
		addChildNode( new ApplicationPluginBaseNode( "Arguments",
				          argIDNode = new ArgumentIDNode( this, 7 )));
	}
	
	public String[] getNames( String []path ) {
		String pid = ApplicationIDNode.getPID( path );
		Hashtable scheduleHash = (Hashtable)schedulesByPidHash.get( pid );
		if( scheduleHash == null ) {
			scheduleHash = new Hashtable();
			schedulesByPidHash.put( pid, scheduleHash );
		}
		
		synchronizeHashWithRegistry( pid, scheduleHash );
		
		String result[] = new String [ scheduleHash.size() ];
		Enumeration enumeration = scheduleHash.keys();
		int i=0;
		while( enumeration.hasMoreElements() )
			result[ i++ ] = (String)enumeration.nextElement();
		
		return result;
	}	
	
	public DmtData getItemValue( String path[], int kind ) throws DmtException {
		String pid = ApplicationIDNode.getPID( path );
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
		if( ApplicationIDNode.getApplicationDescriptor( path ) == null ) {
			Activator.log( LogService.LOG_ERROR, "Cannot modify properties of an orphaned schedule!", null );
			throw new DmtException( path, DmtException.COMMAND_FAILED, "Cannot modify properties of an orphaned schedule!" );							
		}
		String pid = ApplicationIDNode.getPID( path );
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
					enable( item, path, key );				
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
		String pid = ApplicationIDNode.getPID( path );
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
		String pid = ApplicationIDNode.getPID( path );
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
		if( ApplicationIDNode.getApplicationDescriptor( path ) == null ) {
			Activator.log( LogService.LOG_ERROR, "Cannot create schedule for an uninstalled applivation!", null );
			throw new DmtException( path, DmtException.COMMAND_FAILED, "Cannot create schedule for an uninstalled applivation!" );							
		}
		String pid = ApplicationIDNode.getPID( path );
		String key = path[ path.length - 1 ];    
		Hashtable scheduleHash = (Hashtable)schedulesByPidHash.get( pid );
		if( scheduleHash == null ) {
			scheduleHash = new Hashtable();
			schedulesByPidHash.put( pid, scheduleHash );
		}
		scheduleHash.put( key, new ScheduledItem() );
	}
	
	public void deleteNode(String path[]) throws DmtException {
		String pid = ApplicationIDNode.getPID( path );
		String key = path[ path.length - 1 ];    
		Hashtable scheduleHash = (Hashtable)schedulesByPidHash.get( pid );
		if( scheduleHash == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
		
		ScheduledItem item = (ScheduledItem)scheduleHash.remove( key );
		if( item != null )
			disable( item );
		
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
			
			Enumeration enumeration = scheduleHash.keys();
			while( enumeration.hasMoreElements() ) {
				String key = (String)enumeration.nextElement();
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
					enumeration = scheduleHash.keys(); /* restart check because of delete */
				}
			}
			
			for( int j=0; j != findInHash.length; j++ ) {
				if( !findInHash[ j ] ) {       /* the reference is missing from the hash table? */
					Object schedID = refs[ j ].getProperty( ScheduledApplication.SCHEDULE_ID );
					
					if( schedID == null ) {
						System.err.println( "No scheduling ID found, schedule ignored!" );
						continue;
					}
					
					String key = (String)schedID;  /* place it into the hash */
					ScheduledItem item = new ScheduledItem();
					item.servRef = refs[ j ];
					item.enabled = true;
					
					ScheduledApplication schedApp = (ScheduledApplication)ApplicationPlugin.bc
					                                      .getService( refs[ j ] );
					
					if( (item.eventFilter = schedApp.getEventFilter() ) == null )
						item.eventFilter = "";					
					item.topicFilter = schedApp.getTopic();
					Map args = schedApp.getArguments();
					if( args == null )
						args = new HashMap();
					item.arguments = argIDNode.toArgIDHash( args );
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
	
	void enable( ScheduledItem item, String path[], String key ) throws DmtException {
		if( item.enabled )
			disable( item );
		
		try {
			ServiceReference appDescRef = ApplicationIDNode.getApplicationDescriptor( path );
			if( appDescRef == null ) {
				Activator.log( LogService.LOG_ERROR, "Cannot enable schedule for an uninstalled application!", null );
				throw new DmtException( path, DmtException.COMMAND_FAILED, "Cannot enable schedule for an uninstalled application!" );				
			}
			ApplicationDescriptor appDesc = (ApplicationDescriptor)ApplicationPlugin.bc.getService( appDescRef );
			
			String eventFilter = item.eventFilter;
			if( eventFilter.equals( "" ) )
				eventFilter = null;
			ScheduledApplicationImpl schedApp = (ScheduledApplicationImpl)
				appDesc.schedule( key, argIDNode.getArguments( item.arguments ), 
						item.topicFilter, eventFilter, item.recurring );
			item.servRef = schedApp.getReference();
			item.enabled = true;		  
			ApplicationPlugin.bc.ungetService( appDescRef );
		}catch( Exception e ) {
			Activator.log( LogService.LOG_ERROR, "Error occured at enabling the schedule!", e );
			throw new DmtException( path, DmtException.COMMAND_FAILED, "Schedule throwed an exception!" );
		}
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
	
	LaunchResultNode( LaunchIDNode launchIDRef, String name, int kind, int format ) {
		super( name, "text/plain", format );
		
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
		
		setScope( MetaNode.DYNAMIC );
		setMaxOccurrence( Integer.MAX_VALUE );
		setZeroOccurrenceAllowed( true );
		
		addChildNode( new ApplicationPluginBaseNode( "Arguments", argIDNode = new ArgumentIDNode( this, 8 ) ) );
		addChildNode( new ApplicationPluginBaseNode( "Result", 
				new LaunchResultNode( this, "InstanceID", 0, DmtData.FORMAT_STRING ),
				new LaunchResultNode( this, "Status",     1, DmtData.FORMAT_STRING ),
				new LaunchResultNode( this, "Message",    2, DmtData.FORMAT_STRING ) ) );
		
		canAdd = canDelete = canExecute = true;
	}
	
	public String[] getNames( String []path ) {
		String pid = ApplicationIDNode.getPID( path );
		Hashtable launchIDHash = (Hashtable)launchIDsHash.get( pid );
		if( launchIDHash == null )
			return new String[ 0 ];
		
		String elems[] = new String[ launchIDHash.size() ];
		Enumeration enumeration = launchIDHash.keys();
		int i=0;
		while( enumeration.hasMoreElements() )
			elems[ i++ ] = (String)enumeration.nextElement();
		return elems;
	}
		
	public void createInteriorNode(String path[], String type) throws DmtException {
		String pid = ApplicationIDNode.getPID( path );
    String key = path[ path.length - 1 ];    
		Hashtable launchIDHash = (Hashtable)launchIDsHash.get( pid );
		if( launchIDHash == null ) {
			launchIDHash = new Hashtable();
			launchIDsHash.put( pid, launchIDHash );
		}
    launchIDHash.put( key, new LaunchableItem() );
	}
	
	public void deleteNode(String path[]) throws DmtException {
		String pid = ApplicationIDNode.getPID( path );
    String key = path[ path.length - 1 ];    
		Hashtable launchIDHash = (Hashtable)launchIDsHash.get( pid );
		if( launchIDHash == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
    launchIDHash.remove( key );
    if( launchIDHash.size() == 0 )
    	launchIDsHash.remove( pid );
	}	
	
	public void execute(DmtSession session, String path[], String correlator, String data) throws DmtException {
		String pid = ApplicationIDNode.getPID( path );

		String key = path[ path.length - 1 ];
		Hashtable launchIDHash = (Hashtable)launchIDsHash.get( pid );
		if( launchIDHash == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");
    
		LaunchableItem item = (LaunchableItem) launchIDHash.get( key );
		if( item == null )
			throw new DmtException(path, DmtException.NODE_NOT_FOUND, "Node not found.");

		ServiceReference appDescRef = ApplicationIDNode.getApplicationDescriptor( path );
		if( appDescRef == null ) {
			Activator.log( LogService.LOG_ERROR, "Cannot execute an uninstalled application!", null );
			throw new DmtException( path, DmtException.COMMAND_FAILED, "Cannot execute an uninstalled application!" );				
		}
				
		HashMap args = argIDNode.getArguments( item.argumentHash );
		
		ApplicationDescriptor appDesc = (ApplicationDescriptor)Activator.bc.getService( appDescRef );
		
		item.resultInstanceID = "";
		item.resultMessage = "";
		item.resultStatus = "";
		try {
			ApplicationHandle appHnd = appDesc.launch( args );
			item.resultInstanceID = appHnd.getInstanceId();
			item.resultStatus = "OK";
		}catch( Exception e ) {
			item.resultMessage = e.getMessage();
			item.resultStatus = e.getClass().getName();
		}
		
		Activator.bc.ungetService( appDescRef );
	}

	public String getResultValue( String path[], int kind ) throws DmtException  {
		String pid = ApplicationIDNode.getPID( path );
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
		String pid = ApplicationIDNode.getPID( path );
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
		isLeaf = canExecute = canGet = true;
		format = DmtData.FORMAT_NULL;
	}
	
	public void execute(DmtSession session, String path[], String correlator, String data) throws DmtException {
		ServiceReference ref = ApplicationIDNode.getApplicationDescriptor( path );
		if( ref == null ) {
			Activator.log( LogService.LOG_ERROR, "Cannot lock/unlock an uninstalled application!", null );
			throw new DmtException(path, DmtException.METADATA_MISMATCH, "Cannot lock/unlock an uninstalled application!" );
		}
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
		
		isLeaf = canExecute = canGet = true;
		
		format = DmtData.FORMAT_NULL;
	}
	
	public void execute(DmtSession session, String path[], String correlator, String data) throws DmtException {
		ServiceReference appHandle = InstanceIDNode.getApplicationHandle( path );
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
		super( "State", "text/plain", DmtData.FORMAT_STRING );
	}
	
	public DmtData getNodeValue( String path[] ) throws DmtException {
		ServiceReference appHndRef = InstanceIDNode.getApplicationHandle( path );
	  if( appHndRef == null )
	  	throw new DmtException(path, DmtException.METADATA_MISMATCH, "Cannot get node value!" );
		  
	  String state = (String)appHndRef.getProperty( ApplicationHandle.APPLICATION_STATE );
	  if( state == null )
	  	throw new DmtException(path, DmtException.METADATA_MISMATCH, "Cannot get node value!" );
		  	
	  return new DmtData( state );
	}
}

class InstanceIDPropertyNode extends ApplicationPluginBaseNode {
	InstanceIDPropertyNode() {
		super( "InstanceID", "text/plain", DmtData.FORMAT_STRING );
	}
	
	public DmtData getNodeValue( String path[] ) throws DmtException {
		String instID = InstanceIDNode.getInstanceID( path );
	  if( instID == null )
	  	throw new DmtException(path, DmtException.METADATA_MISMATCH, "Cannot get the instance ID!" );		  
	  return new DmtData( instID );
	}
}

class InstanceIDNode extends ApplicationPluginBaseNode {
	
	private static Hashtable mangledInstanceIDHash = new Hashtable();
	
	InstanceIDNode() {
		super();

		setMaxOccurrence( Integer.MAX_VALUE );
		setZeroOccurrenceAllowed( true );
		
		addChildNode( new ApplicationPluginBaseNode("Operations", 
				      new InstanceOperationsStopNode()) );
		addChildNode( new InstanceStateNode() );
		addChildNode( new InstanceIDPropertyNode() );
	}

	public String[]  getNames( String []path ) {
		
		ServiceReference[] refs = null;
		
		String appUID = null;
		
		try {
		  refs = ApplicationPlugin.bc.getServiceReferences( ApplicationHandle.class.getName(), 
                         "(" + ApplicationHandle.APPLICATION_DESCRIPTOR + "=" + 
                         (appUID = ApplicationIDNode.getPID( path )) + ")" );
		}catch( InvalidSyntaxException e) {}
		
		if( refs == null || refs.length == 0 )
			return new String [ 0 ];
		
		String result[] = new String[ refs.length ];
		for( int i=0; i != refs.length; i++ ) {
			String instanceID = (String)refs[ i ].getProperty( Constants.SERVICE_PID );
			String mangledInstanceID = ApplicationPlugin.mangle( instanceID );
			mangledInstanceIDHash.put( appUID + "/" + mangledInstanceID, instanceID );
			result[ i ] = mangledInstanceID;
		}
		
		return result;
	}
	
	static String getInstanceID( String path[] ) {
		String appUID     = ApplicationIDNode.getPID( path );
		String instanceID = (String)mangledInstanceIDHash.get( appUID + "/" + path[ 5 ] );
		return instanceID;
	}

	static ServiceReference getApplicationHandle( String path[] ) {
		String appUID     = ApplicationIDNode.getPID( path );
		String instanceID = (String)mangledInstanceIDHash.get( appUID + "/" + path[ 5 ] );
		if( instanceID == null )
			return null;
		try {
  	  ServiceReference refs[] = ApplicationPlugin.bc.getServiceReferences( ApplicationHandle.class.getName(), 
                                      "(" + Constants.SERVICE_PID + "=" + instanceID + ")" );
  	  
      if( refs == null || refs.length != 1 )
        return null;

      return refs[ 0 ];
		}catch( Exception e ) {
			return null;
		}
	}
}

class ApplicationValidityNode extends ApplicationPluginBaseNode {
	ApplicationValidityNode() {
		super( "Valid", "text/plain", DmtData.FORMAT_BOOLEAN );
	}
	
	public DmtData getNodeValue( String path[] ) throws DmtException {
		ServiceReference appDescRef = ApplicationIDNode.getApplicationDescriptor( path );
		if( appDescRef == null )
			return new DmtData( false );
		else
			return new DmtData( true );
	}
}


class ApplicationPropertyNode extends ApplicationPluginBaseNode {
	private String  name;
	private String  propertyName;
	
	ApplicationPropertyNode( String name, String propertyName, int format ) {
		super( name, "text/plain", format );
		
		this.name = name;
		this.propertyName = propertyName;
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
		Object prop = getProperty( path );
		if( prop == null )
			throw new DmtException(path, DmtException.METADATA_MISMATCH, "Cannot get node value!" );
		
		if( getFormat() != DmtData.FORMAT_BOOLEAN ) {
			if( prop instanceof URL )
				return new DmtData( ((URL)prop).toString() );
			else  
				return new DmtData( (String)prop );
		} else
          return new DmtData( ((Boolean)prop ).booleanValue() );
	}
	
	Object getProperty( String []path )  throws DmtException {
		ServiceReference ref = ApplicationIDNode.getApplicationDescriptor( path );
		if( ref == null ) {
			if( name.equals( "ApplicationID" ) )
				return ApplicationIDNode.getPID( path );
			if( isZeroOccurrenceAllowed() )
				return null;
			
			switch( getFormat() ) {
			case DmtData.FORMAT_BOOLEAN:
				return new Boolean( false );
			case DmtData.FORMAT_STRING:
				return new String();
			}			
			return null;
		}
		return ref.getProperty( propertyName );
	}
}

class ApplicationIDNode extends ApplicationPluginBaseNode {
	
	private static Hashtable mangledAppIDHash = new Hashtable();
	
	ApplicationIDNode() {
		super();
		
		setScope( MetaNode.PERMANENT );
		setMaxOccurrence( Integer.MAX_VALUE );
		setZeroOccurrenceAllowed( true );
		
		addChildNode( new ApplicationPluginBaseNode( "Schedules", new ScheduleIDNode() ) );
		
		addChildNode( new ApplicationPluginBaseNode( "Operations",
				new ApplicationPluginBaseNode("Launch", new LaunchIDNode() ),
				new LockerNode("Unlock",false), new LockerNode("Lock",true)) );
		
		addChildNode( new ApplicationPluginBaseNode( "Instances", new InstanceIDNode() ) );
		addChildNode( new ApplicationValidityNode() );
		
		ApplicationPropertyNode vendor, version;
		
		addChildNode( new ApplicationPropertyNode( "Name",          ApplicationDescriptor.APPLICATION_NAME ,     DmtData.FORMAT_STRING ) );
		addChildNode( new ApplicationPropertyNode( "IconURI",       ApplicationDescriptor.APPLICATION_ICON,      DmtData.FORMAT_STRING ) );
		addChildNode( vendor = new ApplicationPropertyNode( "Version",       ApplicationDescriptor.APPLICATION_VERSION,   DmtData.FORMAT_STRING ) );
		addChildNode( version = new ApplicationPropertyNode( "Vendor",        ApplicationDescriptor.APPLICATION_VENDOR,    DmtData.FORMAT_STRING ) );
		addChildNode( new ApplicationPropertyNode( "Locked",        ApplicationDescriptor.APPLICATION_LOCKED,    DmtData.FORMAT_BOOLEAN ) );
		addChildNode( new ApplicationPropertyNode( "ContainerID",   ApplicationDescriptor.APPLICATION_CONTAINER, DmtData.FORMAT_STRING ) );
		addChildNode( new ApplicationPropertyNode( "ApplicationID", ApplicationDescriptor.APPLICATION_PID,       DmtData.FORMAT_STRING ) );
		addChildNode( new ApplicationPropertyNode( "Location",      ApplicationDescriptor.APPLICATION_LOCATION,  DmtData.FORMAT_STRING ) );
		
		vendor.setZeroOccurrenceAllowed( true );
		version.setZeroOccurrenceAllowed( true );		
	}
	
	public String[]  getNames( String []path ) {
		
		mangledAppIDHash.clear();
		
		ServiceReference[] refs = null;
		
		Vector appPids = new Vector();
		
		try {
			refs = ApplicationPlugin.bc.getServiceReferences( ApplicationDescriptor.class.getName(), null );		  
		  	if( refs != null ) {
				for( int i=0; i != refs.length; i++ ) {
					String appID = (String)refs[ i ].getProperty( Constants.SERVICE_PID );
					String mangledAppId = ApplicationPlugin.mangle( appID );
					mangledAppIDHash.put( mangledAppId, appID );
					appPids.add( mangledAppId );
				}			  				
		  	}
		  	
			refs = ApplicationPlugin.bc.getServiceReferences( ScheduledApplication.class.getName(), null );		  
		  	if( refs != null ) {
				for( int i=0; i != refs.length; i++ ) {
					String appID = (String)refs[ i ].getProperty( ScheduledApplication.APPLICATION_PID );
					String mangledAppId = ApplicationPlugin.mangle( appID );
					if( mangledAppIDHash.get( mangledAppId ) == null ) {
						mangledAppIDHash.put( mangledAppId, appID );
						appPids.add( mangledAppId );
					}
				}			  				
		  	}
		}catch( InvalidSyntaxException e) {}
				
		String result[] = new String[ appPids.size() ];
		for( int i=0; i != result.length; i++ )
			result[ i ] = (String)appPids.remove( 0 );
		
		return result;
	}

	static String getPID( String path[] ) {
		String appUID = (String)mangledAppIDHash.get( path[ 3 ] );
		if( appUID == null )
			return null;
		return appUID;
	}
	
	static ServiceReference getApplicationDescriptor( String path[] ) {
		String appUID = (String)mangledAppIDHash.get( path[ 3 ] );
		if( appUID == null )
			return null;
		try {
			ServiceReference[] refs = ApplicationPlugin.bc.getServiceReferences( ApplicationDescriptor.class.getName(), 
					"(" + Constants.SERVICE_PID + "=" + appUID + ")");
			if ( refs == null || refs.length !=  1)
				return null;
			
			return refs[ 0 ];
		}catch( Exception e ) {
			return null;
		}		
	}
}

public class ApplicationPlugin implements BundleActivator, DataPlugin,
                                          ExecPlugin, ReadWriteDataSession {

	static final String			          URI_ROOT_APP = "./OSGi/Application";

	static BundleContext		          bc;
	private ServiceRegistration       pluginReg;
	
	private ApplicationPluginBaseNode rootNode;
	private static ServiceTracker     dmtTracker;

	public void start(BundleContext bc) throws Exception {
		ApplicationPlugin.bc = bc;
		// registers the data and exec DMT plugin
		Dictionary dict = new Hashtable();
		dict.put("dataRootURIs", new String[] {URI_ROOT_APP});
		dict.put("execRootURIs", new String[] {URI_ROOT_APP});
		String[] ifs = new String[] {DataPlugin.class.getName(),
				DataPlugin.class.getName()};
		// unregistered by the OSGi framework
		pluginReg = bc.registerService(ifs, this, dict);
		
		dmtTracker = new ServiceTracker( bc, DmtAdmin.class.getName(), null );
		dmtTracker.open();
		
		rootNode = new ApplicationPluginBaseNode( "Application", new ApplicationIDNode() );
		rootNode.setScope( MetaNode.PERMANENT );
	}

	public void stop(BundleContext context) throws Exception {
		pluginReg.unregister();
		dmtTracker.close();
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
		  getNode( path );
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
	
	static String mangle( String in ) {
		DmtAdmin dmtAdmin = (DmtAdmin)dmtTracker.getService();
		if( dmtAdmin == null )
			throw new RuntimeException("DmtAdmin not running!");
		return Uri.mangle( in );
	}
}
