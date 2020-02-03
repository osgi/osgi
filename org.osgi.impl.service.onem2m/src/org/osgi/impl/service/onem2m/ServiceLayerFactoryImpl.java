package org.osgi.impl.service.onem2m;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import org.osgi.service.onem2m.*;
import org.osgi.service.onem2m.dto.*;


import org.osgi.impl.service.onem2m.protocol.service.ServiceLayreImplService;
import org.osgi.impl.service.onem2m.*;
import org.osgi.impl.service.onem2m.protocol.ServiceLayerUtil;
import org.osgi.impl.service.onem2m.protocol.service.*;
import org.osgi.impl.service.onem2m.serialization.BaseSerialize;
import org.osgi.test.cse.toyCse.CseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.osgi.impl.service.onem2m.protocol.*;

public class ServiceLayerFactoryImpl implements ServiceFactory<ServiceLayer> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLayerFactoryImpl.class);

	@Override
	public ServiceLayer getService(Bundle bundle, ServiceRegistration<ServiceLayer> registration) {

		LOGGER.info("Start factory");

		String bundleSymbolicName = bundle.getSymbolicName();

		// get Properties
		Map<String, String> property = ServiceLayerUtil.getProperty(bundleSymbolicName, bundle.getBundleContext());
		ServiceLayer sl = null;

		if (property == null) {
			LOGGER.warn(bundleSymbolicName + " is no property.");
			return null;
		}

		//Service cooperation
		if (ServiceLayerUtil.SERIALIZE_SERVICE.equals(property.get(ServiceLayerUtil.SERIALIZE).toLowerCase())
				|| ServiceLayerUtil.PROTOCOL_SERVICE.equals(property.get(ServiceLayerUtil.PROTOCOL).toLowerCase())){

			ServiceReference<?> cseService = bundle.getBundleContext().getServiceReference(CseService.class.getName());
			
			CseService cse = (CseService) bundle.getBundleContext().getService(cseService);
			sl = new ServiceLayreImplService(property.get(ServiceLayerUtil.ORIGIN), cse);
			return sl;
		}

		if(sl == null){
			LOGGER.warn(property.get(ServiceLayerUtil.PROTOCOL) + " & " + property.get(ServiceLayerUtil.SERIALIZE)
			+ " is Unimplemented.");
			return null;
		}

		LOGGER.info("End factory");
		return sl;

	}

	@Override
	public void ungetService(Bundle bundle, ServiceRegistration<ServiceLayer> registration, ServiceLayer service) {
		// NOP

	}

	

}
