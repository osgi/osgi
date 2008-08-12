/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi (OSGI)
 * Specification may be subject to third party intellectual property rights,
 * including without limitation, patent rights (such a third party may or may
 * not be a member of OSGi). OSGi is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL NOT
 * INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY LOSS OF
 * PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF BUSINESS,
 * OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL, PUNITIVE OR
 * CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS DOCUMENT OR THE
 * INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.cm;

import java.io.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.cm.*;

/**
 * ConfigurationImpl implements a configuration, holds its properties, manages
 * load and save to persistent storage.
 * 
 * @author OSGi Alliance
 * @version $Revision$
 */
public class ConfigurationImpl implements Configuration, Serializable {
	/** Factory pid or null if none */
	protected String			fPid;
	/** Pid of configuration */
	protected String			pid;
	/** Bundle Location of this configuration */
	protected String			location;
	/** Properties of this configuration */
	protected CMDictionary		props;
	/** Indicates if this configuration had been deleted */
	protected transient boolean	deleted;
	/**
	 * Indicates if the Configuration is strongly bound with the bundle owner.
	 * If the location is bound as a result of a ManagedService(Factory)
	 * registration with the proper pid, then the value is false, otherwise -
	 * true.
	 */
	protected transient boolean	stronglyBound;
	private transient String	target;

	/**
	 * Constructs a ConfigurationImpl object, initially setting its active flag
	 * to false.
	 * 
	 * @param fPid Factory Pid, if any, to be associated to the configuration
	 * @param pid Pid to be associated to the configuration
	 * @param location Bundle Location of bundle owner of this configuration
	 */
	public ConfigurationImpl(String fPid, String pid, String location) {
		this.fPid = fPid;
		this.pid = pid;
		this.location = location;
		init();
	}

	/**
	 * Inits the transient fields of the class.
	 */
	protected void init() {
		deleted = false;
		stronglyBound = (location != null);
		if (fPid == null) {
			target = "(|(!(cm.target=*))(cm.target=" + pid + "))";
		}
		else {
			target = "(|(!(cm.target=*))(cm.target=" + fPid + "))";
		}
	}

	/**
	 * Deletes this configuration.
	 * 
	 * @exception IOException if delete fails.
	 * @exception IllegalStateException if configuration already deleted.
	 * @throws SecurityException If the caller does not have <code>ConfigurationPermission[SET]</code>.
	 */
	public void delete() throws IOException {
		checkIfDeleted();
		synchronized (ServiceAgent.storage) {
			ServiceAgent.storage.deleteConfig(this);
			ServiceReference sRef = ServiceAgent.searchForService(this, false);
			if (sRef != null) {
				CMEventManager.addEvent(new CMEvent(this, null,
						CMEvent.DELETED, sRef));
			}
			deleted = true;
		}
		// notify ConfigurationListeners of delete
		CMEventManager.addEvent(new ConfigurationEvent(CMActivator.cmReg.getReference(), 
				ConfigurationEvent.CM_DELETED, fPid, pid));
	}

	private void checkIfDeleted() {
		if (deleted) {
			throw new IllegalStateException("Configuration already deleted!");
		}
	}

	/**
	 * Gets this configuration location.
	 * 
	 * @return bundle location of configured bundle.
	 * @exception SecurityException When caller has no admin permission
	 * @exception IllegalStateException if configuration already deleted.
	 * @throws SecurityException If the caller does not have <code>ConfigurationPermission[REBIND]</code>.
	 */
	public String getBundleLocation() throws SecurityException {
		checkIfDeleted();
		ConfigurationAdminImpl.checkPermission();
		return location;
	}

	/**
	 * Gets the factoryPid of this configuration, if any.
	 * 
	 * @return factory pid or null.
	 * @exception IllegalStateException if configuration already deleted.
	 */
	public String getFactoryPid() {
		checkIfDeleted();
		return fPid;
	}

	/**
	 * Gets pid of this configuration.
	 * 
	 * @return pid - not null
	 * @exception IllegalStateException if configuration already deleted.
	 */
	public String getPid() {
		checkIfDeleted();
		return pid;
	}

	/**
	 * Gets a copy of the properties of the configuration.
	 * 
	 * @return a copy of props or null if not set, yet.
	 * @exception IllegalStateException if configuration already deleted.
	 */
	public Dictionary getProperties() {
		checkIfDeleted();
		return (props != null) ? (Dictionary) props.clone() : null;
	}

	/**
	 * Associates this configuration with a bundle location.
	 * 
	 * @exception SecurityException When caller has no admin permission
	 * @exception IllegalStateException if configuration already deleted.
	 * @throws SecurityException If the caller does not have <code>ConfigurationPermission[REBIND]</code>.
	 */
	public void setBundleLocation(String bundleLocation)
			throws SecurityException {
		checkIfDeleted();
		ConfigurationAdminImpl.checkPermission();
		setLocation(bundleLocation, true);
		stronglyBound = (bundleLocation != null);
	}

	/**
	 * Updates the configuration and target services with current properties.
	 * 
	 * @exception IOException if storage error occurs.
	 * @exception IllegalStateException if configuration already deleted.
	 * @throws SecurityException If the caller does not have <code>ConfigurationPermission[SET]</code>.
	 */
	public void update() throws IOException {
		checkIfDeleted();
		callBackManagedService(null, null);
	}

	/**
	 * Updates configuration with new properties. Activates configuration when
	 * invoked for the first time.
	 * 
	 * @param properties configuration set to associate with
	 * 
	 * @exception IOException if storage fails.
	 * @exception IllegalStateException if configuration already deleted.
	 * @exception IllegalArgumentException if properties contain wrong data
	 *            types.
	 * @throws SecurityException If the caller does not have <code>ConfigurationPermission[SET]</code>.
	 */
	public void update(Dictionary properties) throws IOException,
			IllegalArgumentException {
		checkIfDeleted();
		if (properties == null) {
			throw new NullPointerException();
		}
		if (!isCorrectType(properties)) {
			throw new IllegalArgumentException(
					"Properties contain incorrect type or duplicate case key! " + properties );
		}
		callBackManagedService(null, properties);

		// notify ConfigurationListeners of update
		CMEventManager.addEvent(new ConfigurationEvent(CMActivator.cmReg.getReference(), 
				ConfigurationEvent.CM_UPDATED, fPid, pid));
	}

	/**
	 * Two configurations are considered equal when their pids are equal.
	 * 
	 * @param o Another configuration object is expected
	 * @return true if the parameter is a Configuration object with the same
	 *         pid; false otherwise
	 */
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Configuration))
			return false;
		return ((Configuration) o).getPid().equals(pid);
	}

	/**
	 * Two configurations with the same pid have the same hash code.
	 * 
	 * @return a hash code value for this object
	 */
	public int hashCode() {
		return pid.hashCode();
	}

	/**
	 * Sets bundle location for the configuration.
	 * 
	 * @param loc new location, may be null
	 * @param toStore indicates if the location must be stored persistently
	 */
	protected void setLocation(String loc, boolean toStore) {
		location = loc;
		if (toStore) {
			try {
				synchronized (ServiceAgent.storage) {
					ServiceAgent.storage.storeConfig(this, false);
				}
			}
			catch (IOException e) {
				Log.log(1, "[CM]IOException while setting bundle location!", e);
			}
		}
	}

	/**
	 * Processes an update call back to a ManagedService(Factory). If the new
	 * properties are not null, after the automatic properties are added, they
	 * are persistently stored. Null properties are not stored. If there is a
	 * target ManagedService(Factory) present in the service registry, the
	 * Configuration Plugins are informed and the target service is updated.
	 * 
	 * @param sRef reference to a ManagedService(Factory). If null, the method
	 *        searches for a proper ManagedService(Factory).
	 * 
	 * @param newProperties properties which will be used for the update. If
	 *        null, the current configuration properties are used.
	 * @exception IOException If storage operation fails with I/O error
	 */
	protected void callBackManagedService(ServiceReference sRef,
			Dictionary newProperties) throws IOException {
		synchronized (ServiceAgent.storage) {
			if (sRef == null) {
				sRef = ServiceAgent.searchForService(this, false);
			}
			if (newProperties != null) {
				if (props == null) {
					props = new CMDictionary();
				}
				else {
					props.clear();
				}
				props.copyFrom(newProperties);
				loadServiceProperties();
				ServiceAgent.storage.storeConfig(this, false);
			}
		}
		if (sRef != null) {
			//correctness check will not be made as plugins are trusted
			// entities
			//they should not corrupt properties
			CMDictionary pprops = (props != null) ? (CMDictionary) props
					.clone() : null;
			informPlugins("(&" + target
					+ "(&(service.cmRanking=*)(!(service.cmRanking>=0))))",
					sRef, pprops);
			informPlugins(
					"(&"
							+ target
							+ "(|(!(service.cmRanking=*))(&(service.cmRanking>=0)(service.cmRanking<=1000))))",
					sRef, pprops);
			informPlugins("(&" + target
					+ "(&(service.cmRanking=*)(!(service.cmRanking<=1000))))",
					sRef, pprops);
			CMEventManager.addEvent(new CMEvent(this, pprops, CMEvent.UPDATED,
					sRef));
		}
	}

	private static boolean isBasicClass(String name) {
		if (name.equals(String.class.getName())
				|| name.equals(Integer.class.getName())
				|| name.equals(Long.class.getName())
				|| name.equals(Float.class.getName())
				|| name.equals(Double.class.getName())
				|| name.equals(Byte.class.getName())
				|| name.equals(Short.class.getName())
				|| name.equals(Character.class.getName())
				|| name.equals(Boolean.class.getName()) || name.equals("I")
				|| name.equals("J") || name.equals("C") || name.equals("S")
				|| name.equals("D") || name.equals("F") || name.equals("B")
				|| name.equals("Z")) {
			return true;
		}
		return false;
	}

	private static boolean isCorrectArray(String className, Object arr) {
		int i = className.indexOf('L');
		if (i > 0) {
			/*
			 * look for L - specifying object arr - - they look like this:
			 * "[[[Ljava.lang.String;"
			 */
			className = className.substring(i + 1, className.length() - 1);
		}
		else {
			/* primitive arrays look like this: "[[[I" */
			className = className.substring(className.length() - 1);
		}
		if (className.equals(Vector.class.getName())) {
			Vector[] v = (Vector[]) arr;
			if (v.length > 0) {
				for (i = 0; i < v.length; i++) {
					if (v[i] != null && !isCorrectVector(v[i]))
						return false;
				}
			}
			return true;
		}
		return isBasicClass(className);
	}

	private static boolean isCorrectType(Dictionary props) {
		Enumeration e = props.keys();
		String key;
		Set			keys = new HashSet();
		while (e.hasMoreElements()) {
			try {
				key = (String) e.nextElement();
				if ( !keys.add(key.toLowerCase()))
					return false;
			}
			catch (ClassCastException cce) {
				return false;
			}
			if (!isCorrectObject(props.get(key)))
				return false;
		}
		return true;
	}

	private static boolean isCorrectObject(Object o) {
		if (o != null) {
			Class c = o.getClass();
			if (!isBasicClass(c.getName())
					&& (!c.isArray() || !isCorrectArray(c.getName(), o))
					&& (!c.equals(Vector.class) || !isCorrectVector((Vector) o))) {
				return false;
			}
		}
		return true;
	}

	private static boolean isCorrectVector(Vector v) {
		Enumeration e = v.elements();
		while (e.hasMoreElements()) {
			if (!isCorrectObject(e.nextElement()))
				return false;
		}
		return true;
	}

	private void loadServiceProperties() {
		if (props == null)
			return;
		props.put(Constants.SERVICE_PID, pid);
		if (fPid != null) {
			props.put(ConfigurationAdmin.SERVICE_FACTORYPID, fPid);
		}
	}

	private void informPlugins(String filter, ServiceReference msRef,
			CMDictionary props) {
		try {
			ServiceReference[] sRefs = ServiceAgent.bc.getServiceReferences(
					ConfigurationPlugin.class.getName(), filter);
			if (sRefs == null)
				return;
			if (sRefs.length > 1) {
				sRefs = sort(sRefs);
			}
			ConfigurationPlugin plugin;
			for (int i = 0; i < sRefs.length; i++) {
				try {
					plugin = (ConfigurationPlugin) ServiceAgent.bc
							.getService(sRefs[i]);
					plugin.modifyConfiguration(msRef, props);
					ServiceAgent.bc.ungetService(sRefs[i]);
				}
				catch (Throwable th) {
					Log
							.log(
									1,
									"[CM]A ConfigurationPlugin failed to modify a configuration!",
									th);
				}
			}
		}
		catch (InvalidSyntaxException ise) {
			Log
					.log(
							1,
							"[CM]Unexpected error, while calling ConfigurationPlugins!",
							ise);
		}
	}

	private ServiceReference[] sort(ServiceReference[] sRefs) {
		int ranking;
		int minRank = 0;
		int maxRank = 1000;
		Vector result = new Vector(sRefs.length);
		for (int i = 0; i < sRefs.length; i++) {
			ranking = getRanking(sRefs[i]);
			if (result.isEmpty()) {
				result.addElement(sRefs[i]);
				minRank = ranking;
				maxRank = ranking;
			}
			else
				if (ranking <= minRank) {
					result.insertElementAt(sRefs[i], 0);
					minRank = ranking;
				}
				else
					if (ranking >= maxRank) {
						result.addElement(sRefs[i]);
						maxRank = ranking;
					}
					else {
						for (int j = 1; j < i; j++) {
							if (ranking <= getRanking((ServiceReference) result
									.elementAt(j))) {
								result.insertElementAt(sRefs[i], j);
								break;
							}
						}
					}
		}
		ServiceReference[] sRefss = new ServiceReference[result.size()];
		result.copyInto(sRefss);
		return sRefss;
	}

	private int getRanking(ServiceReference sRef) {
		try {
			Integer temp = (Integer) sRef.getProperty("service.cmRanking");
			if (temp != null)
				return temp.intValue();
		}
		catch (ClassCastException cce) {
		}
		return 0;
	}
}