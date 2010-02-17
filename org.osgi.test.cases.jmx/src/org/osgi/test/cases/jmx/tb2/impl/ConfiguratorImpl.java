package org.osgi.test.cases.jmx.tb2.impl;

import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;

public class ConfiguratorImpl implements ManagedServiceFactory, ManagedService {
	
	private Properties cfg;
	
	public ConfiguratorImpl() {
	}
	
	//coming from ManagedService
	public void updated(Dictionary props) throws ConfigurationException {
		System.out.println("Update called with pid " + props.get(Constants.SERVICE_PID));		
	}
	
	//coming from ManagedServiceFactory	
	public String getName() {
		System.out.println("Get name called");		
		return "JMX test managed service factory";
	}

	//coming from ManagedServiceFactory	
	public void updated(String pid, Dictionary properties)	throws ConfigurationException {
		System.out.println("Factory Configuration update with " + pid + " and value " + properties + " is called");
	}
	
	//coming from ManagedServiceFactory	
	public void deleted(String pid) {
		System.out.println("Factory Configuration delete with " + pid + " called");		
	}
}
