package org.osgi.tools.btool;

import java.util.*;

public class Package implements Comparable {
	String	name;
	String	version;
	int		type;
	Map		attributes;
	Map		directives;
	public Set	uses;

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
		return name + " ;version=" + version;
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
	
	Map getDirectives() { return directives; }
	Map getAttributes() { return attributes; }

	/**
	 * @param parameter
	 */
	public void addParameter(Parameter parameter) {
		switch( parameter.type ) {
			case Parameter.ATTRIBUTE: 
				if ( attributes == null )
					attributes = new HashMap();
				attributes.put( parameter.key, parameter );
				break;
				
			case Parameter.DIRECTIVE: 
				if ( directives == null )
					directives = new HashMap();
				directives.put( parameter.key, parameter );
				break;				
		}
	}
	
}
