package org.osgi.test.cse.toyCse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.onem2m.dto.FilterCriteriaDTO.FilterUsage;
import org.osgi.service.onem2m.dto.PrimitiveContentDTO;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.service.onem2m.dto.ResponsePrimitiveDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CseService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CseService.class);

	private Map<String, ResourceDTO> cse = new HashMap<String, ResourceDTO>();
	private int id = 0;

	public CseService(String uri, ResourceDTO cseBase){
		String now = getDate();
		cseBase.resourceID = String.valueOf(id);
		cseBase.creationTime = now;
		cseBase.lastModifiedTime = now;
		cse.put(uri, cseBase);
	}

	public ResponsePrimitiveDTO create(RequestPrimitiveDTO req){
		ResponsePrimitiveDTO res = new ResponsePrimitiveDTO();
		PrimitiveContentDTO con = new PrimitiveContentDTO();
		String msg = null;
		res.to = req.to;
		res.from = req.from;
		res.requestIdentifier = req.requestIdentifier;
		res.content = con;

		ResourceDTO resource = req.content.resource;

		if(!cse.containsKey(req.to)){
			msg = "Resource not found.";
			LOGGER.warn(msg);
			res.responseStatusCode = 4004;
			return res;
		}

		String targetUri = req.to + "/" + resource.resourceName;

		if(!cse.containsKey(targetUri)){
			resource.resourceID = String.valueOf(id++);
			resource.creationTime = getDate();
			resource.lastModifiedTime = getDate();
			con.resource = resource;
			res.content = con;
			res.responseStatusCode = 2001;
			cse.put(targetUri, resource);
		} else {
			LOGGER.warn("Name already present in the parent collection.");
			res.responseStatusCode = 4105;
		}

		return res;
	}

	public ResponsePrimitiveDTO retrieve(RequestPrimitiveDTO req){
		ResponsePrimitiveDTO res = new ResponsePrimitiveDTO();
		PrimitiveContentDTO con = new PrimitiveContentDTO();
		res.to = req.to;
		res.from = req.from;
		res.requestIdentifier = req.requestIdentifier;

		//Retrieve
		if(req.filterCriteria == null
				|| req.filterCriteria.filterUsage != FilterUsage.DiscoveryCriteria){

			if(cse.containsKey(req.to)){
				res.responseStatusCode = 2000;
				con.resource = cse.get(req.to);
			} else {
				LOGGER.warn("Resource not found.");
				res.responseStatusCode = 4004;
			}

		} else {
		//Discovery
			String target = req.to.split("\\?")[0];
			if(cse.containsKey(target)){
				List<String> uril = new ArrayList<String>();
				Set<String> keys = cse.keySet();
				for(String key:keys){
					if(key.contains(target)){
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

	public ResponsePrimitiveDTO update(RequestPrimitiveDTO req){
		ResponsePrimitiveDTO res = new ResponsePrimitiveDTO();
		PrimitiveContentDTO con = new PrimitiveContentDTO();
		res.to = req.to;
		res.from = req.from;
		res.requestIdentifier = req.requestIdentifier;
		res.content = con;

		ResourceDTO resource = req.content.resource;
		ResourceDTO orgResource = null;

		if(cse.containsKey(req.to)){
			orgResource = cse.get(req.to);
		}

		if(orgResource != null){
			res.responseStatusCode = 2004;
			resource.creationTime = orgResource.creationTime;
			resource.lastModifiedTime = getDate();
			cse.put(req.to, resource);
		} else {
			LOGGER.warn("Resource not found.");
			res.responseStatusCode = 4004;
		}

		return res;
	}

	public ResponsePrimitiveDTO delete(RequestPrimitiveDTO req){
		ResponsePrimitiveDTO res = new ResponsePrimitiveDTO();
		res.to = req.to;
		res.from = req.from;
		res.requestIdentifier = req.requestIdentifier;

		Set<String> keys =  cse.keySet();
		boolean valid = false;
		String msg = null;
		for(String key: keys){
			if(req.to.equals(key)){
				valid = true;
				cse.remove(key);
				break;
			}
		}

		if(valid){
			res.responseStatusCode = 2002;
		} else {
			msg = "Resource not found.";
			LOGGER.warn(msg);
			res.responseStatusCode = 4004;
		}

		return res;
	}


	public String getDate(){
		Calendar cl = Calendar.getInstance();
		String date;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
		date = sdf.format(cl.getTime());
		return date;

	}
}
