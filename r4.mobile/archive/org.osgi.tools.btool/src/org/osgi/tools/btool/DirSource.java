package org.osgi.tools.btool;

import java.io.*;
import java.util.*;

public class DirSource implements Source {
	File	base;

	DirSource(File base) {
		this.base = base;
	}

	public File getFile() {
		return base;
	}

	public InputStream getEntry(String resourceName) throws IOException {
		return new FileInputStream(new File(base, resourceName));
	}

	public Collection getResources(String dir) {
		Vector v = new Vector();
		File sub = new File(base, dir);
		String[] list = sub.list();
		if (list == null) {
			System.out.println("No contents for " + sub);
		}
		else
			v.addAll(Arrays.asList(list));
		return v;
	}

	public boolean contains(String path) {
		File f = new File(base, path);
		return f.exists();
	}

	public boolean isDirectory(String path) {
		File f = new File(base, path);
		return f.isDirectory();
	}

	public String toString() {
		return "D: " + base.getName();
	}
	
	public long lastModified(String path) {
		return new File(base, path).lastModified();
	}
}
