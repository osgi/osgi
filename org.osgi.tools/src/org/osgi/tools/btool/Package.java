package org.osgi.tools.btool;

public class Package implements Comparable {
	String	name;
	String	version;
	int		type;

	public Package(String name) {
		this.name = name;
	}

	public Package(String name, String version) {
		this.name = name;
		this.version = version;
	}

	public String toString() {
		if (version == null)
			return name;
		return name + ";specification-version=" + version;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @param version The version to set.
	 */
	void setVersion(String version) {
		this.version = version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		Package p = (Package) o;
		return name.compareTo(p.name);
	}

	/**
	 * @return
	 */
	public Object getPath() {
		return getName().replace('.', '/');
	}
}
