/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.impl.service.dmtsubtree;

import info.dmtree.Uri;


import info.dmtree.spi.DataPlugin;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.impl.service.dmtsubtree.mapping.flags.MappedPath;

/**
 * This class manages the registrations and un-registrations of DataPlugins 
 * for configured rootnodes.
 * @author steffen
 *
 */
public class DmtSubtreeAdmin implements ManagedServiceFactory, Constants {


	private Activator			activator;
	private Hashtable 			registrations;
	private LinkedHashMap		pendingPlugins;
	private VendorPluginHandler vendorPluginHandler;
	private Util				util;

	public DmtSubtreeAdmin(Activator activator) {
		this.activator = activator;
		util = new Util( activator );
		initialize();
	}

	/*
	 * initialization of the DMTSubtreeAdmin - registers the ManagedServiceFactory
	 */
	private void initialize() {
		
		// register as ManagedServiceFactory
		Hashtable props = new Hashtable();
		props.put( org.osgi.framework.Constants.SERVICE_PID, _PID );
		activator.context.registerService( ManagedServiceFactory.class.getName(), this, props );

		vendorPluginHandler = new VendorPluginHandler( activator );
	}

	public void cleanup() {
		if ( vendorPluginHandler != null )
			vendorPluginHandler.cleanup();
		
		Enumeration regs = getRootPluginRegistrations().elements(); 
		while (regs.hasMoreElements()) {
			ServiceRegistration reg = (ServiceRegistration) regs.nextElement();
			reg.unregister();
		}
	}

	
	Hashtable getRootPluginRegistrations () {
		if (registrations == null) {
			registrations = new Hashtable();
		}
		return registrations;
	}

	LinkedHashMap getPendingPlugins() {
		if (pendingPlugins == null) {
			pendingPlugins = new LinkedHashMap();
		}
		return pendingPlugins;
	}


	/**
	 * called when a factory configuration has been deleted
	 * @param pid .. the deleted pid
	 */
	public void deleted(String pid) {
		delete(pid, true);
	}

	/*
	 * performs the real delete action
	 * @returns true, if a registration has been deleted and false otherwise
	 */
	private boolean delete(String pid, boolean triggerReassignment ) {
		boolean changed = false;
		ServiceRegistration reg = (ServiceRegistration) getRootPluginRegistrations().get( pid );
		if (reg != null) {
			String rootNode = (String) reg.getReference().getProperty( _ROOTNODE );
			reg.unregister();
			changed = true;
			getRootPluginRegistrations().remove( pid );
			
			if ( triggerReassignment )
				vendorPluginHandler.reassign();
			
			// lookup for a pending plugin with this rootNode
			Iterator iterator = getPendingPlugins().values().iterator();
			String pendingPid = null;
			while ( iterator.hasNext() ) {
				PendingPlugin pendingPlugin = (PendingPlugin) iterator.next();
				if ( rootNode.equals( pendingPlugin.rootNode )) 
					pendingPid = pendingPlugin.pid;
			}
			// if a matching pending plugin was found, remove it from the map and register a plugin for it 
			if ( pendingPid != null ) {
				getPendingPlugins().remove(pendingPid);
				registerRootPlugin(pendingPid, rootNode);
			}

			checkTopLevel( rootNode );
		}
		else {
			// remove from pending plugins if it was one
			getPendingPlugins().remove(pid);
		}
		return changed;
	}


	/**
	 * returns the name of this factory
	 */
	public String getName() {
		return "factory for DMT Subtree DataPlugins";
	}

	private boolean isPlausible( String rootNode ) {
		boolean plausible = true;
		if ( rootNode == null ) {
			activator.logInfo( "This configuration does not contain a property 'rootnode' --> it will be ignored " );
			plausible = false;
		}
		else
		// rootnode must be a valid and absolute URI 
		if ( !Uri.isValidUri( rootNode ) || !Uri.isAbsoluteUri( rootNode )) {
			activator.logInfo( "The configured rootnode ("+ rootNode +") is not a valid absolute DMT-URI --> it will be ignored " );
			plausible = false;
		}
		else { 
			String[] rootPath = Uri.toPath( rootNode );
			if ( rootPath.length == 1 ) {
				activator.logInfo( "The DMT-root '.' is not a valid path for a rootnode configuration --> it will be ignored " );
				plausible = false;
			}
		}
		return plausible;
	}
	
	
	/**
	 * called when a new configuration has been added or an existing one was updated
	 * - checks 
	 * @param pid .. the pid of the new or updated configuration
	 * @param props .. the Dictionary of properties for the new or updated configuration
	 */
	public void updated(String pid, Dictionary props) throws ConfigurationException {
		
		boolean changed = delete( pid, false );
		
		// mandatory property there ?
		String rootNode =  (String) props.get( _ROOTNODE );
		if ( ! isPlausible(rootNode) ) {
			if ( changed ) 
				// trigger re-assignment of VendorPlugins before canceling
				vendorPluginHandler.reassign();
			return;
		}
		
		// exact match already registered?
		String filter1 = "(" + _CONF_DATA_ROOT_URIs + "=" + rootNode + ")";
		String filter2 = "(" + _ROOTNODE + "=" + rootNode + ")";
		String filter = "(&" + filter1 + filter2 + ")";
		try {
			ServiceReference[] refs = activator.context.getServiceReferences( DataPlugin.class.getName(), filter );
			if ( refs != null && refs.length > 0 ) {
				activator.logInfo( "There is already a data plugin registered for rootnode: " + rootNode + " --> this one will be ignored for now" );
				getPendingPlugins().put(pid, new PendingPlugin(pid, rootNode));
				if ( changed ) 
					// trigger re-assignment of VendorPlugins before canceling
					vendorPluginHandler.reassign();
				return;
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

		checkTopLevel( rootNode );

		registerRootPlugin(pid, rootNode);
		vendorPluginHandler.reassign();
	}

	private void registerRootPlugin(String pid, String rootNode) {
		RootPlugin rootPlugin = new RootPlugin( Uri.toPath( rootNode ), activator );
		
		Hashtable properties = new Hashtable();
		properties.put( _ROOTNODE, rootNode );
		properties.put( _CONF_DATA_ROOT_URIs, new String[] { rootNode } );
		// for easier lookup and merging of "ghost" nodes from configured root-nodes and the ones from mapped vendor plugins
		// both will register also as MappedPath with the property "mappedNodePath"
		properties.put( _MAPPED_NODE_PATH, rootNode );
		ServiceRegistration reg = activator.context.registerService( new String[] {DataPlugin.class.getName(), MappedPath.class.getName()}, rootPlugin, properties );
		getRootPluginRegistrations().put( pid, reg );
	}

	/**
	 * This method ensures, that there is a top-level DataPlugin registered as long as needed and removed if not needed anymore.
	 * It should therefore be invoked before adding and after removal of RootPlugins. 
	 * NOTE: This method is a workaround for some missing functionality in the DMTAdmins reference implementation. The ref. impl. does
	 * not handle DataPlugin registrations with dataRootURIs deeper than top-level.
	 * @param rootNode
	 */
	private void checkTopLevel(String rootNode ) {
		String[] rootPath = Uri.toPath( rootNode );
		String topLevel = "./" + rootPath[1];

		boolean topLevelExists = false;
		
		// top level node there? (either emulated or configured)
		String filter1 = "(" + _CONF_DATA_ROOT_URIs + "=" + topLevel + ")";
		String filter2 = "(" + _ROOTNODE + "=" + topLevel + ")";
		String filter = "(&" + filter1 + filter2 + ")";
		try {
			ServiceReference[] refs = activator.context.getServiceReferences( DataPlugin.class.getName(), filter );
			topLevelExists = refs != null;
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

		// is there the need for an emulated topLevel node? (check nodes with depth > 1)
		int minDepth = util.countSlashes( rootNode );
		filter1 = "(" + _ROOTNODE + "=*)"; 
		filter2 = "(" + _CONF_DATA_ROOT_URIs + "=*)";
		String filter3 = "(" + _MAPPED_NODE_PATH + "=" + topLevel + ")";
		String filter4 = "(" + _MAPPED_NODE_PATH + "=" + topLevel + "/*)";
		filter = "(&" + filter1 + filter2 + "(|" + filter3 + filter4 + "))"; 
		try {
			ServiceReference[] refs = activator.context.getServiceReferences( DataPlugin.class.getName(), filter );
			for (int i = 0; refs != null && i < refs.length; i++) {
				String mappedPath = (String) refs[i].getProperty( _MAPPED_NODE_PATH );
				int depth = util.countSlashes( mappedPath );
				if ( depth < minDepth )
					minDepth = depth;
			}
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean topLevelNeeded = minDepth > 1;
		
		if ( topLevelNeeded && ! topLevelExists ) {
			// register emulated topLevel node
			RootPlugin rootPlugin = new RootPlugin( Uri.toPath( topLevel ), activator );
			Hashtable properties = new Hashtable(); 
			properties.put( _ROOTNODE, topLevel );
			properties.put( _CONF_DATA_ROOT_URIs, new String[] { topLevel } );
			// NOTE: this node does not get a _CONF_MAPPED_NODE_PATH property, because it is an automatically inserted 
			// node and not a really configured one. This way it can be distinguished from the others.

			ServiceRegistration reg = activator.context.registerService( DataPlugin.class.getName(), rootPlugin, properties );
			// put this also to the table with the top-level as key
			getRootPluginRegistrations().put( topLevel, reg );
		}
		
		if ( topLevelExists && ! topLevelNeeded ) {
			// remove emulated topLevel node
			ServiceRegistration reg = (ServiceRegistration) getRootPluginRegistrations().get( topLevel );
			if ( reg != null )
				reg.unregister();
			getRootPluginRegistrations().remove( topLevel );
		}
	}
	
	/**
	 * a simple data holder for rootnode-plugin configurations that are pending
	 * @author steffen
	 *
	 */
	class PendingPlugin {
		String pid;
		String rootNode;
		
		public PendingPlugin( String pid, String rootNode ) {
			this.pid = pid;
			this.rootNode = rootNode;
		}
	}
}
