package nursery.obr.bindex;

import java.util.*;

import org.osgi.framework.Version;

public class Entry implements Comparable {
	String		name;
	Version		version;
	Map			attributes;
	Map			directives;
	public Set	uses;

	public Entry(String name) {
		this.name = name;
	}

	public Entry(String name, Version version) {
		this.name = name;
		this.version = version;
	}

	public String toString() {
		if (version == null)
			return name;
		return name + " ;version=" + version;
	}

	public String getName() {
		return name;
	}

	public Version getVersion() {
		if (version != null)
			return version;
		return new Version("0");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		Entry p = (Entry) o;
		return name.compareTo(p.name);
	}

	/**
	 * @return
	 */
	public Object getPath() {
		return getName().replace('.', '/');
	}

	public Map getDirectives() {
		return directives;
	}

	public Map getAttributes() {
		return attributes;
	}

	/**
	 * @param parameter
	 */
	public void addParameter(Parameter parameter) {
		switch (parameter.type) {
			case Parameter.ATTRIBUTE :
				if (attributes == null)
					attributes = new HashMap();
				attributes.put(parameter.key, parameter.value);
				if (parameter.key.equalsIgnoreCase("version")
						|| parameter.key
								.equalsIgnoreCase("specification-version"))
					this.version = new Version(parameter.value);
				break;

			case Parameter.DIRECTIVE :
				if (directives == null)
					directives = new HashMap();
				directives.put(parameter.key, parameter.value);
				break;
		}
	}

}
