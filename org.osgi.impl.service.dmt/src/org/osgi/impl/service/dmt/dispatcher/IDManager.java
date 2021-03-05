/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.dmt.dispatcher;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;


/**
 * This class is responsible for managing the unique id's for the enumerations.
 * It must ensure, that plugins (that provide a service.pid property) get the same 
 * id assigned for the same uri, even if they have been unregistered and registered again.
 * Plugins that don't provide a service.pid as part of their registration will not take advantage of persistence. 
 * Such plugins will get the first available id for the given uri.
 * @author steffen
 *
 */
public class IDManager {

	private static final String _PID = "org.osgi.impl.service.dmt.Persistency";
	
	private Hashtable<String, IDList> ids;
	private ServiceTracker<ConfigurationAdmin,ConfigurationAdmin>	trackerCM;

	/**
	 * holder class for an index/pid combination 
	 */
	class ID {
		int index;
		String pid;
		long bundleID = -1;
		public ID( String pid, long bundleID, int index ) {
			this.pid = pid;
			this.bundleID = bundleID;
			this.index = index;
		}
		@Override
		public String toString() {
			return " pid: " + pid + "  bunldeID: " + bundleID + "  id: " + index;
		}
	}
	
	/**
	 * helper class for maintaining a list of ID's
	 * @author steffen
	 *
	 */
	class IDList {
		@SuppressWarnings("hiding")
		Vector<ID> ids = new Vector<ID>();
		
		ID getId( String pid, long bundleID ) {
			if ( pid == null || bundleID == -1 )
				return null;
			for ( ID id : ids ) {
				if ( pid.equals(id.pid) && bundleID == id.bundleID )
					return id;
			}
			return null;
		}
		
		// gets the first free id in this list
		int getFreeId() {
			for (int i = 1; i < Integer.MAX_VALUE; i++) {
				boolean used = false;
				for ( ID id : ids )
					if ( id.index == i ) {
						used = true;
						break;
					}
				if ( ! used )
					return i;
			}
			return 0;
		}
		
		void add( ID id ) {
			ids.add(id);
		}
	}

	
	//*** EnumerationManager implementation
	public IDManager( BundleContext context ) {
		trackerCM = new ServiceTracker<>(context, ConfigurationAdmin.class,
				null);
		trackerCM.open();
	}
	
	public void cleanup() {
		if ( trackerCM != null )
			trackerCM.close();
		restore();
	}
	
	Hashtable<String, IDList>getIDTable() {
		if (ids == null) {
			ids = new Hashtable<String, IDList>();
		}
		return ids;
	}
	
	/**
	 * returns the ID for the given uri and pid
	 * @param uri ... must not be null
	 * @param pid ... can be null
	 * @param bundleID ... must be given if pid is != null
	 * @return
	 */
	private ID getID( String uri, String pid, long bundleID ) {
		@SuppressWarnings("hiding")
		IDList ids = getIDList(uri);
		// if pid is given, then look for this special ID
		ID id = null;
		if ( pid != null && bundleID != -1 )
			id = ids.getId(pid, bundleID);

		if ( id == null ) {
			int i = ids.getFreeId();
			id = new ID(pid, bundleID, i);
			ids.add(id);
			// make persistent, only if pid is provided
			if ( pid != null )
				store(uri, id);
		}
		return id;
	}
	
	/**
	 * ensures that there is an IDList for the given uri
	 * @param uri
	 * @return
	 */
	private IDList getIDList( String uri ) {
		@SuppressWarnings("hiding")
		IDList ids = getIDTable().get(uri);
		if ( ids == null ) {
			ids = new IDList();
			getIDTable().put(uri, ids);
		}
		return ids;
	}
	
	/**
	 * returns the unique index for the given uri and pid
	 * @param uri ... the uri for which the id is required
	 * @param pid ... the service.pid of the DataPlugin, can be null
	 * @param bundleID ... the id of the bundle that registered the DataPlugin, must be given if pid is present
	 * @return the index, which is either newly generated or taken from already known mapping (persistency)
	 */
	public int getIndex( String uri, String pid, long bundleID ) {
		if ( uri == null )
			return -1;
		return getID(uri, pid, bundleID).index;
	}
	
	/**
	 * adds a mapping to persistent storage
	 */
	private void store( String uri, ID id ) {
		
		ConfigurationAdmin ca = trackerCM.getService();
		if (ca != null) {
			try {
				if ( getConfiguration(uri, id) == null) {
					Dictionary<String, String> props = new Hashtable<String, String>();
					props.put("uri", uri);
					props.put("plugin.pid", id.pid);
					props.put("bundle.id", "" + id.bundleID);
					props.put("index", "" + id.index);
		
					Configuration config = ca.createFactoryConfiguration(_PID);
					config.update(props);
				}
			}
			catch (IOException x) {
				System.err.println(
						"unable to create new factory configuration for pid: "
								+ _PID + x.getMessage() );
			}
		}
	}

	private Configuration getConfiguration(String uri, ID id) {
		Configuration configuration = null;
		ConfigurationAdmin ca = trackerCM.getService();
		if (ca != null) {
			try {
				StringBuffer filter = new StringBuffer();
				filter.append( "(&" );
				filter.append( "(" + ConfigurationAdmin.SERVICE_FACTORYPID + "=" + _PID + ")");
				filter.append( "(uri=" + uri + ")");
				filter.append( "(plugin.pid=" + id.pid + ")");
				filter.append( "(bundle.id=" + id.bundleID + ")");
				filter.append( "(index=" + id.index + ")");
				filter.append( ")" );
				
				Configuration[] configs = ca.listConfigurations( filter.toString() );
				if ( configs != null && configs.length > 0 )
					configuration = configs[0];
			}
			catch (IOException x) {
				System.err.println(
						"unable to create new factory configuration for pid: "
								+ _PID + x.getMessage());
			}
			catch (InvalidSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return configuration;
	}

	
	/**
	 * restores the list from persistency, old list will be cleared before
	 */
	private void restore() {
		getIDTable().clear();
		Configuration[] configs = null;
		ConfigurationAdmin ca = trackerCM.getService();
		if (ca != null) {

			String filter = "(" + ConfigurationAdmin.SERVICE_FACTORYPID + "="
					+ _PID + ")";
			try {
				configs = ca.listConfigurations(filter);
				for (int i = 0; i < configs.length; i++) {
					String uri = (String) configs[i].getProperties().get( "uri" );
					String pid = (String) configs[i].getProperties().get( "service.pid" );
					int index = -1;
					int bundleID = -1;
					try {
						index = Integer.parseInt((String) configs[i].getProperties().get( "index" ));
						bundleID = Integer.parseInt((String) configs[i].getProperties().get( "bundle.id" ));
					} catch (NumberFormatException e) {
						System.err.println( "NumberformatException in persistent index for: " + uri);
					}
					if ( index > -1 ) {
						getIDList(uri).add( new ID(pid, bundleID, index));
					}
						
				}
			}
			catch (IOException x) {
				System.err.println(
						"unable to read factory configurations for pid: "
								+ _PID + x.getMessage());
			}
			catch (InvalidSyntaxException x) {
				System.err.println(
						"unable to read factory configurations for pid: "
						+ _PID + x.getMessage());
			}
		}
	}
	
}
