/*
 * Created on May 27, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.osgi.tools.btool;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * @author Peter Kriens
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ZipSource implements Source {
	Map		entries;
	ZipFile	zip;
	File	file;

	public ZipSource(File zipfile) throws IOException {
		this.file = zipfile;
		zip = new ZipFile(zipfile);
	}

	public File getFile() {
		return file;
	}

	public InputStream getEntry(String resourceName) throws IOException {
		ZipEntry entry = zip.getEntry(resourceName);
		if (entry != null) {
			return zip.getInputStream(entry);
		}
		else
			return null;
	}

	public boolean isDirectory(String path) {
		if (path.equals(""))
			return true;
		return entries.containsKey(path);
	}

	/**
	 * Do not think this is overdone. Many zip files exist that do not have
	 * directories! Each entry has a full name and directories are, in a way,
	 * optional. However, some tools expect them. So we have to traverse the ZIP
	 * and figure out the tree from the names.
	 */
	private void checkZip() {
		if (entries == null) {
			entries = new TreeMap();
			for (Enumeration e = zip.entries(); e.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				String name = entry.getName();
				if (name.endsWith("/")) {
					name = name.substring(0, name.length() - 1);
				}
				add(name);
			}
		}
	}

	class Node {
		String	path;
		List	subs;

		void add(String sub) {
			if (subs == null)
				subs = new Vector();
			subs.add(sub);
		}

		public String toString() {
			return "'" + path + "'" + ":" + subs;
		}
	}

	void add(String path) {
		if (path.equals("."))
			return;
		int n = path.lastIndexOf("/");
		String dir;
		String name;
		if (n < 0) {
			dir = "";
			name = path;
		}
		else {
			dir = path.substring(0, n);
			name = path.substring(n + 1);
		}
		Node node = (Node) entries.get(dir);
		if (node == null) {
			node = new Node();
			node.path = dir;
			entries.put(dir, node);
		}
		node.add(name);
	}

	public Collection getResources(String dir) {
		checkZip();
		Node node = (Node) entries.get(dir);
		if (node == null)
			return null;
		return node.subs;
	}

	public boolean contains(String path) {
		checkZip();
		//TODO: Inefficient, but simple Note that a JAR file
		// is not required to have directories which would
		// file a simple getEntry check ...
		ZipEntry entry = zip.getEntry(path);
		if (entry != null)
			return true;
		return entries.containsKey(path);
	}

	public String toString() {
		return "Z: " + zip.getName();
	}

	/**
	 * We take the container date
	 * 
	 * @param resourceName
	 * @return
	 */
	public long lastModified(String resourceName) {
		return file.lastModified();
	}
}
