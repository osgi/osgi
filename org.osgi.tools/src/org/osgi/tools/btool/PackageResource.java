package org.osgi.tools.btool;

import java.io.*;
import java.util.*;

public class PackageResource extends Resource {
	final static int	EXPORT	= 1;
	final static int	PRIVATE	= 2;
	final static int	IMPORT	= 0;
	String				name;
	String				importVersion;
	String				version;
	PackageResource		next;
	boolean				active;
	boolean				included;
	int					type	= IMPORT;

	/**
	 * @param tool
	 * @param source2
	 * @param path
	 */
	public PackageResource(BTool btool, Source source, String path) {
		super(btool, source, path);
		name = path.replace('/', '.');
	}

	void setType(int type) {
		this.type = type;
	}

	public String toString() {
		return "P:" + getPath();
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public static PackageResource find(PackageResource[] ps, String name,
			int start) {
		for (int i = start; i < ps.length; i++)
			if (ps[i].name.equals(name))
				return ps[i];
		return null;
	}

	/**
	 * @param source
	 */
	public void setSource(Source source) {
		this.source = source;
	}

	Collection getResources() throws IOException {
		Collection strings = source.getResources(getPath());
		Collection result = new Vector();
		for (Iterator i = strings.iterator(); i.hasNext();) {
			String name = (String) i.next();
			String path = getPath() + '/' + name;
			Resource r = null;
			if (source.isDirectory(path))
				r = new PackageResource(btool, source, path);
			else
				r = new Resource(btool, source, path);
			result.add(r);
		}
		return result;
	}

	static String nameToPath(String name) {
		return name.replace('.', '/');
	}

	static String pathToName(String path) {
		return path.replace('/', '.');
	}

	/**
	 * @return
	 */
	public Source getSource() {
		return source;
	}

	public InputStream getInputStream() {
		return null;
	}

	/**
	 * @param version2
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	public int hashCode() {
		return path.hashCode();
	}

	public boolean equals(Object o) {
		return path.equals(((PackageResource) o).path);
	}

	/**
	 * @return
	 */
	public int getType() {
		return type;
	}
	
	public long lastModified() { return 0; }
}
