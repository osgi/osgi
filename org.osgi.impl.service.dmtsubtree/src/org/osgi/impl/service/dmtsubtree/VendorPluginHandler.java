package org.osgi.impl.service.dmtsubtree;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.Uri;
import info.dmtree.spi.DataPlugin;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.dmtsubtree.mapping.IDPersistence;
import org.osgi.impl.service.dmtsubtree.mapping.VendorPluginInfo;
import org.osgi.impl.service.dmtsubtree.mapping.flags.MappedPath;
import org.osgi.impl.service.dmtsubtree.mapping.flags.MultipleID;
import org.osgi.impl.service.dmtsubtree.mapping.flags.MultipleIDMapping;
import org.osgi.impl.service.dmtsubtree.mapping.flags.VendorDataPlugin;
import org.osgi.impl.service.dmtsubtree.sessions.WriteableDataSession;
import org.osgi.service.cm.Configuration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;


public class VendorPluginHandler implements Constants, ServiceListener, EventHandler {

	// filter for vendor data plugins
	final String				_DATA_PLUGIN_FILTER	= "(&("
															+ org.osgi.framework.Constants.OBJECTCLASS
															+ "="
															+ DataPlugin.class
																	.getName()
															+ ")"
															+ "("+_CONF_DATA_ROOT_URIs+"=*)("
															+ _CONF_PATHS + "=*))";
	private final String[]		_TOPICS	= new String[] {
			"info/dmtree/DmtEvent/ADDED", "info/dmtree/DmtEvent/DELETED",
			"info/dmtree/DmtEvent/REPLACED"};
	private final String		_EVENT_FILTER	= "(&(!(session.id=*))(!(newnodes=*))(nodes=*))";


	private Activator activator;
	private IDPersistence persistence;
	
	// this set keeps references of non-assignable vendor dataPlugins in the chronological order of appearance
	private Hashtable		registrations;
	private ServiceRegistration eventHandlerRegistration;
	private Util util;

	public VendorPluginHandler( Activator activator ) {
		this.activator = activator;
		this.util = new Util( activator );
		initialize();
	}
	
	private void initialize() {
	
		this.persistence = new IDPersistence( activator );
		// restore mappings of multiple ID's from persistence
		restoreIdMappings();
		
		// check and handle all DataPlugins registered now before we start tracking them
		assignVendorDataPlugins();
		try {
			activator.context.addServiceListener(this, _DATA_PLUGIN_FILTER);
			activator.logDebug("added servicelistener for Filter: "
					+ _DATA_PLUGIN_FILTER);
		}
		catch (InvalidSyntaxException e) {
			activator
					.logError(
							"Invalid Filter for DataPlugins: ",
							e);
			e.printStackTrace();
		}

		// registering EventHandler
		Hashtable eventProps = new Hashtable();
		eventProps.put(EventConstants.EVENT_TOPIC, _TOPICS);
		eventProps.put(EventConstants.EVENT_FILTER, _EVENT_FILTER);
		eventHandlerRegistration = activator.context.registerService(
				EventHandler.class.getName(), this, eventProps);

	}
	
	
	/**
	 * check all currently registered vendor plugins
	 */
	private void assignVendorDataPlugins() {
		// handle all DataPlugins registered now before we start tracking them
		ServiceReference[] refs;
		try {
			refs = activator.context.getServiceReferences( DataPlugin.class.getName(), _DATA_PLUGIN_FILTER );
			for (int i = 0; refs != null && i < refs.length; i++) {
				ServiceReference ref = refs[i];
				VendorPluginInfo[] pluginInfos = null;
				try {
					pluginInfos = util.extractVendorPluginInfo( ref );
					handleRegisteredDataPlugin(pluginInfos);
				} catch (RuntimeException e) {
					activator.logError( "The registration properties of the DataPlugin are invalid! --> ignoring this plugin", e );
					continue;
				}
			}
		}
		catch (InvalidSyntaxException e) {
			activator
					.logError(
							"Invalid Filter for DataPlugins: ",
							e);
			e.printStackTrace();
		}

	}

	
	public void cleanup() {
		if (eventHandlerRegistration != null) {
			eventHandlerRegistration.unregister();
			eventHandlerRegistration = null;
		}

		cleanupRegistrations();
	}

	private void cleanupRegistrations() {
		Vector pluginInfos = new Vector();
		Enumeration keys = getRegistrations().keys(); 
		while (keys.hasMoreElements())
			pluginInfos.add( keys.nextElement() );
		handleUnregisteredDataPlugin( (VendorPluginInfo[]) pluginInfos.toArray(new VendorPluginInfo[pluginInfos.size()]));
	}

	/**
	 * triggers a complete re-assignment of the vendor plugins
	 */
	void reassign() {
		cleanupRegistrations();
		assignVendorDataPlugins();
	}

	/**
	 * callback for changes of matching DataPlugin registrations
	 * 
	 * @param serviceEvent
	 * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
	 */
	public void serviceChanged(ServiceEvent serviceEvent) {
		activator.logInfo("service changed");

		final ServiceReference ref = serviceEvent.getServiceReference();
		final int type = serviceEvent.getType();
//		util.dumpProperties(ref);
		VendorPluginInfo[] pluginInfos = null;
		try {
			pluginInfos = util.extractVendorPluginInfo( ref );
		} catch (RuntimeException e) {
			activator.logError( "The registration properties of the DataPlugin are invalid! --> ignoring this plugin", e );
			return;
		}

		if (type == ServiceEvent.REGISTERED) {
			activator.logInfo("DataPlugin was added");
			handleRegisteredDataPlugin(pluginInfos);
		}
		else
		if (type == ServiceEvent.UNREGISTERING) {
			activator.logInfo("DataPlugin was removed");
			handleUnregisteredDataPlugin(pluginInfos);
		}
	}

	/*
	 * handles a single added DataPlugin
	 * 
	 * @param pluginRef
	 */
	private void handleRegisteredDataPlugin(VendorPluginInfo[] pluginInfos) {
		for (int i = 0; i < pluginInfos.length; i++) {
			VendorPluginInfo pluginInfo = pluginInfos[i];
			
			// check for matching rootPlugin(s)
			if ( checkAncestorRootNode( pluginInfo ) == null )  {
				activator.logInfo( "No ancestor rootnode found for plugin path: " + pluginInfo.getConfigurationPath() + " --> This plugin will be ignored for now!" );
				continue;
			}

			// we have to check if this path is already used/blocked by another plugin
			// (the only safe way to check would be to open a DMTSession and to invoke isNodeUri() for this node)
			// here we are just checking for existence of other plugins with the same mapped pathes
			ServiceReference directMatch = checkDirectMatch( pluginInfo );
			
			// non-multiple ?
			if ( ! pluginInfo.isMultiple() ) {
				// check against active mappings
				if ( directMatch != null ) {
					activator.logInfo( "There is already a mapping that includes plugin path: " + pluginInfo.getConfigurationPath() + " --> This plugin will be ignored for now!" );
					continue;
				}
				else {
					// would it hide a multiple mapping (active or inactive)?
					if ( checkMultiple( pluginInfo.getConfigurationPath() ) != null ) {
						activator.logInfo( "There is already a multiple mapping (active or inactive) that would be hidden by this plugin (with path: " + pluginInfo.getConfigurationPath() + ") --> This plugin will be ignored!" );
						EventAdmin ea = (EventAdmin) activator.eaTracker.getService();
						Hashtable props = new Hashtable();
						props.put( "node", pluginInfo.getConfigurationPath() );
						ea.postEvent( new Event( _EVENT_TOPIC_NODE_EXISTS, props ));
						continue;
					}
				}
				registerMapping( pluginInfo );
			} 
			else {
				// get or create the unique id for this plugin
				String vendorPathID = getID(pluginInfo, true);
				registerMapping( pluginInfo, vendorPathID );
			}
		}
	}

	/*
	 * handles unregistered Vendor DataPlugins
	 * 
	 * @param pluginRef
	 */
	private void handleUnregisteredDataPlugin(VendorPluginInfo[] pluginInfos) {
		for (int i = 0; i < pluginInfos.length; i++) {
			VendorPluginInfo pluginInfo = pluginInfos[i];
			ServiceRegistration reg = (ServiceRegistration) getRegistrations().get( pluginInfo );
			if ( reg != null ) {
				// remove the injected dmtActions stuff
				removeDmtActionsNode( pluginInfo );
				reg.unregister();
				getRegistrations().remove( pluginInfo );
			}
		}
	}

	
	private Hashtable getRegistrations() {
		if (registrations == null) {
			registrations = new Hashtable();
		}
		return registrations;
	}
	
	/**
	 * Checks for registered RootPlugins that are an ancestor of the given plugin
	 * @param pluginInfo
	 * @return
	 */
	private ServiceReference checkAncestorRootNode( VendorPluginInfo pluginInfo ) {
		ServiceReference ref = null;
		String configurationPath = pluginInfo.getConfigurationPath();
		int pos = configurationPath.indexOf( '/', 2 );
		if ( pos == -1 )
			// this plugin is invalid, because it points to a root-node
			return null;
		// find all potential ancestor rootnodes
		String filter1 = "(" + _ROOTNODE + "=*)";
		String filter2 = "(" + _MAPPED_NODE_PATH + "=" + configurationPath.substring( 0, pos )+ ")";
		String filter3 = "(" + _MAPPED_NODE_PATH + "=" + configurationPath.substring( 0, pos )+ "/*)";
		String filter = "(&" + filter1 + "(|" + filter2 + filter3 + "))";
		ServiceReference[] refs;
		try {
			refs = activator.context.getServiceReferences( DataPlugin.class.getName(), filter );
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			activator.logError( "Error in filter syntax while looking up ancestor nodes in the service registry.", e );
			return null;
		}
		if ( refs != null ) {
			for (int i = 0; i < refs.length; i++) {
				String mappedPath = (String) refs[i].getProperty( _MAPPED_NODE_PATH );
				// is it an ancestor ?
				if ( configurationPath.startsWith( mappedPath )) {
					ref = refs[i];
					// stop at first match
					break;
				}
			}
		}
		return ref;
	}
	
	/**
	 * Checks for any registered plugin (rootNode or vendor plugin) with the same mappedNodePath 
	 * 
	 * @param pluginInfo
	 * @return
	 */
	private ServiceReference checkDirectMatch( VendorPluginInfo pluginInfo ) {
		ServiceReference ref = null;
		String configurationPath = pluginInfo.getConfigurationPath();

		String filter = "(" + _MAPPED_NODE_PATH + "=" + configurationPath + ")";
		ServiceReference[] refs;
		try {
			refs = activator.context.getServiceReferences( MappedPath.class.getName(), filter );
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			activator.logError( "Error in filter syntax while looking up direct matches in the service registry.", e );
			return null;
		}
		if ( refs != null )
			ref = refs[0];
		return ref;
	}
	
	/**
	 * Checks for any registered plugin (rootNode or vendor plugin) with the same mappedNodePath 
	 * 
	 * @param pluginInfo
	 * @return
	 */
	private ServiceReference checkMultiple( String configurationPath ) {
		ServiceReference ref = null;
		String filter = "(" + _MAPPED_NODE_PATH + "=" + configurationPath + ")";
		ServiceReference[] refs;
		try {
			refs = activator.context.getServiceReferences( MultipleIDMapping.class.getName(), filter );
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			activator.logError( "Error in filter syntax while looking up multiple id matches in the service registry.", e );
			return null;
		}
		if ( refs != null )
			ref = refs[0];
		return ref;
	}
	
	/**
	 * facade of same method with default value null for the id parameter 
	 * @param pluginInfo
	 */
	private void registerMapping( VendorPluginInfo pluginInfo ) {
		registerMapping( pluginInfo, null );
	}
	
	/**
	 * Registers a instance of VendorDataPlugin for the given VendorPluginInfo
	 * @param pluginInfo
	 * @param id .. if given, it will be added to the configurationPath of the pluginInfo
	 */
	private void registerMapping( VendorPluginInfo pluginInfo, String id ) {
		VendorDataPlugin vendorDataPlugin = new VendorDataPlugin();
		String path = pluginInfo.getConfigurationPath();
		if ( id != null ) 
			path = buildMultiplePath( pluginInfo.getConfigurationPath(), id );
		
		Hashtable properties = new Hashtable();
		properties.put( _MAPPED_NODE_PATH, path );
		properties.put( _CONF_DATA_ROOT_URIs, pluginInfo.getDataRootURI() );
		properties.put( _PLUGIN_INFO, pluginInfo );
		properties.put( _CONF_MULTIPLES, "" + pluginInfo.isMultiple() );
		ServiceRegistration reg = activator.context.registerService( new String[] {VendorDataPlugin.class.getName(), MappedPath.class.getName()}, vendorDataPlugin, properties );
		getRegistrations().put( pluginInfo, reg );

		injectMappedPath( pluginInfo, id );
	}
	

	/************** mapping of multiple ids *****************/
	
	/**
	 * Returns a new or already settled id for the given combination of
	 * dataRootURI and hgConfigurationPath
	 * 
	 * @param pluginInfo .. VendorPluginInfo
	 * @param create ... defines whether a new id shall be created if not
	 *        already found in list
	 * @return
	 */
	public String getID(VendorPluginInfo pluginInfo, boolean create) {
		String id = null;
		
		// has this plugin already been mapped before? --> check direct match
		String filter1 = "(" + _CONF_PATHS + "=" + pluginInfo.getConfigurationPath() + ")";
		String filter2 = "(" + _CONF_DATA_ROOT_URIs + "=" + pluginInfo.getDataRootURI() + ")";
		String filter3 = "(" + _MULTIPLE_ID + "=*)";
		String filter = "(&" + filter1 + filter2 + filter3 + ")";
		ServiceReference[] refs;
		try {
			refs = activator.context.getServiceReferences( MultipleID.class.getName(), filter );
			if ( refs != null )
				id = (String) refs[0].getProperty( _MULTIPLE_ID );
			else if ( create ) {
				// count plugins for this configuration Path and create a new id
				filter = "(&" + filter1 + filter3 + ")";
				refs = activator.context.getServiceReferences( MultipleID.class.getName(), filter );
				if ( refs != null )
					id = "" + refs.length;
				else 
					id = "0";

				// register this MultipleID
				Hashtable props = new Hashtable();
				props.put( _CONF_DATA_ROOT_URIs, pluginInfo.getDataRootURI() );
				props.put( _CONF_PATHS, pluginInfo.getConfigurationPath() );
				props.put( _MULTIPLE_ID, id );
				// also put the potential mapped path into the props to allow for later checks
				props.put( _MAPPED_NODE_PATH, buildMultiplePath( pluginInfo.getConfigurationPath(), id ) );
				activator.context.registerService( MultipleID.class.getName(), new MultipleIDMapping(), props );
				// and make the new id-mapping persistent
				persistence.storeIDMapping(pluginInfo.getDataRootURI(), pluginInfo.getConfigurationPath(), id);
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			activator.logError( "Error in filter syntax while looking up ids for multiple mappings.", e );
		}
		return id;
	}

	/**
	 * reads all stored mapping configurations and registeres the corresponding MultipleIDMappings
	 */
	private void restoreIdMappings() {
		activator
				.logDebug("''''''''''''' reading idMapping from configurations");
		Configuration[] configs = persistence.getIDMappings();
		if (configs == null || configs.length == 0)
			return;

		for (int i = 0; i < configs.length; i++) {

			Dictionary dict = configs[i].getProperties();
			String configPath = (String) dict.get(Constants._CONF_PATHS);
			String dataRootURI = (String) dict.get("dataRootURI");
			String id = (String) dict.get("id");

			if (configPath == null || dataRootURI == null || id == null ) {
				activator.logInfo( "found an invalid idMapping in configuration --> ignoring" );
				continue;
			}
			
			// register this MultipleID
			Hashtable props = new Hashtable();
			props.put( _CONF_DATA_ROOT_URIs, dataRootURI );
			props.put( _CONF_PATHS, configPath );
			props.put( _MULTIPLE_ID, id );
			activator.context.registerService( MultipleID.class.getName(), new MultipleIDMapping(), props );
			activator.logDebug("''''''''''''' adding id " + id
					+ " for dataRootURI: " + dataRootURI);
		}
	}

	/******** EVENT Handling ***********/
	
	public void handleEvent( Event event ) {
		activator.logDebug("EventForwarder received an event of topic: "
				+ event.getTopic());
		// is it our own event? --> check flag property
		if ( "true".equals( event.getProperty( _DMT_SUBTREE_EVENT )) ) {
			activator.logDebug( "it's our own event --> ignoring");
			return;
		}
		String[] nodes = (String[]) event.getProperty("nodes");
		Vector newNodes = new Vector();
		for (int i = 0; i < nodes.length; i++) {

			String node = nodes[i];
			
			// check longest mapping for this node
			int pos = node.indexOf( '/', 2 );
			pos = (pos == -1) ? node.length() : pos;
			String topLevelNode = node.substring( 0, pos );
			
			String filter1 = "(" + _CONF_DATA_ROOT_URIs + "=" + topLevelNode + ")";
			String filter2 = "(" + _CONF_DATA_ROOT_URIs + "=" + topLevelNode + "/*)";
			String filter = "(|" + filter1 + filter2 + ")";
			ServiceReference[] refs = null;
			try {
				refs = activator.context.getServiceReferences( VendorDataPlugin.class.getName(), filter );
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
				activator.logError( "Error in filter syntax while looking up matching Plugin for event forwarding of node: " + node, e );
			}
			int max = 0;
			ServiceReference ref = null;
			for (int j = 0; refs != null && j < refs.length; j++) {
				String rootUri = (String) refs[j].getProperty( _CONF_DATA_ROOT_URIs );
				int depth = util.countSlashes( rootUri );
				if ( depth > max ) {
					max = depth;
					ref = refs[j];
				}
			}
			if ( ref != null ) {
				String rootUri = (String) ref.getProperty( _CONF_DATA_ROOT_URIs );
				String mappedPath = (String) ref.getProperty( _MAPPED_NODE_PATH ); 
					
				String suffix = node.substring(rootUri.length());
				if (suffix.startsWith("/"))
					suffix = suffix.substring(1);

				// mapped path is absolute and already contains multiple index (if exists)
				String newNode = mappedPath + "/" + suffix;
				newNodes.add( newNode ); 
				activator.logDebug("converted node: '" + node + "' to: '" + newNode + "'");
			}
		}
		
		if (newNodes.size() > 0 ) {
			// create and send new event
			Hashtable props = new Hashtable();
			props.put( "nodes", newNodes.toArray(new String[0]));
			// flag this event as our own
			props.put( _DMT_SUBTREE_EVENT, "true" );
			Event newEvent = new Event(event.getTopic(), props );
			
			EventAdmin ea = (EventAdmin)activator.eaTracker.getService();
			if ( ea != null )
				ea.postEvent(newEvent);
		}
	}
	
	private String buildMultiplePath( String configPath, String id ) {
		if ( configPath == null || id == null )
			return null;
		String path = null;
		int pos = configPath.lastIndexOf( '/' );
		if ( pos != -1 ) {
			path = configPath.substring( 0, pos ) + "/" + id + configPath.substring( pos );
		}
		return path;
	}
	
	/*********** handling of dmtactions **************/
	
	/**
	 * This method injects entries into the specified dmtAction - node right after a successful mapping.
	 * If the dmtActionNode and or its parents are not existing they will be created.
	 * So far only the "mappedPath" entry will be added according to current RFC version.
	 * @param pluginInfo
	 * @param mappedPath
	 */
	private void injectMappedPath( VendorPluginInfo pluginInfo, String multipleID ) {
		/*
		 * - check and - if needed - create parent nodes of the dmtActions node
		 * - create leaf "mappedPath" and set the String value to mappedPath
		 */
		// this is the relative path under the plugins configurationPath
		String dmtActionNode = pluginInfo.getDmtActionNode();
		if ( dmtActionNode == null )
			return;
		
		String[] dmtActionsPath = Uri.toPath( dmtActionNode );
		Vector tmpPath = new Vector( Arrays.asList( Uri.toPath( pluginInfo.getConfigurationPath() )));
		if ( multipleID != null ) 
			tmpPath.insertElementAt( multipleID, tmpPath.size() - 1 );
		
		WriteableDataSession session = null;
		String[] tmpPathArr = (String[]) tmpPath.toArray( new String[tmpPath.size()] );
		try {
			session = new WriteableDataSession( activator, tmpPathArr );
			// check and setup the parent nodes
			for (int i = 0; i < dmtActionsPath.length; i++) {
				tmpPath.add( dmtActionsPath[i] );
				tmpPathArr = (String[]) tmpPath.toArray( new String[tmpPath.size()] );
				// does this interior node exist already in the plugin?
				if ( ! session.isNodeUri( tmpPathArr )) {
					session.createInteriorNode( tmpPathArr, null );
				}
			}
			// create leafNode "mappedPath" and set content
			DmtData data = new DmtData( pluginInfo.getConfigurationPath() + "/" + multipleID );
			tmpPath.add( _ACTIONS_MAPPED_PATH );
			tmpPathArr = (String[]) tmpPath.toArray( new String[tmpPath.size()] );
			if ( ! session.isNodeUri( tmpPathArr ) )  {
				session.createLeafNode( tmpPathArr, null, null );
			}
			session.setNodeValue( tmpPathArr, data );
		} catch (DmtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if ( session != null )
				try {
					session.close();
				} catch (DmtException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * This method removes the dmtAction node itself from the data plugin.
	 * @param pluginInfo
	 */
	private void removeDmtActionsNode( VendorPluginInfo pluginInfo ) {
		/**
		 * get the path to the dmtActionsNode and remove it
		 */
		// this is the relative path under the plugins configurationPath
		String dmtActionNode = pluginInfo.getDmtActionNode();
		if ( dmtActionNode == null )
			return;

		String pluginPath = pluginInfo.getConfigurationPath();
		if (pluginInfo.isMultiple()) {
			// lookup the plugins id
			String vendorPathID = getID(pluginInfo, false);
			if (vendorPathID != null) {
				pluginPath+= "/" + vendorPathID;
			}
		}
		
		String absPath = pluginPath + "/" + dmtActionNode;
		WriteableDataSession session = null;
		try {
			String[] absPathArr = Uri.toPath( absPath );
			session = new WriteableDataSession( activator, absPathArr );
			// check and delete dmtAction node
			if ( session.isNodeUri( absPathArr )) {
				session.deleteNode( absPathArr );
			}
		} catch (DmtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if ( session != null )
				try {
					session.close();
				} catch (DmtException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

}
