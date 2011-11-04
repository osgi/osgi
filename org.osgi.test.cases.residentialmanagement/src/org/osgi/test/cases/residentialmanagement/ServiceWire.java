package org.osgi.test.cases.residentialmanagement;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.ServiceReference;

public class ServiceWire implements RMTConstants{

	String provider;
	String requirer;
	ServiceReference ref;

	public ServiceWire( ServiceReference ref, String provider, String requirer) {
		this.ref = ref;
		this.provider = provider;
		this.requirer = requirer;
	}
	
	String getFilter() {
		String id = (String) ref.getProperty("service.id");
		return ("(service.id=" + id + ")");
	}
	
	Map<String, String> getCapabilityAttributes() {
		Map<String, String> attributes = new HashMap<String, String>();
		for (String key : ref.getPropertyKeys())
			attributes.put( key, (String) ref.getProperty(key));
		return attributes;
	}
	
	Map<String, String> getRequirementAttributes() {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(FILTER, getFilter());
		return attributes;
	}
	
}
