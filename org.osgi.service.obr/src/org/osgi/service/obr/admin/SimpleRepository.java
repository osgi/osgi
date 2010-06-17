package org.osgi.service.obr.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.osgi.service.obr.Capability;
import org.osgi.service.obr.Part;
import org.osgi.service.obr.Requirement;

public class SimpleRepository implements Repository {

	private final String name;
	private final Part[] parts;
	private final Map properties;
	private final long created;

	public SimpleRepository(String name, Part[] parts, Map properties) {
		this.name = name;
		this.parts = parts;
		this.properties = properties;
		this.created = System.currentTimeMillis();
	}
	
	public long getLastModified() {
		return created;
	}

	public String getName() {
		return name;
	}

	public Iterator<Part> parts() {
		return Arrays.asList(parts).iterator();
	}

	public Map getProperties() {
		return properties;
	}

	public Iterator<Capability> capabilities(Requirement filter) {
		ArrayList<Capability> list = new ArrayList<Capability>();
		
		for (Part p : parts) {
			for( Capability c : p.getCapabilities() ) {
				if ( filter == null || filter.isSatisfied(c) ) {
					list.add(c);
				}
			}
		}
		
		return list.iterator();
	}
	
}
