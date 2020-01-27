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

		//Serialize
		BaseSerialize serialize = null;
	
		if(serialize == null){
			LOGGER.warn(property.get(ServiceLayerUtil.PROTOCOL) + " & " + property.get(ServiceLayerUtil.SERIALIZE)
			+ " is Unimplemented.");
			return null;
		}

		//HTTP
		if (ServiceLayerUtil.PROTOCOL_HTTP.equals(property.get(ServiceLayerUtil.PROTOCOL).toLowerCase())) {

			// Create ServiceLayer Service
			sl = new ServiceLayerImplHttp(property.get(ServiceLayerUtil.ORIGIN), serialize);

			// get NotificationListener Service
			Collection<ServiceReference<NotificationListener>> notificationListenerServices = null;
			try {
				notificationListenerServices = bundle.getBundleContext().getServiceReferences(
						NotificationListener.class, "(" + ServiceLayerUtil.PROPERTY_KEY + "=" + bundleSymbolicName + ")");
			} catch (InvalidSyntaxException e) {
				LOGGER.warn("NotificationListener Acquisition failure.", e);
				return null;
			}
			ServiceReference<NotificationListener> notificationListenerService = notificationListenerServices.iterator()
					.next();
			NotificationListener listener = bundle.getBundleContext().getService(notificationListenerService);

			// get alias
			URI uri = null;
			try {
				uri = new URI(property.get(ServiceLayerUtil.POA));
			} catch (URISyntaxException e) {
				LOGGER.warn("Property PoA is Not URI.", e);
				return null;
			}
			String[] splitUri = (property.get(ServiceLayerUtil.POA)).split(uri.getRawAuthority());

			try {
				ServiceReference<?> sRef = bundle.getBundleContext().getServiceReference(HttpService.class.getName());
				if (sRef != null) {

					HttpService service = (HttpService) bundle.getBundleContext().getService(sRef);

					NotificationServlet servlet = new NotificationServlet(listener, serialize);

					service.registerServlet(splitUri[1], servlet, null, service.createDefaultHttpContext());
				} else {
					LOGGER.warn("HttpService is None.");
				}
			} catch (Exception e) {
				LOGGER.warn("ServletRegist failed.", e);
			}
		}

		//COAP
		if (ServiceLayerUtil.PROTOCOL_COAP.equals(property.get(ServiceLayerUtil.PROTOCOL).toLowerCase())) {

			// Create ServiceLayer Service
			sl = new ServiceLayerImplCoap(property.get(ServiceLayerUtil.ORIGIN), serialize);

			// Create CoAPServer
			URI uri = null;
			try {
				uri = new URI(property.get(ServiceLayerUtil.POA));
			} catch (URISyntaxException e) {
				LOGGER.warn("Property PoA is Not URI.", e);
				return null;
			}

			CoapServer server = new CoapServer();
			for (InetAddress addr : EndpointManager.getEndpointManager()
					.getNetworkInterfaces()) {
				if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
					InetSocketAddress bindToAddress = new InetSocketAddress(addr,uri.getPort());
					server.addEndpoint(new CoapEndpoint(bindToAddress));
				}
			}

			// get NotificationListener Service
			Collection<ServiceReference<NotificationListener>> notificationListenerServices = null;
			try {
				notificationListenerServices = bundle.getBundleContext().getServiceReferences(
						NotificationListener.class, "(" + ServiceLayerUtil.PROPERTY_KEY + "=" + bundleSymbolicName + ")");
			} catch (InvalidSyntaxException e) {
				LOGGER.warn("NotificationListener Acquisition failure.", e);
				return null;
			}
			ServiceReference<NotificationListener> notificationListenerService = notificationListenerServices.iterator()
					.next();
			NotificationListener listener = bundle.getBundleContext().getService(notificationListenerService);

			// set Deliverer
			server.setMessageDeliverer(new ServiceLayreDeliver(bundleSymbolicName, listener));
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
