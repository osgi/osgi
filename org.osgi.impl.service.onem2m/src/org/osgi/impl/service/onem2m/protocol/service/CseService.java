package org.osgi.impl.service.onem2m.protocol.service;

import static org.osgi.service.onem2m.dto.Constants.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.onem2m.NotificationListener;
import org.osgi.service.onem2m.dto.FilterCriteriaDTO.FilterUsage;
import org.osgi.service.onem2m.dto.NotificationDTO;
import org.osgi.service.onem2m.dto.NotificationEventDTO;
import org.osgi.service.onem2m.dto.PrimitiveContentDTO;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.service.onem2m.dto.ResponsePrimitiveDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("boxing")
public class CseService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CseService.class);

	private Map<String, ResourceDTO> resourceTree = new HashMap<String, ResourceDTO>();
	private int id = 0;

	String cseID = "in-cse";
	String csebaseName = "cb";
	BundleContext context;
	Bundle bundleFor;

	public CseService(BundleContext context, Bundle bundle) {
		this.context = context;
		bundleFor = bundle;
		// CSEBase
		ResourceDTO cseBase = new ResourceDTO();
		cseBase.resourceName = csebaseName;
		cseBase.resourceType = RT_CSEBase;
		String now = getDate();
		cseBase.resourceID = String.valueOf(id++);
		cseBase.creationTime = now;
		cseBase.lastModifiedTime = now;

		cseBase.attribute = new HashMap<String, Object>();
		cseBase.attribute.put("CSE-ID", cseID);
		cseBase.attribute.put("cseType", 1);// means IN-CSE

		HashSet<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < 23; i++) {
			set.add(i);
		}
		cseBase.attribute.put("supportedResourceType", set);
		resourceTree.put(csebaseName, cseBase);
	}

	public boolean isURIForMe(String uri) {
		if (uri.startsWith("//")) {// Absolute ID
			LOGGER.warn("Absolute ID is not supported");
			return false;
		} else if (uri.startsWith("/")) {// SP relative ID.
			String[] elem = uri.split("/", 3);
			return cseID.equals(elem[1]);

		} else {// CSE relative ID
			return true;
		}
	}

	/**
	 * convert URI to cseRelative form.
	 */
	public String cseRelativeURI(String uri) {
		if (uri.startsWith("//")) {// Absolute ID
			LOGGER.warn("Absolute ID is not supported");
			throw new IllegalArgumentException("Absolute ID is not expected. URI=" + uri);
		} else if (uri.startsWith("/")) {// SP relative ID.
			String[] elem = uri.split("/", 3);
			LOGGER.info("elment count:" + elem.length);
			for (int i = 0; i < elem.length; i++) {
				LOGGER.info("elment [" + i + "] :" + elem[i]);

			}
			if (cseID.equals(elem[1])) {// for me
				return convertShortcut(elem[2]);
			} else {
				throw new IllegalArgumentException(
						"SP relative name that is targeing other CSE is not expected. URI=" + uri);
			}
		} else {// CSE relative ID
			return convertShortcut(uri);
		}
	}

	private String convertShortcut(String uri) {
		if (uri.charAt(0) == '-') {
			return csebaseName + uri.substring(1);
		} else {
			return uri;
		}
	}

	public ResponsePrimitiveDTO create(RequestPrimitiveDTO req) {
		ResponsePrimitiveDTO res = new ResponsePrimitiveDTO();
		PrimitiveContentDTO con = new PrimitiveContentDTO();
		String msg = null;
		res.to = req.from;
		res.from = "/" + cseID;
		res.requestIdentifier = req.requestIdentifier;
		res.content = con;

		ResourceDTO resource = req.content.resource;

		if (resource.resourceType == RT_AE) {// check Type is AE.
			completeAE(resource);
		}
		if (!isURIForMe(req.to)) {
			msg = "The uri is not for the CSE";
			LOGGER.warn(msg);
			res.responseStatusCode = 4004;// need to change to right one.
			return res;
		}
		String regularTo = cseRelativeURI(req.to);

		LOGGER.info("to:" + req.to);
		LOGGER.info("regularTo:" + regularTo);

		ResourceDTO parent = resourceTree.get(regularTo);
		if (parent == null) {
			LOGGER.warn("Parent doesn't exit. uri:" + regularTo);
			res.responseStatusCode = 4004;// need to change to right one.
			return res;
		}
		resource.parentID = parent.resourceID;
		String targetUri = regularTo + "/" + resource.resourceName;

		if (!resourceTree.containsKey(targetUri)) {
			resource.resourceID = String.valueOf(id++);
			resource.creationTime = getDate();
			resource.lastModifiedTime = getDate();
			con.resource = resource;
			res.content = con;
			res.responseStatusCode = 2001;
			resourceTree.put(targetUri, resource);
			LOGGER.info("resource created. uri:" + targetUri + " resource:" + resource);
		} else {
			LOGGER.warn("Name already present in the parent collection. uri:" + targetUri + " resource:"
					+ resourceTree.get(targetUri));
			res.responseStatusCode = 4105;
		}
		return res;
	}

	private void completeAE(ResourceDTO resource) {
		String aename = aeNameBySybolicName(bundleFor.getSymbolicName());
		if (resource.attribute == null) {
			resource.attribute = new HashMap<String, Object>();
		}
		resource.attribute.put("AE-ID", aename);
		resource.attribute.put("pointOfAccess", "http://localhost:9999/" + aename);

	}

	public ResponsePrimitiveDTO retrieve(RequestPrimitiveDTO req) {
		ResponsePrimitiveDTO res = new ResponsePrimitiveDTO();
		PrimitiveContentDTO con = new PrimitiveContentDTO();
		res.to = req.from;
		res.from = "/" + cseID;
		res.requestIdentifier = req.requestIdentifier;

		if (!isURIForMe(req.to)) {
			LOGGER.warn("Resource is not for me. To:" + req.to);
			res.responseStatusCode = 4004;// TODO
			return res;
		}
		String regularTo = cseRelativeURI(req.to);

		// Retrieve
		if (req.filterCriteria == null || req.filterCriteria.filterUsage != FilterUsage.DiscoveryCriteria) {

			if (resourceTree.containsKey(regularTo)) {
				res.responseStatusCode = 2000;

				con.resource = resourceTree.get(regularTo);
			} else {
				LOGGER.warn("Resource not found.");
				res.responseStatusCode = 4004;
			}

		} else {
			// Discovery
			String target = regularTo.split("\\?")[0];
			if (resourceTree.containsKey(target)) {
				List<String> uril = new ArrayList<String>();
				Set<String> keys = resourceTree.keySet();
				for (String key : keys) {
					if (key.contains(target)) {
						uril.add(key);
					}
				}
				res.responseStatusCode = 2000;
				con.listOfURIs = uril;
			} else {
				LOGGER.warn("Root resource not found for discovery. root:" + target);
				dumpResourceTree();
				res.responseStatusCode = 4004;

			}
		}
		res.content = con;
		return res;
	}

	private void dumpResourceTree() {
		LOGGER.info("---------dump of resource Tree");
		Set<String> keys = resourceTree.keySet();
		for (String key : keys) {
			LOGGER.info("uri:" + key + " resource:" + resourceTree.get(key).toString());
		}
		LOGGER.info("--------- end of dump");

	}

	public ResponsePrimitiveDTO update(RequestPrimitiveDTO req) {
		LOGGER.info("update req:" + req);
		ResponsePrimitiveDTO res = new ResponsePrimitiveDTO();
		PrimitiveContentDTO con = new PrimitiveContentDTO();
		res.to = req.from;
		res.from = "/" + cseID;
		res.requestIdentifier = req.requestIdentifier;
		res.content = con;

		ResourceDTO resource = req.content.resource;
		ResourceDTO orgResource = null;

		if (!isURIForMe(req.to)) {
			LOGGER.warn("Resource not found.");
			res.responseStatusCode = 4004;
			return res;
		}
		String regularTo = cseRelativeURI(req.to);

		if (resourceTree.containsKey(regularTo)) {
			orgResource = resourceTree.get(regularTo);
		} else {
			LOGGER.warn("Resource not found. regularTo:" + regularTo + " req.to:" + req.to);
			res.responseStatusCode = 4004;
			return res;
		}
		res.responseStatusCode = 2004;
		resource.creationTime = orgResource.creationTime;
		resource.resourceID = orgResource.resourceID;
		resource.parentID = orgResource.parentID;

		resource.lastModifiedTime = getDate();

		resourceTree.put(regularTo, resource);// This omits detailed implementation.
		res.content.resource = resource;

		updateNotify(regularTo, resource);

		return res;
	}

	private void updateNotify(String uri, ResourceDTO resource) {
		LOGGER.info("updateNotify() is called. uri:" + uri + " resource:" + resource);
		dumpResourceTree();

		Set<Entry<String, ResourceDTO>> h = resourceTree.entrySet();
		int len = uri.length();

		for (Entry<String, ResourceDTO> en : h) {
			String u2 = en.getKey();
			if (u2.length() <= len)
				continue;

			if (uri.equals(u2.substring(0, len))) {
				// u2 is under uri.
				String child = u2.substring(len + 1);// +1 means deleting "/"
				if (!child.contains("/")) {
					// u2 is direct child of uri.
					ResourceDTO sub = en.getValue();
					if (sub.resourceType != RT_subscription) {
						LOGGER.info("resource is not subscription uri:" + u2 + " resurceType:" + sub.resourceType);
						continue;
					}

					LOGGER.info("subscription found" + u2 + " sub:" + sub);
					String notificationURI = (String) sub.attribute.get("notificationURI");
					RequestPrimitiveDTO req = new RequestPrimitiveDTO();
					req.content = new PrimitiveContentDTO();
					req.content.notification = new NotificationDTO();
					req.content.notification.notificationEvent = new NotificationEventDTO();
					req.content.notification.notificationEvent.representation = resource;
					req.to = notificationURI;

					notify(req);
				}

			}
		}

	}

	public ResponsePrimitiveDTO delete(RequestPrimitiveDTO req) {
		ResponsePrimitiveDTO res = new ResponsePrimitiveDTO();
		res.to = req.from;
		res.from = "/" + cseID;
		res.requestIdentifier = req.requestIdentifier;

		if (!isURIForMe(req.to)) {
			LOGGER.warn("Resource not found.");
			res.responseStatusCode = 4004;
			return res;
		}
		String regularTo = cseRelativeURI(req.to);

		Set<String> keys = resourceTree.keySet();
		boolean valid = false;
		String msg = null;
		for (String key : keys) {
			if (regularTo.equals(key)) {
				valid = true;
				resourceTree.remove(key);
				break;
			}
		}

		if (valid) {
			res.responseStatusCode = 2002;
		} else {
			msg = "Resource not found.";
			LOGGER.warn(msg);
			res.responseStatusCode = 4004;
		}

		return res;
	}

	public String getDate() {
		Calendar cl = Calendar.getInstance();
		String date;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
		date = sdf.format(cl.getTime());
		return date;

	}

	public ResponsePrimitiveDTO notify(RequestPrimitiveDTO req) {
		LOGGER.info("notify() req:" + req);
		ResponsePrimitiveDTO res = new ResponsePrimitiveDTO();
		String to = req.to;
		if (to == null) {
			res.responseStatusCode = 1000;// error
			return res;
		}
		String[] element = to.split("/");
		LOGGER.info("to:" + to);

		String aeid = element[2];
		String bsn = symbolicNameByAeName(aeid);
		if (element[1].equals(cseID)) {

			LOGGER.debug("NOW prepare to send notification.");
			ServiceReference< ? >[] rs;
			try {
				rs = context.getServiceReferences(NotificationListener.class.getName(), null);
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
				res.responseStatusCode = 1000;
				return res;
			}
			int count = 0;
			for (ServiceReference< ? > ref : rs) {

				if (ref.getBundle().getSymbolicName().equals(bsn)) {
					NotificationListener lis = (NotificationListener) context.getService(ref);
					lis.notified(req);
					count++;
				}
			}

			LOGGER.debug("Count of Notification:" + count);
			res.responseStatusCode = 2000;
		} else {
			res.responseStatusCode = 1000;// error
		}
		return res;
	}

	private String aeNameBySybolicName(String symbolicName) {
		if (symbolicName.equals("org.osgi.test.cases.onem2m")) {
			return "CAE1";
		} else if (symbolicName.equals("org.osgi.test.cases.onem2m2")) {
			return "CAE2";
		} else if (symbolicName.equals("org.osgi.test.cases.onem2m3")) {
			return "CAE3";
		}
		throw new IllegalArgumentException("unexpected bundleSymbolicName:" + symbolicName);
	}

	private String symbolicNameByAeName(String aeName) {
		if (aeName.equals("CAE1")) {
			return "org.osgi.test.cases.onem2m";
		} else if (aeName.equals("CAE2")) {
			return "org.osgi.test.cases.onem2m2";
		} else if (aeName.equals("CAE3")) {
			return "org.osgi.test.cases.onem2m3";
		}
		throw new IllegalArgumentException("unexpected aeName:" + aeName);

	}
}
