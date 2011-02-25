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
	private ServiceTracker trackerCM;

	/**
	 * holder class for an index/pid combination 
	 */
	class ID {
		int index;
		String pid;
		public ID( String pid, int index ) {
			this.pid = pid;
			this.index = index;
		}
		public String toString() {
			return " pid: " + pid + "  id: " + index;
		}
	}
	
	/**
	 * helper class for maintaining the list of ID's for a given uri
	 * @author steffen
	 *
	 */
	class IDList {
		Vector<ID> ids = new Vector<ID>();
		
		ID getId( String pid ) {
			if ( pid == null )
				return null;
			for ( ID id : ids ) {
				if ( pid.equals(id.pid))
					return id;
			}
			return null;
		}
		
		// gets the first free id in this list
		int getFreeId() {
			for (int i = 0; i < Integer.MAX_VALUE; i++) {
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
		trackerCM = new ServiceTracker(context, ConfigurationAdmin.class.getName(), null);
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
	 * @return
	 */
	private ID getID( String uri, String pid ) {
		IDList ids = getIDList(uri);
		// if pid is given, then look for this special ID
		ID id = null;
		if ( pid != null ) {
			id = ids.getId(pid);
		}
		if ( id == null ) {
			int i = ids.getFreeId();
			id = new ID(pid, i);
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
		IDList ids = (IDList) getIDTable().get(uri);
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
	 * @return the index, which is either newly generated or taken from already known mapping (persistency)
	 */
	public int getIndex( String uri, String pid ) {
		if ( uri == null )
			return -1;
		return getID(uri, pid).index;
	}
	
	/**
	 * adds a mapping to persistent storage
	 */
	private void store( String uri, ID id ) {
		
		ConfigurationAdmin ca = (ConfigurationAdmin) trackerCM.getService();
		if (ca != null) {
			try {
				if ( getConfiguration(uri, id) == null) {
					Dictionary<String, String> props = new Hashtable<String, String>();
					props.put("uri", uri);
					props.put("service.pid", id.pid);
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
		ConfigurationAdmin ca = (ConfigurationAdmin) trackerCM.getService();
		if (ca != null) {
			try {
				StringBuffer filter = new StringBuffer();
				filter.append( "(&" );
				filter.append( "(" + ConfigurationAdmin.SERVICE_FACTORYPID + "=" + _PID + ")");
				filter.append( "(uri=" + uri + ")");
				filter.append( "(service.pid=" + id.pid + ")");
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
		ConfigurationAdmin ca = (ConfigurationAdmin) trackerCM.getService();
		if (ca != null) {

			String filter = "(" + ConfigurationAdmin.SERVICE_FACTORYPID + "="
					+ _PID + ")";
			try {
				configs = ca.listConfigurations(filter);
				for (int i = 0; i < configs.length; i++) {
					String uri = (String) configs[i].getProperties().get( "uri" );
					String pid = (String) configs[i].getProperties().get( "service.pid" );
					int index = -1;
					try {
						index = Integer.parseInt((String) configs[i].getProperties().get( "index" ));
					} catch (NumberFormatException e) {
						System.err.println( "NumberformatException in persistent index for: " + uri);
					}
					if ( index > -1 ) {
						getIDList(uri).add( new ID(pid, index));
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
