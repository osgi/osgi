package org.osgi.service.onem2m.impl.serialization.json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.onem2m.dto.NotificationDTO;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.service.onem2m.impl.serialization.BaseSerialize;
import org.osgi.service.onem2m.impl.serialization.LongShortConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerialize implements BaseSerialize{

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonSerialize.class);

	@Override
	public ResourceDTO responseToResource(Object orgJson) throws Exception {

		LOGGER.info("Start jsonToResource");
		String json = (String) orgJson;

		ResourceDTO returnRes = new ResourceDTO();
		ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map = new LinkedHashMap<>();
        Map<String,Object> attr = new HashMap<String, Object>();
        Object value = null;

		try {
            map = mapper.readValue(json, new TypeReference<LinkedHashMap<String,Object>>(){});
    		String key = map.keySet().iterator().next();
    		Map<String,Object> resMap = (Map<String, Object>) map.get(key);
    		Set<String> resKeys = resMap.keySet();

			Field[] varArray = returnRes.getClass().getFields();

			for(String resKey : resKeys){
				if (resMap.get(resKey) instanceof ArrayList) {
					ArrayList<Object> list = new ArrayList<Object>();

					// fill the value into List
					for(int i = 0; i < ((ArrayList<?>)resMap.get(resKey)).size(); i++){
						list.add((((ArrayList<?>)resMap.get(resKey)).get(i)));
					}
					value = list;
				}else{
					value = resMap.get(resKey);
				}

				// DTO is set using Reflection.
				for(int i = 0; i < varArray.length; i++){
					Field var = varArray[i];
					// if variable name match of Long-Name, set value into DTO.
					if(var.getName().equals(LongShortConverter.s2l(resKey))){
						var.set(returnRes, value);
						break;
					}

					// if variable name does not match of Long-Name, set value into DTO after filling into "attribute".
					if(i == varArray.length - 1){
						attr.put(LongShortConverter.s2l(resKey), attrCheck(value));
					}
				}
			}

			// set "attribute"
			for(int i = 0; i < varArray.length; i++){
				if(varArray[i].getName().equals("attribute")){
					varArray[i].set(returnRes, attr);
				}
			}

		} catch (Exception e) {
			LOGGER.warn("error!");
			e.printStackTrace();
		}

		LOGGER.info("End jsonToResource");
		LOGGER.debug(returnRes.toString());

		return returnRes;
	}

	private Object attrCheck(Object value){
		Object val = null;
		if(value instanceof Map){
			val = mapCheck((Map<String, Object>)value);
		} else if(value instanceof List){
			val = listCheck((List<Object>)value);
		} else {
			val = value;
		}
		return val;
	}

	private Map<String,Object> mapCheck(Map<String,Object> value){
		Map<String, Object> valMap = (Map) value;
		Set<String> valKeys = valMap.keySet();
		Map<String, Object> valAttr = new HashMap<String, Object>();

		for(String valKey : valKeys){
			valAttr.put(LongShortConverter.s2l(valKey), attrCheck(valMap.get(valKey)));
		}

		return valAttr;
	}

	private List<Object> listCheck(List<Object> value){
		List<Object> valList = (List<Object>) value;
		List<Object> list = new ArrayList<Object>();

		for(Object val : valList){
			list.add(attrCheck(val));
		}

		return list;
	}

	@Override
	public String resourceToRequest(ResourceDTO dto) throws Exception  {

		String jsonRes = "";

		String resourceTypeName = null;
		switch(dto.resourceType){
			case 2:
				resourceTypeName = "m2m:ae";
				break;
			case 3:
				resourceTypeName = "m2m:cnt";
				break;
			case 4:
				resourceTypeName = "m2m:cin";
				break;
			case 23:
				resourceTypeName = "m2m:sub";
				break;
		}

		Field[] varArray = dto.getClass().getFields();
		// create the JSON format with Reflection.
		for(int i = 0; i < varArray.length; i++){
			Field var = varArray[i];
			// judge whether the attribute(s) will be a factor causing an error, if it is included in request resource.
			if(var.getName().equals("resourceType")
					|| var.getName().equals("resourceID")
					|| var.getName().equals("parentID")
					|| var.getName().equals("creationTime")
					|| var.getName().equals("lastModifiedTime")
					){
				continue;
			}

			if(!var.getName().equals("attribute") && !var.getName().equals("labels")){
			// convert variable name(Long-Name) to Short-Name to use as key
				String key = LongShortConverter.l2s(var.getName());

				Object val = null;
				var.setAccessible(true);
				try {
					val = var.get(dto);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if(val != null){
					if(var.getType().equals(Integer.class)){
						jsonRes += "\"" + key  + "\": " + val + ", ";
					} else {
						jsonRes += "\"" + key  + "\": \"" + val + "\", ";
					}
				}

			} else if(var.getName().equals("labels")) {
				Object val = null;
				var.setAccessible(true);
				try {
					var.setAccessible(true);
					val = var.get(dto);
				} catch (Exception e) {
					e.printStackTrace();
				}

				List<String> list = (List<String>)val;
				if(list != null && list.size() >= 0){
					jsonRes += "\"" + LongShortConverter.l2s(var.getName()) + "\": [";
					for(int j = 0; j < list.size(); j++){
						jsonRes += "\"" + list.get(j) + "\", ";
					}
					jsonRes =  jsonRes.substring(0, jsonRes.length() - 2);
					jsonRes += "], ";
				}

			} else if (var.getName().equals("attribute")){

				Object val = null;
				var.setAccessible(true);
				try {
					var.setAccessible(true);
					val = var.get(dto);
				} catch (Exception e) {
					e.printStackTrace();
				}

				Map<String, Object> map = (Map<String, Object>)val;
				if(map == null){
					continue;
				}
				Set<String> keys = map.keySet();

				for(String key: keys){
					if(key.equals("currentNrOfInstances")
							|| key.equals("currentByteSize")
							|| key.equals("accessControlPolicyIDs")
							){
						continue;
					}

					if(!(map.get(key) instanceof ArrayList)){
						jsonRes += "\"" + LongShortConverter.l2s(key) + "\": \"" + map.get(key) + "\", ";
					} else {

						if(map.get(key) != null && ((ArrayList)map.get(key)).size() >= 0){
							jsonRes += "\"" + LongShortConverter.l2s(key) + "\": [";
							for(int j = 0; j < ((ArrayList)map.get(key)).size(); j++){
								jsonRes += "\"" + ((ArrayList)map.get(key)).get(j) + "\", ";
							}
							jsonRes =  jsonRes.substring(0, jsonRes.length() - 2);
							jsonRes += "], ";
						}
					}
				}
			}

		}

		jsonRes =  jsonRes.substring(0, jsonRes.length() - 2);
		jsonRes = "{\"" + resourceTypeName + "\":{" + jsonRes + "}}";

		LOGGER.debug(jsonRes);

		LOGGER.info("End resourceToJson");

		return jsonRes;
	}

	public String notificationToRequest(NotificationDTO notification){
		String jsonRes = "";
		String resourceTypeName = "m2m:sgn";

		Field[] varArray = notification.getClass().getFields();
		for(int i = 0; i < varArray.length; i++){
			Field var = varArray[i];

			// convert variable name(Long-Name) to Short-Name to use as key
			String key = LongShortConverter.l2s(var.getName());

			Object val = null;
			var.setAccessible(true);
			try {
				val = var.get(notification);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if(val != null){
				if(var.getType().equals(Integer.class)){
					jsonRes += "\"" + key  + "\" : " + val + ", ";
				} else if(var.getType().equals(String.class)) {
					jsonRes += "\"" + key  + "\" : \"" + val + "\", ";
				} else if(var.getType().equals(Boolean.class)) {
					if((Boolean)val){
						jsonRes += "\"" + key  + "\" : true, ";
					} else {
						jsonRes += "\"" + key  + "\" : false, ";
					}
				} else if(var.getType().equals(Map.class)) {
					jsonRes += "\"" + key  + "\" : {";

					Map<String, Object> map = (Map<String, Object>)val;
					Set<String> mapKeys = map.keySet();

					for(String mapKey: mapKeys){

						if(map.get(mapKey) instanceof String){
							jsonRes += "\"" + LongShortConverter.l2s(mapKey) + "\" : \"" + map.get(mapKey) + "\", ";
						} else 	if(map.get(mapKey) instanceof Integer){
							jsonRes += "\"" + LongShortConverter.l2s(mapKey) + "\" : " + map.get(mapKey) + ", ";
						} else if(map.get(mapKey) instanceof Map){

							if(map.get(mapKey) != null && ((ArrayList)map.get(mapKey)).size() >= 0){
								jsonRes += "\"" + LongShortConverter.l2s(mapKey) + "\": [";
								for(int j = 0; j < ((ArrayList)map.get(mapKey)).size(); j++){
									jsonRes += "\"" + ((ArrayList)map.get(mapKey)).get(j) + "\", ";
								}
								jsonRes =  jsonRes.substring(0, jsonRes.length() - 2);
								jsonRes += "], ";
							}
						}
					}
					jsonRes =  jsonRes.substring(0, jsonRes.length() - 2);
					jsonRes += "}, ";
				}
			}
		}

		jsonRes =  jsonRes.substring(0, jsonRes.length() - 2);
		jsonRes = "{\"" + resourceTypeName + "\":{" + jsonRes + "}}";

		return jsonRes;
	}

}
