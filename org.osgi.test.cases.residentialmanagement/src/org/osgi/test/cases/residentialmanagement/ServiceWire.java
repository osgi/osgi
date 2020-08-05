package org.osgi.test.cases.residentialmanagement;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.ServiceReference;

public class ServiceWire implements RMTConstants{

	String provider;
	String requirer;
	ServiceReference< ? >	ref;

	public ServiceWire(ServiceReference< ? > ref, String provider,
			String requirer) {
		this.ref = ref;
		this.provider = provider;
		this.requirer = requirer;
	}
	
	String getFilter() {
		return ("(service.id=" + ref.getProperty("service.id") + ")");
	}
	
	Map<String, Object> getCapabilityAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();
		for (String key : ref.getPropertyKeys())
			attributes.put( key, ref.getProperty(key));
		attributes.put("osgi.wiring.rmt.service", ref.getProperty("service.id"));
		return attributes;
	}
	
	Map<String, String> getRequirementAttributes() {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(FILTER, getFilter());
		return attributes;
	}
}
