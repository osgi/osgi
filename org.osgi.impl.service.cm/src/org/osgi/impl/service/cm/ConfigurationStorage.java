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

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import org.osgi.framework.BundleContext;

/**
 * The class represents a friendly wrapper of DataBase class, providing methods
 * for getting, storing, deleting configurations.
 * 
 * @author OSGi Alliance
 * @version $Revision$
 */
public class ConfigurationStorage extends DataBase {
	/* Holds all configuration objects */
	protected static Hashtable	configs;
	/* Holds all factory configurations */
	protected static Hashtable	configsF;
	private final static String	GEN_PID	= "CM_GENERATED_PID_";

	/**
	 * Inits the storage.
	 * 
	 * @param bc to be passed to DataBase,
	 * @exception IOException if an I/O error occurs
	 */
	public ConfigurationStorage(BundleContext bc) throws IOException {
		super(bc);
		configs = new Hashtable();
		configsF = new Hashtable();
	}

	/**
	 * Loads configuration data from database to memory.
	 * 
	 * @exception IOException if an I/O error occurs.
	 */
	protected void load() throws IOException {
		super.load();
		Enumeration pids = dataInfo.keys();
		String nextKey;
		byte[] configBytes;
		ConfigurationImpl config;
		while (pids.hasMoreElements()) {
			nextKey = (String) pids.nextElement();
			if (!nextKey.equals(CM_NEXT_ID)) {
				configBytes = read(nextKey);
				config = makeConfig(configBytes);
				configs.put(nextKey, config);
				if (config.fPid != null) {
					putFactoryConfig(config);
				}
			}
		}
	}

	/**
	 * Gets the next uniquely generated pid for factory Configurations.
	 * 
	 * @return generated pid.
	 * @exception IOException if an I/O error occurs.
	 */
	protected String getNextPID() throws IOException {
		return GEN_PID + getNextId();
	}

	/**
	 * Persistently deletes an Configuration.
	 * 
	 * @param config object to delete.
	 * @exception IOException if an I/O error occurs.
	 */
	protected void deleteConfig(ConfigurationImpl config) throws IOException {
		if (config.fPid != null) {
			Vector factoryVector = (Vector) configsF.get(config.fPid);
			if (factoryVector != null) {
				factoryVector.removeElement(config);
				if (factoryVector.size() < 1) {
					configsF.remove(config.fPid);
				}
			}
		}
		configs.remove(config.pid);
		delete(config.pid);
	}

	/**
	 * Stores the config in database. The object is serialized and passed to
	 * DataBase to be persistently stored.
	 * 
	 * @param config object to store.
	 * @param create indicates if config is newly created.
	 * @exception IOException if an I/O error occurs.
	 */
	protected void storeConfig(ConfigurationImpl config, boolean create)
			throws IOException {
		if (create) {
			if (config.fPid != null) {
				putFactoryConfig(config);
			}
			configs.put(config.pid, config);
		}
		store(config.pid, makeByteArray(config));
	}

	private void putFactoryConfig(ConfigurationImpl config) {
		Vector factoryVector = (Vector) configsF.get(config.fPid);
		if (factoryVector == null) {
			factoryVector = new Vector();
		}
		factoryVector.addElement(config);
		configsF.put(config.fPid, factoryVector);
	}

	private ConfigurationImpl makeConfig(byte[] configBytes) {
		ConfigurationImpl config = null;
		ByteArrayInputStream barr = new ByteArrayInputStream(configBytes);
		try {
			ObjectInputStream osi = new ObjectInputStream(barr);
			config = (ConfigurationImpl) osi.readObject();
			config.init();
			osi.close();
		}
		catch (StreamCorruptedException sce) {
			Log.log(1, "[CM]Error while trying to read Configuration object.",
					sce);
		}
		catch (IOException ioe) {
			Log.log(1, "[CM]Error while trying to read Configuration object.",
					ioe);
		}
		catch (ClassNotFoundException cnfe) {
			Log.log(1, "[CM]Error while trying to read Configuration object.",
					cnfe);
		}
		finally {
			try {
				barr.close();
			}
			catch (IOException ioe) {
				/* ignore */
			}
		}
		return config;
	}

	private byte[] makeByteArray(ConfigurationImpl config) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(config);
		return baos.toByteArray();
	}

	/**
	 * Closes data base, and clears hashtables.
	 * 
	 * @exception IOException if db fails to close.
	 */
	protected void close() throws IOException {
		super.close();
		configs.clear();
		configsF.clear();
	}
}