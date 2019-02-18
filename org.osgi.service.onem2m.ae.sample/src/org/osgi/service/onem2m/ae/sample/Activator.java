package org.osgi.service.onem2m.ae.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.onem2m.NotificationListener;
import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.FilterCriteriaDTO;
import org.osgi.service.onem2m.dto.FilterCriteriaDTO.FilterUsage;
import org.osgi.service.onem2m.dto.NotificationDTO;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.util.promise.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Activator implements BundleActivator {
	private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

	private static BundleContext context;

	private static String bundleSymbolicName = null;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		LOGGER.info("start sample bundle");

		Activator.context = bundleContext;
		bundleSymbolicName = context.getBundle().getSymbolicName();

		// NotificationListener Registration
		Hashtable<String, String> tbl = new Hashtable<String, String>();
		tbl.put("symbolicName", bundleSymbolicName);
		NotificationListener notificationListener = new NotificationListenerImpl();
		context.registerService(NotificationListener.class, notificationListener, tbl);

		Thread.sleep(1000);

		//Get ServiceLayerFactory
		ServiceReference<?> serviceLayerFactory = context.getServiceReference(ServiceLayer.class.getName());
		ServiceLayer service = (ServiceLayer) context.getService(serviceLayerFactory);

		if (service == null) {
			LOGGER.warn("Create Service Failed");
			return;
		}

		ServiceLayer serviceLayerService = (ServiceLayer) service;

//		LOGGER.debug("----AE CREATE!!!----");
//		ResourceDTO ae = new ResourceDTO();
//		ae.resourceType = 2;
//		ae.resourceName = "SampleAE";
//		Map<String, Object> attr = new HashMap<String, Object>();
//		attr.put("App-ID", "SampleAE");
//		attr.put("requestReachability", "true");
//		attr.put("pointOfAccess", "coap://127.0.0.1:8089");
//		ae.attribute = attr;
//		serviceLayerService.create("coap://127.0.0.1:5683/~/in-cse", ae);
//
//		//Wait
//		Thread.sleep(3000);
//		System.out.println();
//		System.out.println();
//		System.out.println();
//
//		serviceLayerService.retrieve("coap://127.0.0.1:5683/~/in-cse", new ResourceDTO());
//
//		//Wait
//		Thread.sleep(3000);
//		System.out.println();
//		System.out.println();
//		System.out.println();
//
//		serviceLayerService.retrieve("coap://127.0.0.1:5683/in-name", new ResourceDTO());
//
//		serviceLayerService.retrieve("coap://192.168.1.59:5703/in-cse", new ResourceDTO());
//
//		serviceLayerService.retrieve("http://192.168.1.59:8290/in-cse", new ResourceDTO());
//		serviceLayerService.retrieve("http://127.0.0.1:38080/in-name", new ResourceDTO());

		//Retrieve(ACP)
		System.out.println();
		System.out.println();
		System.out.println();
		LOGGER.debug("----ACP RETRIEVE!!!----");
		ResourceDTO res =  serviceLayerService.retrieve("http://127.0.0.1:38080/in-name/acp_admin").getValue();
		LOGGER.debug(res.toString());


		//Wait
		Thread.sleep(3000);
		System.out.println();
		System.out.println();
		System.out.println();

		//Create(AE)
		LOGGER.debug("----AE CREATE!!!----");
		ResourceDTO ae = new ResourceDTO();
		ae.resourceType = 2;
		ae.resourceName = "SampleAE";
		Map<String, Object> attr = new HashMap<String, Object>();
		attr.put("App-ID", "SampleAE");
		attr.put("requestReachability", "true");
		attr.put("pointOfAccess", "http://127.0.0.1:38080/requestSample");
		ae.attribute = attr;
		serviceLayerService.create("http://127.0.0.1:38080/in-name", ae);

		//Wait
		Thread.sleep(3000);
		System.out.println();
		System.out.println();
		System.out.println();

		//Create(Conteiner)
		LOGGER.debug("----Conteiner CREATE!!!----");
		ResourceDTO cnt = new ResourceDTO();
		cnt.resourceType = 3;
		cnt.resourceName = "SampleCnt";
		serviceLayerService.create("http://127.0.0.1:38080/in-name/SampleAE", cnt);

		//Wait
		Thread.sleep(3000);
		System.out.println();
		System.out.println();
		System.out.println();

		//Send Notify
		LOGGER.debug("----Notify!!!----");
		NotificationDTO notification = new NotificationDTO();
		Map<String, Object> nev = new HashMap<String, Object>();
		nev.put("representation", 1);
		notification.notificationEvent = nev;
		notification.subscriptionDeletion = false;
		notification.subscriptionReference = "http://127.0.0.1:38080/in-name/SampleAE";
		serviceLayerService.notify("http://127.0.0.1:38080/in-name/SampleAE", notification);

		//Wait
		Thread.sleep(3000);
		System.out.println();
		System.out.println();
		System.out.println();

		//Create(ContentInstanse)
		LOGGER.debug("----ContentInstanse CREATE!!!----");
		ResourceDTO cin = new ResourceDTO();
		cin.resourceType = 4;
		cin.resourceName = "SampleCin";
		Map<String, Object> attrCin = new HashMap<String, Object>();
		attrCin.put("content", "sample");
		cin.attribute = attrCin;
		serviceLayerService.create("http://127.0.0.1:38080/in-name/SampleAE/SampleCnt", cin);

		//Wait
		Thread.sleep(3000);
		System.out.println();
		System.out.println();
		System.out.println();

		//Update(ContentInstanse)
		LOGGER.debug("----Container UPDATE!!!----");
		ResourceDTO cnt2 = new ResourceDTO();
		cnt2.resourceType = 3;
		Map<String, Object> attrCnt = new HashMap<String, Object>();
		List<String> lbl = new ArrayList<String>();
		lbl.add("SampleList");
		attrCnt.put("labels", lbl);
		cnt2.attribute = attrCnt;
		serviceLayerService.update("http://127.0.0.1:38080/in-name/SampleAE/SampleCnt", cnt2);

		//Wait
		Thread.sleep(3000);
		System.out.println();
		System.out.println();
		System.out.println();

		//DISCOVERY
		LOGGER.debug("----AE DISCOVERY!!!----");
		FilterCriteriaDTO fc = new FilterCriteriaDTO();
		fc.filterUsage = FilterUsage.DiscoveryCriteria;
		serviceLayerService.discovery("http://127.0.0.1:38080/in-name/SampleAE", fc);

		//Wait
		Thread.sleep(3000);
		System.out.println();
		System.out.println();
		System.out.println();

		//RETRIEVE
		LOGGER.debug("----ContentInstanse RETRIEVE!!!----");
		serviceLayerService.retrieve("http://127.0.0.1:38080/in-name/SampleAE/SampleCnt/SampleCin");

		//Wait
		Thread.sleep(3000);
		System.out.println();
		System.out.println();
		System.out.println();

		//RETRIEVE
		LOGGER.debug("----ContentInstanse RETRIEVE_ATTRIBUTES!!!----");
		List<String> attributes = new ArrayList<String>();
		attributes.add("rn");
		attributes.add("ty");
		serviceLayerService.retrieve("http://127.0.0.1:38080/in-name/SampleAE/SampleCnt/SampleCin", attributes);

		//Wait
		Thread.sleep(3000);
		System.out.println();
		System.out.println();
		System.out.println();

		//DELETE
		LOGGER.debug("----AE DELETE!!!----");
		Promise<Boolean> delRes = serviceLayerService.delete("http://127.0.0.1:38080/in-name/SampleAE");
		if(delRes.getValue()){
			LOGGER.info("AE DELETE is Done.");
		} else {
			LOGGER.info("AE DELETE is Failed.");
		}

		LOGGER.info("end sample bundle");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
