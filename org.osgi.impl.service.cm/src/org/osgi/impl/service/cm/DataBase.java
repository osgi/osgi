/*
 * $Id$
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
import java.security.*;
import java.util.*;
import org.osgi.framework.BundleContext;

/**
 * The class managed a simple indexed database for store of Configurations.
 * 
 * @author OSGi Alliance
 * @version $Revision$
 */
public class DataBase {
	/* The name against which is written the next id number */
	protected final static String	CM_NEXT_ID			= "CM_NEXT_ID";
	/* Holds Configuration data: pid and serialized data */
	protected Hashtable				dataInfo;
	private final static String		INDEX_NAME			= "index.db";
	private final static String		DATA_NAME			= "data.db";
	private final static int		CM_ID_LENGTH		= 18;
	private final static int		INFO_BLOCK_LENGTH	= 16;
	private final static byte		INFO				= 0;
	private final static byte		FREE				= 1;
	private final static byte		TYPE_BYTE_ARR		= 0;
	private final static byte		TYPE_LONG			= 1;
	private RandomAccessFile		indexRAF;
	private RandomAccessFile		dataRAF;
	private File					indexF;
	private File					dataF;
	private boolean					needsDefragment;
	private BundleContext			bc;

	/**
	 * Initializes the DataBase: its files and variables.
	 * 
	 * @param bc necessary to get data files from.
	 * @exception IOException if an I/O error occurs.
	 */
	public DataBase(BundleContext bc) throws IOException {
		this.bc = bc;
		indexF = bc.getDataFile(INDEX_NAME);
		dataF = bc.getDataFile(DATA_NAME);
		indexRAF = new RandomAccessFile(indexF, "rw");
		dataRAF = new RandomAccessFile(dataF, "rw");
		dataInfo = new Hashtable();
		needsDefragment = false;
	}

	/**
	 * Loads the indexed data. Indexed data holds pid and address of config
	 * data.
	 * 
	 * @exception IOException if an I/O error occurs while reading data.
	 */
	protected void load() throws IOException {
		if (indexF.exists()) {
			byte tag = (byte) indexRAF.read();
			while (tag != -1) {
				if (tag == INFO) {
					InfoBlock info = readInfo();
					dataInfo.put(info.key, info);
				}
				if (tag == FREE) {
					needsDefragment = true;
					indexRAF
							.seek(indexRAF.getFilePointer() + INFO_BLOCK_LENGTH);
				}
				tag = (byte) indexRAF.read();
			}
			if (needsDefragment) {
				defragment();
			}
		}
	}

	/**
	 * Deletes the data, related with the key passed.
	 * 
	 * @param key a pid, whose Configuration-related data must be deleted.
	 * @exception IOException if an I/O error occurs.
	 */
	protected void delete(String key) throws IOException {
		InfoBlock info = (InfoBlock) dataInfo.get(key);
		dataInfo.remove(key);
		try {
			final InfoBlock tempInfo = info;
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws IOException {
					indexRAF.seek(tempInfo.indexAddr);
					indexRAF.write(FREE);
					return null;
				}
			});
		}
		catch (PrivilegedActionException pae) {
			throw (IOException) pae.getException();
		}
	}

	/**
	 * Stores the config data against the key.
	 * 
	 * @param key pid of Configuration
	 * @param data serialized Configuration
	 * @exception IOException if an I/O error occurs.
	 */
	protected void store(String key, byte[] data) throws IOException {
		InfoBlock info = (InfoBlock) dataInfo.get(key);
		if (info == null) {
			info = new InfoBlock();
			info.key = key;
			info.keyLength = key.length();
			dataInfo.put(info.key, info);
		}
		info.blockLength = data.length + info.keyLength;
		writeInfo(info, data, TYPE_BYTE_ARR);
	}

	/**
	 * Closes the files, in which data is stored.
	 * 
	 * @exception IOException if an I/O error occurs.
	 */
	protected void close() throws IOException {
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws IOException {
					indexRAF.close();
					dataRAF.close();
					return null;
				}
			});
		}
		catch (PrivilegedActionException pae) {
			throw (IOException) pae.getException();
		}
	}

	/**
	 * Returns a long, which is the index of the unique pid, generated by CM to
	 * Factory Configurations.
	 * 
	 * @return a non-negative long.
	 * @exception IOException if an I/O error occurs.
	 */
	protected long getNextId() throws IOException {
		InfoBlock info = (InfoBlock) dataInfo.get(CM_NEXT_ID);
		long toReturn[] = null;
		if (info != null) {
			try {
				final InfoBlock tempInfo = info;
				toReturn = (long[]) AccessController
						.doPrivileged(new PrivilegedExceptionAction() {
							public Object run() throws IOException {
								return readData(tempInfo, TYPE_LONG);
							}
						});
			}
			catch (PrivilegedActionException pae) {
				throw (IOException) pae.getException();
			}
			++toReturn[0];
			info.blockLength = CM_ID_LENGTH;
		}
		else {
			info = new InfoBlock();
			info.key = CM_NEXT_ID;
			info.keyLength = CM_NEXT_ID.length();
			info.blockLength = CM_ID_LENGTH;
			dataInfo.put(CM_NEXT_ID, info);
			toReturn = new long[1];
			toReturn[0] = 0;
		}
		writeInfo(info, toReturn, TYPE_LONG);
		return toReturn[0];
	}

	/**
	 * Reads the data, related to this key.
	 * 
	 * @param key Configuration's pid.
	 * @return byte array - serialized Configuration.
	 * @exception IOException if an I/O error occurs.
	 */
	protected byte[] read(String key) throws IOException {
		if (dataInfo.containsKey(key)) {
			return (byte[]) readData((InfoBlock) dataInfo.get(key),
					TYPE_BYTE_ARR);
		}
		else {
			return null;
		}
	}

	/**
	 * Method used by load.
	 */
	private InfoBlock readInfo() throws IOException {
		InfoBlock info = new InfoBlock();
		info.indexAddr = indexRAF.getFilePointer() - 1;
		info.blockLength = indexRAF.readInt();
		info.keyLength = indexRAF.readInt();
		info.address = indexRAF.readLong();
		if (info.keyLength < 0)
			throw new IOException("Negative Name Length");
		byte[] barr = new byte[info.keyLength];
		dataRAF.seek(info.address);
		dataRAF.read(barr);
		info.key = new String(barr);
		return info;
	}

	private void defragment() throws IOException {
		String newIndexName = "newindex.db";
		String newDataName = "newdata.db";
		String tempIndexName = "tmpindex.db";
		String tempDataName = "tmpdata.db";
		try {
			File newIndexF = bc.getDataFile(newIndexName);
			File newDataF = bc.getDataFile(newDataName);
			if (newIndexF.exists()) {
				newIndexF.delete();
			}
			if (newDataF.exists()) {
				newDataF.delete();
			}
			RandomAccessFile newIndex = new RandomAccessFile(newIndexF, "rw");
			RandomAccessFile newData = new RandomAccessFile(newDataF, "rw");
			Enumeration infoEnum = dataInfo.elements();
			while (infoEnum.hasMoreElements()) {
				InfoBlock info = (InfoBlock) infoEnum.nextElement();
				newIndex.write(INFO);
				writeInfo(info, newIndex, newData);
			}
			indexRAF.close();
			dataRAF.close();
			newIndex.close();
			newData.close();
			File temp1 = bc.getDataFile(tempIndexName);
			File temp2 = bc.getDataFile(tempDataName);
			indexF.renameTo(temp1);
			dataF.renameTo(temp2);
			newIndexF.renameTo(bc.getDataFile(INDEX_NAME));
			newDataF.renameTo(bc.getDataFile(DATA_NAME));
			temp1.delete();
			temp2.delete();
		}
		catch (IOException ioe) {
			//recover block:
			File temp1 = bc.getDataFile(tempIndexName);
			File temp2 = bc.getDataFile(tempDataName);
			if (temp1.exists()) {
				indexF = temp1;
			}
			else {
				indexF = bc.getDataFile(INDEX_NAME);
			}
			if (temp2.exists()) {
				dataF = temp2;
			}
			else {
				dataF = bc.getDataFile(DATA_NAME);
			}
			indexRAF = new RandomAccessFile(indexF, "rw");
			dataRAF = new RandomAccessFile(dataF, "rw");
			Log.log(1, "[CM]Defragmentation failed, but database recovered!",
					ioe);
		}
		indexF = bc.getDataFile(INDEX_NAME);
		dataF = bc.getDataFile(DATA_NAME);
		indexRAF = new RandomAccessFile(indexF, "rw");
		dataRAF = new RandomAccessFile(dataF, "rw");
	}

	//for DEFRAGMENT ONLY
	private void writeInfo(InfoBlock info, RandomAccessFile newIndex,
			RandomAccessFile newData) throws IOException {
		byte[] data = new byte[info.blockLength];
		dataRAF.seek(info.address);
		dataRAF.read(data);
		info.address = newData.getFilePointer();
		newData.write(data);
		info.indexAddr = newIndex.getFilePointer() - 1;
		newIndex.writeInt(info.blockLength);
		newIndex.writeInt(info.keyLength);
		newIndex.writeLong(info.address);
	}

	private void writeInfo(InfoBlock info, Object data, byte type)
			throws IOException {
		try {
			final InfoBlock tempInfo = info;
			final byte finaltype = type;
			final Object tempData = data;
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws IOException {
					tempInfo.address = dataRAF.length();
					dataRAF.seek(tempInfo.address);
					dataRAF.write(tempInfo.key.getBytes());
					if (finaltype == TYPE_BYTE_ARR) {
						dataRAF.write((byte[]) tempData);
					}
					else
						if (finaltype == TYPE_LONG) {
							dataRAF.writeLong(((long[]) tempData)[0]);
						}
					if (tempInfo.indexAddr == -1) {
						tempInfo.indexAddr = indexRAF.length();
						indexRAF.seek(tempInfo.indexAddr);
						indexRAF.write(INFO);
					}
					else {
						indexRAF.seek(tempInfo.indexAddr + 1);
					}
					indexRAF.writeInt(tempInfo.blockLength);
					indexRAF.writeInt(tempInfo.keyLength);
					indexRAF.writeLong(tempInfo.address);
					return null;
				}
			});
		}
		catch (PrivilegedActionException pae) {
			throw (IOException) pae.getException();
		}
	}

	private Object readData(InfoBlock info, byte type) throws IOException {
		dataRAF.seek(info.address + info.keyLength);
		if (type == TYPE_BYTE_ARR) {
			byte[] data = new byte[info.blockLength - info.keyLength];
			dataRAF.read(data);
			return data;
		}
		else {
			long[] data = new long[1];
			data[0] = dataRAF.readLong();
			return data;
		}
	}

	class InfoBlock {
		protected long		indexAddr	= -1;
		protected int		blockLength;		//+4
		protected int		keyLength;			//+4
		protected long		address;			//+8
		protected String	key;
	}
}