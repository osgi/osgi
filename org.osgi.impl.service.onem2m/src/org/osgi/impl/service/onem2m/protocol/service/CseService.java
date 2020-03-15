package org.osgi.impl.service.onem2m.protocol.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.*;
import org.osgi.service.onem2m.dto.FilterCriteriaDTO.FilterUsage;
import org.osgi.service.onem2m.NotificationListener;
import org.osgi.service.onem2m.dto.PrimitiveContentDTO;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.service.onem2m.dto.ResponsePrimitiveDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CseService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CseService.class);

	private Map<String, ResourceDTO> resourceTree = new HashMap<String, ResourceDTO>();
	private int id = 0;

	/*
	 * public CseService(String uri, ResourceDTO cseBase){ String now = getDate();
	 * cseBase.resourceID = String.valueOf(id); cseBase.creationTime = now;
	 * cseBase.lastModifiedTime = now; cse.put(uri, cseBase); }
	 */

	String cseID = "in-cse";
	String csebaseName = "cb";
	BundleContext context;

	public CseService(BundleContext context) {
		this.context = context;

		// CSEBase
		ResourceDTO cseBase = new ResourceDTO();
		cseBase.resourceName = csebaseName;
		cseBase.resourceType = 5;
		String now = getDate();
		cseBase.resourceID = String.valueOf(id++);
		cseBase.creationTime = now;
		cseBase.lastModifiedTime = now;

		cseBase.attribute = new HashMap<String, Object>();
		cseBase.attribute.put("CSE-ID", cseID);
		cseBase.attribute.put("cseType", 1);// means IN-CSE

		HashSet<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < 80; i++) {
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

		if (!isURIForMe(req.to)) {
			msg = "The uri is not for the CSE";
			LOGGER.warn(msg);
			res.responseStatusCode = 4004;// need to change to right one.
			return res;
		}
		String regularTo = cseRelativeURI(req.to);
		if (!resourceTree.containsKey(regularTo)) {
			LOGGER.warn("Parent doesn't exit.");
			res.responseStatusCode = 4004;// need to change to right one.
		}
		String targetUri = regularTo + "/" + resource.resourceName;

		if (!resourceTree.containsKey(targetUri)) {
			resource.resourceID = String.valueOf(id++);
			resource.creationTime = getDate();
			resource.lastModifiedTime = getDate();
			con.resource = resource;
			res.content = con;
			res.responseStatusCode = 2001;
			resourceTree.put(targetUri, resource);
		} else {
			LOGGER.warn("Name already present in the parent collection.");
			res.responseStatusCode = 4105;
		}
		return res;
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
				LOGGER.warn("Root resource not found for discovery.");
				res.responseStatusCode = 4004;

			}
		}
		res.content = con;
		return res;
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
		}

		if (orgResource != null) {
			res.responseStatusCode = 2004;
			resource.creationTime = orgResource.creationTime;
			resource.lastModifiedTime = getDate();
			resourceTree.put(regularTo, resource);// This omits detailed implementation.
		} else {
			LOGGER.warn("Resource not found. regularTo" + regularTo + " req.to:" + req.to);
			res.responseStatusCode = 4004;
		}

		return res;
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
		ResponsePrimitiveDTO res = new ResponsePrimitiveDTO();
		String to = req.to;
		if (to == null) {
			res.responseStatusCode = 1000;// error
			return res;
		}
		String[] element = to.split("/");
		if (element[1].equals(cseID)) {

			LOGGER.info("NOW prepare to send notification!!!");
			ServiceReference[] rs;
			try {
				rs = context.getServiceReferences(NotificationListener.class.getName(), null);
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
				res.responseStatusCode = 1000;
				return res;
			}
			for (ServiceReference ref : rs) {
				LOGGER.info("symbolic name:" + ref.getBundle().getSymbolicName());
				LOGGER.info("bundle location:" + ref.getBundle().getLocation());

				if (ref.getBundle().getSymbolicName().equals("org.osgi.test.cases.onem2m.service")) {
					NotificationListener lis = (NotificationListener) context.getService(ref);
					lis.notified(req);
				}
			}
			res.responseStatusCode = 2000;
		} else {
			res.responseStatusCode = 1000;// error
		}
		return res;
	}

	private String aeNameBySybolicName(String symbolicName) {
		if (symbolicName.equals("org.osgi.test.cases.onem2m.service")) {
			return "CAE1";
		} else if (symbolicName.equals("org.osgi.test.cases.onem2m.service2")) {
			return "CAE2";
		} else if (symbolicName.equals("org.osgi.test.cases.onem2m.service3")) {
			return "CAE3";
		}
		throw new IllegalArgumentException("unexpected bundleSymbolicName:" + symbolicName);
	}

	private String symbolicNameByAeName(String aeName) {
		if (aeName.equals("CAE1")) {
			return "org.osgi.test.cases.onem2m.service";
		} else if (aeName.equals("CAE2")) {
			return "org.osgi.test.cases.onem2m.service2";
		} else if (aeName.equals("CAE3")) {
			return "org.osgi.test.cases.onem2m.service3";
		}
		throw new IllegalArgumentException("unexpected aeName:" + aeName);

	}
}
