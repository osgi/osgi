/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import java.io.*;
import java.util.*;
import org.eclipse.osgi.framework.adaptor.PermissionStorage;
import org.eclipse.osgi.framework.adaptor.core.AbstractFrameworkAdaptor;
import org.eclipse.osgi.framework.adaptor.core.AdaptorMsg;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.internal.core.ConditionalPermissionInfoImpl;
import org.eclipse.osgi.framework.internal.reliablefile.*;
import org.eclipse.osgi.util.NLS;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 * Class to model permission data storage.
 */

public class DefaultPermissionStorage implements PermissionStorage {
	/** Filename used to store the ConditionalPermissions. This name
	 * is relative to permissionDir.*/
	private static final String CONDPERMS = "condPerms"; //$NON-NLS-1$
	/** Directory into which permission data files are stored. */
	protected File permissionDir;

	/** List of permission files: String location => File permission file */
	protected Hashtable permissionFiles;

	/** Default permission data. */
	protected File defaultData;

	/** First permission data format version */
	protected static final int PERMISSIONDATA_VERSION_1 = 1;

	/** Current permission data format version */
	protected static final int PERMISSIONDATA_VERSION = PERMISSIONDATA_VERSION_1;

	/**
	 * Constructor.
	 *
	 * @throws IOException If an error occurs initializing the object.
	 */
	public DefaultPermissionStorage(AbstractFrameworkAdaptor adaptor) throws IOException {
		permissionDir = new File(adaptor.getBundleStoreRootDir(), "permdata"); //$NON-NLS-1$
		permissionFiles = new Hashtable();

		if (!permissionDir.exists() && !permissionDir.mkdirs()) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Unable to create directory: " + permissionDir.getPath()); //$NON-NLS-1$
			}

			throw new IOException(NLS.bind(AdaptorMsg.ADAPTOR_DIRECTORY_CREATE_EXCEPTION, permissionDir)); //$NON-NLS-1$
		}

		defaultData = new File(permissionDir, ".default"); //$NON-NLS-1$

		loadLocations();
	}

	/**
	 * Returns the locations that have permission data assigned to them,
	 * that is, locations for which permission data
	 * exists in persistent storage.
	 *
	 * @return The locations that have permission data in
	 * persistent storage, or <tt>null</tt> if there is no permission data
	 * in persistent storage.
	 * @throws IOException If a failure occurs accessing peristent storage.
	 */
	public synchronized String[] getLocations() throws IOException {
		int size = permissionFiles.size();

		if (size == 0) {
			return null;
		}

		String[] locations = new String[size];

		Enumeration keysEnum = permissionFiles.keys();

		for (int i = 0; i < size; i++) {
			locations[i] = (String) keysEnum.nextElement();
		}

		return locations;
	}

	/**
	 * Gets the permission data assigned to the specified
	 * location.
	 *
	 * @param location The location whose permission data is to
	 * be returned.
	 *
	 * @return The permission data assigned to the specified
	 * location, or <tt>null</tt> if that location has not been assigned any
	 * permission data.
	 * @throws IOException If a failure occurs accessing peristent storage.
	 */
	public synchronized String[] getPermissionData(String location) throws IOException {
		File file;

		if (location == null) {
			file = defaultData;
		} else {
			file = (File) permissionFiles.get(location);

			if (file == null) {
				return null;
			}
		}

		try {
			return readData(file);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	/**
	 * Assigns the specified permission data to the specified
	 * location.
	 *
	 * @param location The location that will be assigned the
	 *                 permissions.
	 * @param data The permission data to be assigned, or <tt>null</tt>
	 * if the specified location is to be removed from persistent storaqe.
	 * @throws IOException If a failure occurs modifying peristent storage.
	 */
	public synchronized void setPermissionData(String location, String[] data) throws IOException {
		File file;

		if (location == null) {
			file = defaultData;

			if (data == null) {
				ReliableFile.delete(defaultData);
			} else {
				save(defaultData, null, data); /* Save the value in persistent storage */
			}
		} else {
			file = (File) permissionFiles.get(location);

			if (data == null) {
				if (file == null) {
					return;
				}

				permissionFiles.remove(location);

				ReliableFile.delete(file);
			} else {
				file = save(file, location, data); /* Save the value in persistent storage */

				permissionFiles.put(location, file);
			}
		}
	}

	/**
	 * Load the locations for which permission data exists.
	 *
	 * @throws IOException If an error occurs reading the files.
	 */
	protected void loadLocations() throws IOException {
		String list[] = ReliableFile.getBaseFiles(permissionDir);
		if (list == null)
			return;
		int len = list.length;

		for (int i = 0; i < len; i++) {
			String name = list[i];

			if (name.endsWith(ReliableFile.tmpExt)) {
				continue;
			}
			if (name.equals(CONDPERMS)) {
				continue;
			}

			File file = new File(permissionDir, name);

			try {
				String location = readLocation(file);

				if (location != null) {
					permissionFiles.put(location, file);
				}
			} catch (FileNotFoundException e) {
				/* the file should have been there */
			}
		}
	}

	/**
	 * Read the location from the specified file.
	 *
	 * @param file File to read the location from.
	 * @return Location from the file or null if the file is unknown.
	 * @throws IOException If an error occurs reading the file.
	 * @throws FileNotFoundException if the data file does not exist.
	 */
	private String readLocation(File file) throws IOException {
		DataInputStream in = new DataInputStream(new ReliableFileInputStream(file));
		try {
			int version = in.readInt();

			switch (version) {
				case PERMISSIONDATA_VERSION_1 : {
					boolean locationPresent = in.readBoolean();

					if (locationPresent) {
						String location = in.readUTF();

						return location;
					}
					break;
				}
				default : {
					throw new IOException(AdaptorMsg.ADAPTOR_STORAGE_EXCEPTION);
				}
			}
		} finally {
			in.close();
		}

		return null;
	}

	/**
	 * Read the permission data from the specified file.
	 *
	 * @param file File to read the permission data from.
	 * @throws IOException If an error occurs reading the file.
	 * @throws FileNotFoundException if the data file does not exist.
	 */
	private String[] readData(File file) throws IOException {
		DataInputStream in = new DataInputStream(new ReliableFileInputStream(file));
		try {
			int version = in.readInt();

			switch (version) {
				case PERMISSIONDATA_VERSION_1 : {
					boolean locationPresent = in.readBoolean();

					if (locationPresent) {
						String location = in.readUTF();
					}

					int size = in.readInt();
					String[] data = new String[size];

					for (int i = 0; i < size; i++) {
						data[i] = in.readUTF();
					}

					return data;
				}
				default : {
					throw new IOException(AdaptorMsg.ADAPTOR_STORAGE_EXCEPTION);
				}
			}
		} finally {
			in.close();
		}
	}

	/**
	 * Save the permission data for the specified location.
	 * This assumes an attempt has been made to load
	 * the specified location just prior to calling save.
	 */
	protected File save(File file, String location, String[] data) throws IOException {
		if (file == null) /* we need to create a filename */{
			file = File.createTempFile("perm", "", permissionDir); //$NON-NLS-1$ //$NON-NLS-2$
			file.delete(); /* delete the empty file */
		}

		int size = data.length;

		DataOutputStream out = new DataOutputStream(new ReliableFileOutputStream(file));

		try {
			out.writeInt(PERMISSIONDATA_VERSION);
			if (location == null) {
				out.writeBoolean(false);
			} else {
				out.writeBoolean(true);
				out.writeUTF(location);
			}
			out.writeInt(size);

			for (int i = 0; i < size; i++) {
				out.writeUTF(data[i]);
			}

		} finally {
			out.close();
		}

		return file;
	}

	/**
	 * Serializes the ConditionalPermissionInfos to CONDPERMS. Serialization is done
	 * by writing out each ConditionalPermissionInfo as a set of ConditionInfos 
	 * followed by PermissionInfos followed by a blank line.
	 * 
	 * @param v the Vector to be serialized that contains the ConditionalPermissionInfos.
	 * @throws IOException
	 * @see org.eclipse.osgi.framework.adaptor.PermissionStorage#serializeConditionalPermissionInfos(Vector)
	 */
	public void serializeConditionalPermissionInfos(Vector v) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(permissionDir, CONDPERMS))));
			Enumeration en = v.elements();
			while (en.hasMoreElements()) {
				ConditionalPermissionInfo cpi = (ConditionalPermissionInfo) en.nextElement();
				ConditionInfo cis[] = cpi.getConditionInfos();
				PermissionInfo pis[] = cpi.getPermissionInfos();
				writer.write('#');
				writer.write(((ConditionalPermissionInfoImpl) cpi).getName());
				writer.newLine();
				for (int i = 0; i < cis.length; i++) {
					writer.write(cis[i].getEncoded());
					writer.newLine();
				}
				for (int i = 0; i < pis.length; i++) {
					writer.write(pis[i].getEncoded());
					writer.newLine();
				}
				writer.newLine();
			}
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	/**
	 * Deserializes the ConditionalPermissionInfos from CONDPERMS and returns the object.
	 * 
	 * @return the deserialized object that was previously passed to serializeCondationalPermissionInfos.
	 * @throws IOException
	 * @see org.eclipse.osgi.framework.adaptor.PermissionStorage#deserializeConditionalPermissionInfos()
	 */
	public Vector deserializeConditionalPermissionInfos() throws IOException {
		BufferedReader reader = null;
		Vector v = new Vector(15);
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(permissionDir, CONDPERMS))));
			String line;
			Vector c = new Vector(3);
			Vector p = new Vector(3);
			String id = null;
			while ((line = reader.readLine()) != null) {
				if (line.length() == 0) {
					ConditionalPermissionInfoImpl cpi;
					cpi = new ConditionalPermissionInfoImpl(id, (ConditionInfo[]) c.toArray(new ConditionInfo[0]), (PermissionInfo[]) p.toArray(new PermissionInfo[0]));
					v.add(cpi);
					c.clear();
					p.clear();
					id = null;
				} else if (line.startsWith("(")) { //$NON-NLS-1$
					p.add(new PermissionInfo(line));
				} else if (line.startsWith("[")) { //$NON-NLS-1$
					c.add(new ConditionInfo(line));
				} else if (line.startsWith("#")) { //$NON-NLS-1$
					id = line.substring(1);
				}
			}
		} catch (FileNotFoundException e) {
			// do nothing return empty vector
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		} finally {
			if (reader != null)
				reader.close();
		}
		return v;
	}
}
