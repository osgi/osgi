/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.adaptor.core;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import org.eclipse.osgi.framework.util.SecureAction;

/**
 * A BundleEntry represents one entry of a BundleFile.
 */
public abstract class BundleEntry {
	/**
	 * Return an InputStream for the entry.
	 *
	 * @return InputStream for the entry.
	 * @throws java.io.IOException If an error occurs reading the bundle.
	 */
	public abstract InputStream getInputStream() throws IOException;

	/**
	 * Return the size of the entry (uncompressed).
	 *
	 * @return size of entry.
	 */
	public abstract long getSize();

	/**
	 * Return the name of the entry.
	 *
	 * @return name of entry.
	 */
	public abstract String getName();

	/**
	 * Get the modification time for this BundleEntry.
	 * <p>If the modification time has not been set,
	 * this method will return <tt>-1</tt>.
	 *
	 * @return last modification time.
	 */
	public abstract long getTime();

	/**
	 * Get a URL to the bundle entry that uses a common protocol (i.e. file:
	 * jar: or http: etc.).  
	 * @return a URL to the bundle entry that uses a common protocol
	 */
	public abstract URL getLocalURL();

	/**
	 * Get a URL to the content of the bundle entry that uses the file: protocol.
	 * The content of the bundle entry may be downloaded or extracted to the local
	 * file system in order to create a file: URL.
	 * @return a URL to the content of the bundle entry that uses the file: protocol
	 */
	public abstract URL getFileURL();

	/**
	 * Return the name of this BundleEntry by calling getName().
	 *
	 * @return String representation of this BundleEntry.
	 */
	public String toString() {
		return (getName());
	}

	/**
	 * A BundleEntry represented by a ZipEntry in a ZipFile.  The ZipBundleEntry
	 * class is used for bundles that are installed as a ZipFile on a file system.
	 */
	public static class ZipBundleEntry extends BundleEntry {
		/**
		 * ZipEntry for this entry.
		 */
		protected ZipEntry zipEntry;

		/**
		 * The BundleFile for this entry.
		 */
		protected BundleFile bundleFile;

		/**
		 * Constructs the BundleEntry using a ZipEntry.
		 * @param zipFile the ZipFile that this BundleEntry belongs to
		 * @param bundleFile BundleFile object this entry is a member of
		 * @param entry ZipEntry object of this entry
		 */
		protected ZipBundleEntry(ZipEntry entry, BundleFile bundleFile) {
			this.zipEntry = entry;
			this.bundleFile = bundleFile;
		}

		/**
		 * Return an InputStream for the entry.
		 *
		 * @return InputStream for the entry
		 * @exception java.io.IOException
		 */
		public InputStream getInputStream() throws IOException {
			return ((BundleFile.ZipBundleFile) bundleFile).getZipFile().getInputStream(zipEntry);
		}

		/**
		 * Return size of the uncompressed entry.
		 *
		 * @return size of entry
		 */
		public long getSize() {
			return zipEntry.getSize();
		}

		/**
		 * Return name of the entry.
		 *
		 * @return name of entry
		 */
		public String getName() {
			return zipEntry.getName();
		}

		/**
		 * Get the modification time for this BundleEntry.
		 * <p>If the modification time has not been set,
		 * this method will return <tt>-1</tt>.
		 *
		 * @return last modification time.
		 */
		public long getTime() {
			return zipEntry.getTime();
		}

		public URL getLocalURL() {
			try {
				return new URL("jar:file:" + bundleFile.basefile.getAbsolutePath() + "!/" + zipEntry.getName()); //$NON-NLS-1$//$NON-NLS-2$
			} catch (MalformedURLException e) {
				//This can not happen. 
				return null;
			}
		}

		public URL getFileURL() {
			try {
				File file = bundleFile.getFile(zipEntry.getName());
				return file.toURL();
			} catch (MalformedURLException e) {
				//This can not happen. 
				return null;
			}
		}
	}

	/**
	 * A BundleEntry represented by a File object.  The FileBundleEntry class is
	 * used for bundles that are installed as extracted zips on a file system.
	 */
	public static class FileBundleEntry extends BundleEntry {
		/**
		 * File for this entry.
		 */
		private File file;
		private String name;

		/**
		 * Constructs the BundleEntry using a File.
		 * @param file BundleFile object this entry is a member of
		 * @param name the name of this BundleEntry
		 */
		FileBundleEntry(File file, String name) {
			this.file = file;
			this.name = name;
		}

		/**
		 * Return an InputStream for the entry.
		 *
		 * @return InputStream for the entry
		 * @exception java.io.IOException
		 */
		public InputStream getInputStream() throws IOException {
			return SecureAction.getFileInputStream(file);
		}

		/**
		 * Return size of the uncompressed entry.
		 *
		 * @return size of entry
		 */
		public long getSize() {
			return (file.length());
		}

		/**
		 * Return name of the entry.
		 *
		 * @return name of entry
		 */
		public String getName() {
			return (name);
		}

		/**
		 * Get the modification time for this BundleEntry.
		 * <p>If the modification time has not been set,
		 * this method will return <tt>-1</tt>.
		 *
		 * @return last modification time.
		 */
		public long getTime() {
			return file.lastModified();
		}

		public URL getLocalURL() {
			return getFileURL();
		}

		public URL getFileURL() {
			try {
				return file.toURL();
			} catch (MalformedURLException e) {
				return null;
			}
		}
	}

	/**
	 * Represents a directory entry in a ZipBundleFile.  This object is used to 
	 * reference a directory entry in a ZipBundleFile when the directory entries are
	 * not included in the zip file.
	 */
	public static class DirZipBundleEntry extends BundleEntry {

		/**
		 * ZipBundleFile for this entry.
		 */
		private BundleFile.ZipBundleFile bundleFile;
		private String name;

		public DirZipBundleEntry(BundleFile.ZipBundleFile bundleFile, String name) {
			this.name = (name.length() > 0 && name.charAt(0) == '/') ? name.substring(1) : name;
			this.bundleFile = bundleFile;
		}

		public InputStream getInputStream() throws IOException {
			return null;
		}

		public long getSize() {
			return 0;
		}

		public String getName() {
			return name;
		}

		public long getTime() {
			return 0;
		}

		public URL getLocalURL() {
			try {
				return new URL("jar:file:" + bundleFile.basefile.getAbsolutePath() + "!/" + name); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (MalformedURLException e) {
				//This can not happen, unless the jar protocol is not supported.
				return null;
			}
		}

		public URL getFileURL() {
			try {
				return bundleFile.extractDirectory(name).toURL();
			} catch (MalformedURLException e) {
				// this cannot happen.
				return null;
			}
		}
	}
}
