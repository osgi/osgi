/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.reliablefile;

import java.io.*;
import java.util.Hashtable;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * ReliableFile class used by ReliableFileInputStream and ReliableOutputStream.
 * This class encapsulates all the logic for reliable file support.
 *
 */
public class ReliableFile {
	protected static final byte identifier1[] = {'.', 'c', 'r', 'c'};
	protected static final byte identifier2[] = {'.', 'v', '1', '\n'};

	/**
	 * Extension of tmp file used during writing.
	 * A reliable file with this extension should
	 * never be directly used.
	 */
	public static final String tmpExt = ".tmp"; //$NON-NLS-1$

	/**
	 * Extension of previous generation of the reliable file.
	 * A reliable file with this extension should
	 * never be directly used.
	 */
	public static final String oldExt = ".bak"; //$NON-NLS-1$

	/**
	 * Extension of next generation of the reliable file.
	 * A reliable file with this extension should
	 * never be directly used.
	 */
	public static final String newExt = ".new"; //$NON-NLS-1$

	/** List of active ReliableFile objects: File => ReliableFile */
	private static Hashtable files;

	static {
		files = new Hashtable(30); /* initialize files */
	}

	/** File object for original file */
	private File orgFile;

	/** File object for the temporary output file */
	private File tmpFile;

	/** File object for old data file */
	private File oldFile;

	/** File object for file containing new data */
	private File newFile;

	/** true if this object is open for read or write */
	private boolean locked;

	/** use code of this object. when zero this object must be removed from files */
	private int use;

	/** ReliableFile version number, unknown until a call to getInputStream() or
	 getOutputStream() methods */
	private static final int VERSION_UNKNOWN = 0;
	private static final int VERSION_PLAINTEXT = 1;
	private static final int VERSION_2 = 2;
	private int version;

	/** Checksum of current file, unknown until a call to getInputStream() or
	 getOutputStream() methods */
	Checksum appendCrc;

	public static final String MINIMUM_AGING_INTERVAL_KEY = "reliablefile.minimumAgingInterval"; //$NON-NLS-1$
	private static final int AGING_INTERVAL_DEFAULT = 2000; // 2 seconds
	private static int agingInterval = -1;

	/**
	 * ReliableFile object factory. This method is called by ReliableFileInputStream
	 * and ReliableFileOutputStream to get a ReliableFile object for a target file.
	 * If the object is in the cache, the cached copy is returned.
	 * Otherwise a new ReliableFile object is created and returned.
	 * The use count of the returned ReliableFile object is incremented.
	 *
	 * @param name Name of the target file.
	 * @return A ReliableFile object for the target file.
	 * @throws IOException If the target file is a directory.
	 */
	static ReliableFile getReliableFile(String name) throws IOException {
		return getReliableFile(new File(name));
	}

	/**
	 * ReliableFile object factory. This method is called by ReliableFileInputStream
	 * and ReliableFileOutputStream to get a ReliableFile object for a target file.
	 * If the object is in the cache, the cached copy is returned.
	 * Otherwise a new ReliableFile object is created and returned.
	 * The use count of the returned ReliableFile object is incremented.
	 *
	 * @param file File object for the target file.
	 * @return A ReliableFile object for the target file.
	 * @throws IOException If the target file is a directory.
	 */
	static ReliableFile getReliableFile(File file) throws IOException {
		if (file.isDirectory()) {
			throw new FileNotFoundException("file is a directory"); //$NON-NLS-1$
		}

		synchronized (files) {
			ReliableFile reliable = (ReliableFile) files.get(file);

			if (reliable == null) {
				reliable = new ReliableFile(file);

				files.put(file, reliable);
			}

			reliable.use++;

			return reliable;
		}
	}

	/**
	 * Decrement this object's use count. If the use count
	 * drops to zero, remove this object from the cache.
	 *
	 */
	private void release() {
		synchronized (files) {
			use--;

			if (use <= 0) {
				files.remove(orgFile);
			}
		}
	}

	/**
	 * Private constructor used by the static getReliableFile factory methods.
	 *
	 * @param file File object for the target file.
	 */
	private ReliableFile(File file) {
		String name = file.getPath();

		orgFile = file;
		// Added a random number to the filename to avoid any chance of corruption
		//  if someone should create multiple writes to the same file. -PAL
		tmpFile = new File(name + "." + new Random().nextInt(0x1000000) + tmpExt); //$NON-NLS-1$
		oldFile = new File(name + oldExt);
		newFile = new File(name + newExt);
		use = 0;
		locked = false;
		version = VERSION_UNKNOWN;
		appendCrc = null;

		if (agingInterval == -1) {
			agingInterval = AGING_INTERVAL_DEFAULT;
			String value = System.getProperty(MINIMUM_AGING_INTERVAL_KEY);
			if (value != null) {
				try {
					int i = Integer.parseInt(value);
					if (i >= 0)
						agingInterval = i;
				} catch (NumberFormatException nfe) {
					System.err.println("Invalid property value for key \"" + MINIMUM_AGING_INTERVAL_KEY + "\"."); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}

	/**
	 * Recovers the target file, if necessary, and returns an InputStream
	 * object for reading the target file.
	 *
	 * @return An InputStream object which can be used to read the target file.
	 * @throws IOException If an error occurs preparing the file.
	 */
	synchronized InputStream getInputStream() throws IOException {
		try {
			lock();
		} catch (IOException e) {
			/* the lock request failed; decrement the use count */
			release();

			throw e;
		}

		try {
			BufferedInputStream bis = recoverFile();

			if (bis == null)
				return new FileInputStream(orgFile.getPath());
			else
				return bis; // don't double read file from storage
		} catch (IOException e) {
			unlock();

			release();

			throw e;
		}
	}

	/**
	 * Close the target file for reading.
	 *
	 * @throws IOException If an error occurs closing the file.
	 */
	/* This method does not need to be synchronized if it only calls release. */
	void closeInputFile() throws IOException {
		unlock();
		release();
	}

	/**
	 * Recovers the target file, if necessary, and returns an OutputStream
	 * object for writing the target file.
	 *
	 * @return An OutputStream object which can be used to write the target file.
	 * @throws IOException If an error occurs preparing the file.
	 */
	synchronized OutputStream getOutputStream(boolean append) throws IOException {
		try {
			lock();
		} catch (IOException e) {
			/* the lock request failed; decrement the use count */
			release();

			throw e;
		}

		try {
			// always recover file - PAL
			//  this way we will never have a invalid file as a .bak file
			BufferedInputStream bis = recoverFile();
			// Don't forget to close the input stream, we won't need it any more
			if (bis != null)
				bis.close();
		} catch (IOException e) {
			if (append) {
				unlock();

				release();

				throw e;
			}
			// otherwise, go on
		}

		try {
			if (append) {
				if (orgFile.exists()) {
					int truncate;
					if (version == VERSION_2)
						truncate = 16;
					else
						truncate = 0;
					cp(orgFile, tmpFile, truncate);
				} else {
					if (tmpFile.exists())
						rm(tmpFile);
				}
			}

			return new FileOutputStream(tmpFile.getPath(), append);
		} catch (IOException e) {
			unlock();

			release();

			throw e;
		}
	}

	/**
	 * Close the target file for reading.
	 *
	 * @throws IOException If an error occurs closing the file.
	 */
	synchronized void closeOutputFile() throws IOException {
		try {
			boolean orgExists = orgFile.exists();
			boolean newExists = newFile.exists();

			// fix for files being cached too often. On some platforms (seen on Windows XP & QNX),
			//  the data just written to a file can held in the filesystem cache. If the file is
			//  written to too often, then both the orgFile and oldFile data contents can be held
			//  in cache. The problem is that the directory structure can be flushed before the 
			//  data is flushed (easily recreated on Windows XP) then if power is lost then both the 
			//  orgFile and oldFile contents are garbage. To reduce the chances of this, we will
			//  only move an orgFile to oldFile if it is older than N seconds. This will greatly 
			//  reduce the chances that both copies could be lost in the filesystem cache during a 
			//  power off/failure. The net result is, when a file is written several times in a short
			//  time frame, we will move the original orgFile to oldFile, then rewrite orgFile several times
			//  without changing oldFile. - PAL
			// This problem is greatly reduced by calling java.io.FileOutputStream.getFD().sync() method
			//  before the close. Now this time can be shortened considerably. -PAL
			boolean backupOrg = true;
			long orgAge = System.currentTimeMillis() - orgFile.lastModified();
			if ((orgAge >= 0) && (orgAge < agingInterval)) {
				backupOrg = false;
			}

			if (newExists) {
				// shouldn't be possible, but just in case - PAL
				rm(newFile);
			}

			mv(tmpFile, newFile);

			if (orgExists) {
				if (newExists) {
					rm(orgFile);
				} else {
					if (backupOrg) {
						rm(oldFile);
						mv(orgFile, oldFile);
					} else {
						// orgFile is not old enough, do not replace oldFile.
						rm(orgFile);
					}
				}
			}

			mv(newFile, orgFile);
		} finally {
			unlock();

			release();
		}
	}

	/**
	 * This method recovers the reliable file if necessary.
	 *
	 * @throws IOException If an error occurs recovering the file.
	 */
	private BufferedInputStream recoverFile() throws IOException {
		boolean orgExists = orgFile.exists();
		boolean newExists = newFile.exists();
		boolean oldExists = oldFile.exists();
		Checksum orgCrc = null;
		Checksum newCrc = null;
		Checksum oldCrc = null;
		int orgVersion = VERSION_2;
		int oldVersion = VERSION_2;
		BufferedInputStream orgBis = null;
		BufferedInputStream newBis = null;
		BufferedInputStream oldBis = null;

		if (newExists) {
			newCrc = getChecksumCalculator();
			// if not valid, it doesn't exist
			//   ignore if it has a signature, don't use it
			newBis = new BufferedInputStream(new FileInputStream(newFile));
			newExists = isValidReliableFile(newBis, newCrc, null);
			if (!newExists) // if not valid, delete the unusable file
				rm(newFile);
		}

		// always create orgCrc, account for case that new,org,old don't exist
		orgCrc = getChecksumCalculator();
		if (orgExists) {
			boolean orgHasSig[] = new boolean[1];
			orgBis = new BufferedInputStream(new FileInputStream(orgFile));
			boolean orgValid = isValidReliableFile(orgBis, orgCrc, orgHasSig);

			if (!orgValid) {
				if (oldExists) {
					oldCrc = getChecksumCalculator();
					boolean oldHasSig[] = new boolean[1];
					oldBis = new BufferedInputStream(new FileInputStream(oldFile));
					boolean oldValid = isValidReliableFile(oldBis, oldCrc, oldHasSig);

					if (oldValid) {
						// swap them... old file is good, org file is bad
						if (tmpFile.exists())
							rm(tmpFile);
						if (orgBis != null) {
							orgBis.close();
							orgBis = null;
						}
						if (oldBis != null) {
							oldBis.close();
							oldBis = null;
						}
						mv(orgFile, tmpFile);
						mv(oldFile, orgFile);
						mv(tmpFile, oldFile);
						orgVersion = VERSION_2;
						oldVersion = VERSION_UNKNOWN;
					} else {
						// org is bad, old is bad
						if (!orgHasSig[0] && !oldHasSig[0]) {
							// org looks like text file, old looks like text file, use orgFile
							orgVersion = VERSION_PLAINTEXT;
							oldVersion = VERSION_PLAINTEXT;
						} else if (orgHasSig[0] && !oldHasSig[0]) {
							// org is corrupt, old file is text file, use old file
							// swap them...
							if (tmpFile.exists())
								rm(tmpFile);
							if (orgBis != null) {
								orgBis.close();
								orgBis = null;
							}
							if (oldBis != null) {
								oldBis.close();
								oldBis = null;
							}
							mv(orgFile, tmpFile);
							mv(oldFile, orgFile);
							mv(tmpFile, oldFile);
							orgVersion = VERSION_PLAINTEXT;
							oldVersion = VERSION_UNKNOWN;
						} else if (orgHasSig[0] && oldHasSig[0]) {
							// both org,old are corrupt, not good
							if (!newExists)
								throw new IOException("ReliableFile is corrupt."); //$NON-NLS-1$ 
							// else allow new to replace org
						} else if (!orgHasSig[0] && oldHasSig[0]) {
							// hmmmm, org look like text file, old is corrupt
							//  could happen if org is bad, old is plain text, they'd get swapped
						}
					}
				} else {
					// org is bad, old doesn't exist
					if (orgHasSig[0]) {
						if (!newExists) {
							throw new IOException("ReliableFile is corrupt."); //$NON-NLS-1$
						}
						// else newFile is good, restore it
					} else {
						// else use orgFile, must be hand made or old version
						orgVersion = VERSION_PLAINTEXT;
					}
				}
			} else {
				// else orgFile is good, won't ever use oldFile
			}
		} else if (oldExists) {
			// no orig file, yes old file, check old file
			oldCrc = getChecksumCalculator();
			boolean oldHasSig[] = new boolean[1];
			oldBis = new BufferedInputStream(new FileInputStream(oldFile));
			boolean oldValid = isValidReliableFile(oldBis, oldCrc, oldHasSig);
			if (!oldValid) {
				if (oldHasSig[0]) {
					// old has signature, but not valid
					if (!newExists)
						throw new IOException("ReliableFile is corrupt."); //$NON-NLS-1$
					// else allow new to become org
				} else {
					// else, file appears to be a text file, use it 
					oldVersion = VERSION_PLAINTEXT;
				}
			} else {
				// else no problem, use oldFile
			}
		}

		if (newExists) {
			if (orgBis != null)
				orgBis.close();
			if (oldBis != null)
				oldBis.close();
			if (newBis != null) {
				newBis.close();
				newBis = null;
			}
			if (orgExists) {
				if (oldExists)
					rm(oldFile);
				mv(orgFile, oldFile);
			}
			mv(newFile, orgFile);
			appendCrc = newCrc;
			version = VERSION_2; // must be a version2 file
			return newBis;
		} else {
			if (oldExists && !orgExists) {
				if (orgBis != null)
					orgBis.close();
				cp(oldFile, orgFile, 0);
				appendCrc = oldCrc;
				version = oldVersion;
				return oldBis;
			} else {
				if (oldBis != null)
					oldBis.close();
				appendCrc = orgCrc;
				version = orgVersion;
				return orgBis;
			}
		}
	}

	/**
	 * Lock the target file.
	 *
	 * @throws IOException If the file is already locked.
	 */
	private void lock() throws IOException {
		if (locked) {
			throw new FileNotFoundException("file locked"); //$NON-NLS-1$
		}

		locked = true;
	}

	/**
	 * Unlock the target file.
	 */
	private void unlock() {
		locked = false;
	}

	/**
	 * Rename a file.
	 *
	 * @param from The original file.
	 * @param to The new file name.
	 * @throws IOException If the rename failed.
	 */
	private static void mv(File from, File to) throws IOException {
		if (!from.renameTo(to)) {
			throw new IOException("rename failed"); //$NON-NLS-1$
		}
	}

	private static final int CP_BUF_SIZE = 4096;

	/**
	 * Copy a file.
	 *
	 * @param from The original file.
	 * @param to The target file.
	 * @throws IOException If the copy failed.
	 */
	private static void cp(File from, File to, int truncateSize) throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(to);

			int length = (int) from.length();
			if (truncateSize > length)
				length = 0;
			else
				length -= truncateSize;
			if (length > 0) {
				if (length > CP_BUF_SIZE) {
					length = CP_BUF_SIZE;
				}

				in = new FileInputStream(from);

				byte buffer[] = new byte[length];
				int size = 0;
				int count;
				while ((count = in.read(buffer, 0, length)) > 0) {
					if ((size + count) >= length)
						count = length - size;
					out.write(buffer, 0, count);
					size += count;
				}

				in.close();
				in = null;
			}

			out.close();
			out = null;
		} catch (IOException e) {
			// close open streams
			if (out != null) {
				try {
					out.close();
				} catch (IOException ee) {
				}
			}

			if (in != null) {
				try {
					in.close();
				} catch (IOException ee) {
				}
			}

			throw e;
		}
	}

	/**
	 * Delete a file.
	 *
	 * @param file The file to delete.
	 * @throws IOException If the delete failed.
	 */
	private static void rm(File file) throws IOException {
		if (file.exists() && !file.delete()) {
			throw new IOException("delete failed"); //$NON-NLS-1$
		}
	}

	/**
	 * Answers a boolean indicating whether or not the specified reliable file
	 * exists on the underlying file system.
	 *
	 * @return <code>true</code> if the specified reliable file exists,
	 * <code>false</code> otherwise.
	 */
	public static boolean exists(File file) {
		if (file.exists()) /* quick test */
		{
			return true;
		}

		String name = file.getPath();

		return new File(name + oldExt).exists() || new File(name + newExt).exists();
	}

	/**
	 * Returns the time that the file denoted by this abstract pathname was last modified
	 *
	 * @return time the file was last modified (see java.io.File.lastModified())
	 */
	public static long lastModified(File file) {
		String name = file.getPath();
		File newFile = new File(name + newExt);
		if (newFile.exists())
			return newFile.lastModified();
		if (file.exists())
			return file.lastModified();
		File oldFile = new File(name + oldExt);
		return oldFile.lastModified();
	}

	/**
	 * Delete this reliable file on the underlying file system.
	 *
	 * @throws IOException If the delete failed.
	 */
	private synchronized void delete() throws IOException {
		try {
			lock();
		} catch (IOException e) {
			/* the lock request failed; decrement the use count */
			release();

			throw e;
		}

		try {
			rm(oldFile);
			rm(orgFile);
			rm(newFile);
			rm(tmpFile);
		} finally {
			unlock();

			release();
		}
	}

	/**
	 * Delete the specified reliable file
	 * on the underlying file system.
	 *
	 * @return <code>true</code> if the specified reliable file was deleted,
	 * <code>false</code> otherwise.
	 */
	public static boolean delete(File file) {
		try {
			getReliableFile(file).delete();

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Returns the size of the ReliableFile signature + CRC at the end of the file.
	 * This method should be called only after calling getInputStream() or 
	 * getOutputStream() methods.
	 *
	 * @return <code>int</code> size of the ReliableFIle signature + CRC appended 
	 * to the end of the file.
	 * @throws IOException if getInputStream() or getOutputStream has not been
	 * called.
	 */
	protected int getSignatureSize() throws IOException {
		if (version == VERSION_UNKNOWN)
			throw new IOException("Version is unknown!"); //$NON-NLS-1$
		if (version == VERSION_2)
			return 16;
		else
			return 0;
	}

	/**
	 * Returns a Checksum object for the current file contents. This method 
	 * should be called only after calling getInputStream() or 
	 * getOutputStream() methods.
	 *
	 * @return Object implementing Checksum interface initialized to the 
	 * current file contents.
	 * @throws IOException if getInputStream() or getOutputStream has not been
	 * called.
	 */
	protected Checksum getFileChecksum() throws IOException {
		if (appendCrc == null)
			throw new IOException("Checksum is invalid!"); //$NON-NLS-1$
		return appendCrc;
	}

	/**
	 * Create a checksum implementation used by ReliableFile.
	 *
	 * @return Object implementing Checksum interface used to calculate
	 * a reliable file checksum
	 */
	protected static Checksum getChecksumCalculator() {
		// Using CRC32 because Adler32 isn't in the jclGwp library.
		return new CRC32();
	}

	/**
	 * Determine if a File is a valid ReliableFile
	 *
	 * @return <code>true</code> if the file is a valid ReliableFile
	 * @throws IOException If an error occurs verifying the file.
	 */
	protected static boolean isValidReliableFile(BufferedInputStream bis, Checksum crc, boolean containsSignature[]) throws IOException {
		if (containsSignature != null)
			containsSignature[0] = false;

		bis.mark(bis.available());
		try {
			int len = bis.available();
			if (len < 16) {
				if (crc != null) {
					byte data[] = new byte[16];
					int num = bis.read(data);
					if (num > 0)
						crc.update(data, 0, num);
				}
				return false;
			}
			len -= 16;

			if (crc == null)
				crc = getChecksumCalculator();

			int pos = 0;
			byte data[] = new byte[8192];

			do {
				if (pos >= len)
					break;
				int read = data.length;
				if (pos + read > len)
					read = len - pos;

				int num = bis.read(data, 0, read);
				if (num == -1) {
					throw new IOException("Unable to read entire file."); //$NON-NLS-1$
				}

				crc.update(data, 0, num);
				pos += num;
			} while (true);

			byte sig[] = new byte[16];
			int num = bis.read(sig);
			if (num != 16) {
				throw new IOException("Unable to read entire file."); //$NON-NLS-1$
			}

			int i;
			int j;
			for (i = 0; i < 4; i++)
				if (identifier1[i] != sig[i]) {
					crc.update(sig, 0, 16); // update crc w/ sig bytes
					return false;
				}

			for (i = 0, j = 12; i < 4; i++, j++)
				if (identifier2[i] != sig[j]) {
					crc.update(sig, 0, 16); // update crc w/ sig bytes
					return false;
				}

			if (containsSignature != null)
				containsSignature[0] = true;

			long crccmp = Long.valueOf(new String(sig, 4, 8), 16).longValue();
			if (crccmp == crc.getValue()) {
				return true;
			} else {
				// do not update CRC
				return false;
			}
		} finally {
			bis.reset();
		}
	}

	protected static String intToHex(int l) {
		byte[] buffer = new byte[8];
		int count = 8;

		do {
			int ch = (l & 0xf);
			if (ch > 9)
				ch = ch - 10 + (int) 'a';
			else
				ch += (int) '0';
			buffer[--count] = (byte) ch;
			l >>= 4;
		} while (count > 0);
		return new String(buffer);
	}
}
