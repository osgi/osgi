/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.adaptor.core;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.internal.core.Constants;
import org.eclipse.osgi.framework.internal.protocol.bundleresource.Handler;

/**
 * The BundleFile API is used by Adaptors to read resources out of an 
 * installed Bundle in the Framework.
 */
abstract public class BundleFile {
	/**
	 * The File object for this BundleFile.
	 */
	protected File basefile;

	/**
	 * BundleFile constructor
	 * @param basefile The File object where this BundleFile is 
	 * persistently stored.
	 */
	public BundleFile(File basefile) {
		this.basefile = basefile;
	}

	/**
	 * Returns a File for the bundle entry specified by the path.
	 * If required the content of the bundle entry is extracted into a file
	 * on the file system.
	 * @param path The path to the entry to locate a File for.
	 * @return A File object to access the contents of the bundle entry.
	 */
	abstract public File getFile(String path);

	/**
	 * Locates a file name in this bundle and returns a BundleEntry object
	 *
	 * @param path path of the entry to locate in the bundle
	 * @return BundleEntry object or null if the file name
	 *         does not exist in the bundle
	 */
	abstract public BundleEntry getEntry(String path);

	/** 
	 * Allows to access the entries of the bundle. 
	 * Since the bundle content is usually a jar, this 
	 * allows to access the jar contents.
	 * 
	 * GetEntryPaths allows to enumerate the content of "path".
	 * If path is a directory, it is equivalent to listing the directory
	 * contents. The returned names are either files or directories 
	 * themselves. If a returned name is a directory, it finishes with a 
	 * slash. If a returned name is a file, it does not finish with a slash.
	 * @param path path of the entry to locate in the bundle
	 * @return an Enumeration of Strings that indicate the paths found or
	 * null if the path does not exist. 
	 */
	abstract public Enumeration getEntryPaths(String path);

	/**
	 * Closes the BundleFile.
	 * @throws IOException if any error occurs.
	 */
	abstract public void close() throws IOException;

	/**
	 * Opens the BundleFiles.
	 * @throws IOException if any error occurs.
	 */
	abstract public void open() throws IOException;

	/**
	 * Determines if any BundleEntries exist in the given directory path.
	 * @param dir The directory path to check existence of.
	 * @return true if the BundleFile contains entries under the given directory path;
	 * false otherwise.
	 */
	abstract public boolean containsDir(String dir);

	/**
	 * Returns a URL to access the contents of the entry specified by the path
	 * @param path the path to the resource
	 * @param hostBundleID the host bundle ID
	 */
	public URL getResourceURL(String path, long hostBundleID) {
		return getResourceURL(path, hostBundleID, 0);
	}

	/**
	 * Returns a URL to access the contents of the entry specified by the path
	 * @param path the path to the resource
	 * @param hostBundleID the host bundle ID
	 * @param index the resource index
	 */
	public URL getResourceURL(String path, long hostBundleID, int index) {
		BundleEntry bundleEntry = getEntry(path);
		if (bundleEntry == null)
			return null;

		try {
			StringBuffer url = new StringBuffer(Constants.OSGI_RESOURCE_URL_PROTOCOL);
			url.append("://").append(hostBundleID); //$NON-NLS-1$
			if (index > 0)
				url.append(':').append(index);
			if (path.length() == 0 || path.charAt(0) != '/')
				url.append('/');
			url.append(path);
			return new URL(null, url.toString(), new Handler(bundleEntry));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * A BundleFile that uses a ZipFile as it base file.
	 */
	public static class ZipBundleFile extends BundleFile {
		protected BundleData bundledata;
		protected ZipFile zipFile;
		protected boolean closed = false;

		public ZipBundleFile(File basefile, BundleData bundledata) throws IOException {
			super(basefile);
			this.bundledata = bundledata;
			this.closed = true;
			open();
		}

		protected ZipEntry getZipEntry(String path) {
			if (path.length() > 0 && path.charAt(0) == '/')
				path = path.substring(1);
			return zipFile.getEntry(path);
		}

		protected File extractDirectory(String dirName) {
			Enumeration entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				String entryPath = ((ZipEntry) entries.nextElement()).getName();
				if (entryPath.startsWith(dirName) && !entryPath.endsWith("/")) //$NON-NLS-1$
					getFile(entryPath);
			}
			return getExtractFile(dirName);
		}

		protected File getExtractFile(String entryName) {
			if (!(bundledata instanceof AbstractBundleData)) {
				return null;
			}
			File bundleGenerationDir = ((AbstractBundleData) bundledata).createGenerationDir();
			/* if the generation dir exists, then we have place to cache */
			if (bundleGenerationDir != null && bundleGenerationDir.exists()) {
				String path = ".cp"; /* put all these entries in this subdir */ //$NON-NLS-1$
				String name = entryName.replace('/', File.separatorChar);
				if ((name.length() > 1) && (name.charAt(0) == File.separatorChar)) /* if name has a leading slash */{
					path = path.concat(name);
				} else {
					path = path + File.separator + name;
				}
				return new File(bundleGenerationDir, path);
			}
			return null;
		}

		public File getFile(String entry) {
			ZipEntry zipEntry = getZipEntry(entry);
			if (zipEntry == null) {
				return null;
			}

			try {

				File nested = getExtractFile(zipEntry.getName());
				if (nested != null) {
					if (nested.exists()) {
						/* the entry is already cached */
						if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
							Debug.println("File already present: " + nested.getPath()); //$NON-NLS-1$
						}
					} else {
						if (zipEntry.getName().endsWith("/")) { //$NON-NLS-1$
							if (!nested.mkdirs()) {
								if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
									Debug.println("Unable to create directory: " + nested.getPath()); //$NON-NLS-1$
								}
								throw new IOException(AdaptorMsg.formatter.getString("ADAPTOR_DIRECTORY_CREATE_EXCEPTION", nested.getAbsolutePath())); //$NON-NLS-1$
							}
							extractDirectory(zipEntry.getName());
						} else {
							InputStream in = zipFile.getInputStream(zipEntry);
							if (in == null)
								return null;
							/* the entry has not been cached */
							if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
								Debug.println("Creating file: " + nested.getPath()); //$NON-NLS-1$
							}
							/* create the necessary directories */
							File dir = new File(nested.getParent());
							if (!dir.exists() && !dir.mkdirs()) {
								if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
									Debug.println("Unable to create directory: " + dir.getPath()); //$NON-NLS-1$
								}
								throw new IOException(AdaptorMsg.formatter.getString("ADAPTOR_DIRECTORY_CREATE_EXCEPTION", dir.getAbsolutePath())); //$NON-NLS-1$
							}
							/* copy the entry to the cache */
							AbstractFrameworkAdaptor.readFile(in, nested);
						}
					}

					return nested;
				}
			} catch (IOException e) {
				if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
					Debug.printStackTrace(e);
				}
			}
			return null;
		}

		public boolean containsDir(String dir) {
			if (dir == null)
				return false;

			if (dir.length() == 0)
				return true;

			if (dir.charAt(0) == '/') {
				if (dir.length() == 1)
					return true;
				dir = dir.substring(1);
			}

			if (dir.length() > 0 && dir.charAt(dir.length() - 1) != '/')
				dir = dir + '/';

			Enumeration entries = zipFile.entries();
			ZipEntry zipEntry;
			String entryPath;
			while (entries.hasMoreElements()) {
				zipEntry = (ZipEntry) entries.nextElement();
				entryPath = zipEntry.getName();
				if (entryPath.startsWith(dir)) {
					return true;
				}
			}
			return false;
		}

		public BundleEntry getEntry(String path) {
			ZipEntry zipEntry = getZipEntry(path);
			if (zipEntry == null) {
				if (path.length() == 0 || path.charAt(path.length() - 1) == '/') {
					// this is a directory request lets see if any entries exist in this directory
					if (containsDir(path))
						return new BundleEntry.DirZipBundleEntry(this, path);
				}
				return null;
			}

			return new BundleEntry.ZipBundleEntry(zipFile, zipEntry, this);

		}

		public Enumeration getEntryPaths(String path) {
			if (path == null) {
				throw new NullPointerException();
			}

			if (path.length() > 0 && path.charAt(0) == '/') {
				path = path.substring(1);
			}
			if (path.length() > 0 && path.charAt(path.length() - 1) != '/') {
				path = new StringBuffer(path).append("/").toString(); //$NON-NLS-1$
			}

			Vector vEntries = new Vector();
			Enumeration entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) entries.nextElement();
				String entryPath = zipEntry.getName();
				if (entryPath.startsWith(path)) {
					if (path.length() < entryPath.length()) {
						if (entryPath.lastIndexOf('/') < path.length()) {
							vEntries.add(entryPath);
						} else {
							entryPath = entryPath.substring(path.length());
							int slash = entryPath.indexOf('/');
							entryPath = path + entryPath.substring(0, slash + 1);
							if (!vEntries.contains(entryPath)) {
								vEntries.add(entryPath);
							}
						}
					}
				}
			}
			return vEntries.elements();
		}

		public void close() throws IOException {
			if (!closed) {
				closed = true;
				zipFile.close();
			}
		}

		public void open() throws IOException {
			if (closed) {
				zipFile = new ZipFile(this.basefile);

				closed = false;
			}
		}

	}

	/**
	 * A BundleFile that uses a directory as its base file.
	 */
	public static class DirBundleFile extends BundleFile {

		public DirBundleFile(File basefile) throws IOException {
			super(basefile);
			if (!basefile.exists() || !basefile.isDirectory()) {
				throw new IOException(AdaptorMsg.formatter.getString("ADAPTOR_DIRECTORY_EXCEPTION", basefile)); //$NON-NLS-1$
			}
		}

		public File getFile(String path) {
			File filePath = new File(this.basefile, path);
			if (filePath.exists()) {
				return filePath;
			}
			return null;
		}

		public BundleEntry getEntry(String path) {
			File filePath = new File(this.basefile, path);
			if (!filePath.exists()) {
				return null;
			}
			return new BundleEntry.FileBundleEntry(filePath, path);
		}

		public boolean containsDir(String dir) {
			File dirPath = new File(this.basefile, dir);
			return dirPath.exists() && dirPath.isDirectory();
		}

		public Enumeration getEntryPaths(final String path) {
			final java.io.File pathFile = new java.io.File(basefile, path);
			if (!pathFile.exists())
				return new Enumeration() {
					public boolean hasMoreElements() {
						return false;
					}

					public Object nextElement() {
						throw new NoSuchElementException();
					}
				};
			if (pathFile.isDirectory()) {
				final String[] fileList = pathFile.list();
				final String dirPath = path.length() == 0 || path.charAt(path.length() - 1) == '/' ? path : path + '/';
				return new Enumeration() {
					int cur = 0;

					public boolean hasMoreElements() {
						return fileList != null && cur < fileList.length;
					}

					public Object nextElement() {
						if (!hasMoreElements()) {
							throw new NoSuchElementException();
						}
						java.io.File childFile = new java.io.File(pathFile, fileList[cur]);
						StringBuffer sb = new StringBuffer(dirPath).append(fileList[cur++]);
						if (childFile.isDirectory()) {
							sb.append("/"); //$NON-NLS-1$
						}
						return sb.toString();
					}

				};
			} else {
				return new Enumeration() {
					int cur = 0;

					public boolean hasMoreElements() {
						return cur < 1;
					}

					public Object nextElement() {
						if (cur == 0) {
							cur = 1;
							return path;
						} else
							throw new NoSuchElementException();
					}
				};
			}
		}

		public void close() {
			// nothing to do.
		}

		public void open() {
			// nothing to do.
		}
	}

	/**
	 * A NestedDirBundleFile uses another BundleFile as its source but
	 * accesses all of its resources relative to a nested directory within
	 * the other BundleFile object.  This is used to support zipped bundles
	 * that use a Bundle-ClassPath with an nested directory specified.
	 * <p>
	 * For Example:
	 * <pre>
	 * Bundle-ClassPath: nested.jar,nesteddir/
	 * </pre>
	 */
	public static class NestedDirBundleFile extends BundleFile {
		BundleFile baseBundleFile;
		String cp;

		public NestedDirBundleFile(BundleFile baseBundlefile, String cp) {
			super(baseBundlefile.basefile);
			this.baseBundleFile = baseBundlefile;
			this.cp = cp;
			if (cp.charAt(cp.length() - 1) != '/') {
				this.cp = this.cp + '/';
			}
		}

		public void close() {
			// do nothing.
		}

		public BundleEntry getEntry(String path) {
			if (path.length() > 0 && path.charAt(0) == '/')
				path = path.substring(1);
			String newpath = new StringBuffer(cp).append(path).toString();
			return baseBundleFile.getEntry(newpath);
		}

		public boolean containsDir(String dir) {
			if (dir == null)
				return false;

			if (dir.length() > 0 && dir.charAt(0) == '/')
				dir = dir.substring(1);
			String newdir = new StringBuffer(cp).append(dir).toString();
			return baseBundleFile.containsDir(newdir);
		}

		public Enumeration getEntryPaths(String path) {
			// getEntryPaths is only valid if this is a root bundle file.
			return null;
		}

		public File getFile(String entry) {
			// getFile is only valid if this is a root bundle file.
			return null;
		}

		public void open() {
			// do nothing
		}
	}
	
	public File getBaseFile() {
		return basefile;
	}
}