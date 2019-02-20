package org.osgi.service.onem2m.impl.serialization.cbor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.onem2m.dto.NotificationDTO;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.service.onem2m.impl.serialization.BaseSerialize;
import org.osgi.service.onem2m.impl.serialization.LongShortConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

public class CborSerialize implements BaseSerialize{

	private static final Logger LOGGER = LoggerFactory.getLogger(CborSerialize.class);

	@Override
	public Object resourceToRequest(ResourceDTO dto) throws Exception {
		LOGGER.info("------START ResourceToCbor------");

		Object val = null;
		ObjectMapper mapper = new ObjectMapper(new CBORFactory());
		ObjectNode oNodes = mapper.createObjectNode();

		// create the beginning part of the resource(s) which is in JSON format.
		String resourceTypeName = null;
		switch (dto.resourceType) {
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
		for (int i = 0; i < varArray.length; i++) {
			Field var = varArray[i];
			// judge whether the attribute(s) will be a factor causing an error, if it is included in request resource.
			if (var.getName().equals("resourceType") || var.getName().equals("resourceID")
					|| var.getName().equals("parentID") || var.getName().equals("creationTime")
					|| var.getName().equals("lastModifiedTime")) {
				continue;
			}

			// case of the attribute name except for "attribute" or "labels".
			if (!var.getName().equals("attribute") && !var.getName().equals("labels")) {
				var.setAccessible(true);
				try {
					// Long-Name to Short-Name.
					String key = LongShortConverter.l2s(var.getName());
					val = var.get(dto);
					oNodes.put(key, val.toString());

				} catch (Exception e) {
					e.printStackTrace();
				}
				continue;

				// case of attribute name is "labels".
			} else if (var.getName().equals("labels")) {
				try {
					ArrayNode arNode = oNodes.putArray(LongShortConverter.l2s(var.getName()));
					var.setAccessible(true);
					val = var.get(dto);
					if (val != null) {
						List<String> list = (List<String>) val;
						for (String str : list) {
							arNode.add(str);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				continue;

				// case of attribute name is "attribute"
			} else if (var.getName().equals("attribute")) {
				try {
					var.setAccessible(true);
					val = var.get(dto);
					Map<String, Object> map = (Map<String, Object>) val;
					Set<String> keys = map.keySet();
					for (String key : keys) {
						if (val != null) {
							if (!key.equals("currentNrOfInstances") && !key.equals("currentByteSize")
									&& !key.equals("accessControlPolicyIDs")) {
								String shortName = LongShortConverter.l2s(key);
								if (shortName == null) {
									 LOGGER.warn("This Attribute is not exist : " + key);
									continue;
								}
								oNodes.put(LongShortConverter.l2s(key), map.get(key).toString());
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		String cborReqStr = "{\"" + resourceTypeName + "\":" + oNodes + "}";

		LOGGER.info("Request Value : " + cborReqStr);

		byte[] bytes = mapper.writeValueAsBytes(cborReqStr);

		LOGGER.info("------END ResourceToCbor------");

		return bytes;
	}

	@Override
	public ResourceDTO responseToResource(Object orgResponse) throws Exception {
		LOGGER.info("------START CborToResource------");
		byte[] response = (byte[])orgResponse;

		ResourceDTO returnRes = new ResourceDTO();
		ObjectMapper mapperCbor = new ObjectMapper(new CBORFactory());
		ObjectMapper mapperJson = new ObjectMapper();
		List<UnknownObject> jsonlist = new ArrayList<>();

		try {

			mapperCbor.readTree(response).elements().next();
			String resStr = mapperCbor.readTree(response).elements().next().toString();

			LOGGER.info("Response Value : " + resStr);

			// preparation for converting JSON to Java-Object.
			JsonNode node = mapperJson.readTree(resStr);

			// judge whether value in the attribute is an array or not.
			// after then, save the value temporarily in "UnknownObject" Subclass.
			if (node.isArray()) {
				// case of the value which is an array.
				jsonlist.addAll(Arrays.asList(mapperJson.readValue(node.traverse(), UnknownObject[].class)));
			} else {
				// case of the value which is not an array,
				jsonlist.add(mapperJson.readValue(node.traverse(), UnknownObject.class));
			}

			Map<String, Object> attr = new HashMap<String, Object>();
			Field[] varArray = returnRes.getClass().getFields();

			// set temprary saving value in "Unknown-Object" Subclass into "ResourceDTO"
			for (UnknownObject data : jsonlist) {
				Map<String, Object> prop = data.getAnyProperties();
				Set<String> keys = data.getAnyProperties().keySet();
				Object value = null;

				for (String key : keys) {

					// judge whether value in the attribute is an array or not.
					if (prop.get(key) instanceof ArrayList) {
						ArrayList<Object> list = new ArrayList<Object>();

						// fill the value into List
						for (int i = 0; i < ((ArrayList<?>) prop.get(key)).size(); i++) {
							list.add((((ArrayList<?>) prop.get(key)).get(i)));
						}
						value = list;
					} else {
						value = prop.get(key);
					}

					// DTO is set using Reflection.
					for (int i = 0; i < varArray.length; i++) {
						Field var = varArray[i];
						// if variable name does not match of Long-Name, set value into DTO.
						if (var.getName().equals(LongShortConverter.s2l(key))) {
							var.set(returnRes, value);
							break;
						}

						// if variable name does not match of Long-Name, set value into DTO after filling into "attribute".
						if (i == varArray.length - 1) {
							if (value instanceof Map) {
								Map<String, Object> attrMap = new HashMap<String, Object>();
								Set<String> attrKeys = ((Map<String, Object>) value).keySet();
								for (String attrKey : attrKeys) {
									attrMap.put(LongShortConverter.s2l(attrKey), ((Map) value).get(attrKey));
								}
								attr.put(LongShortConverter.s2l(key), attrMap);
							} else {
								attr.put(LongShortConverter.s2l(key), value);
							}
						}
					}
				}
			}

			// set "attribute"
			for (int i = 0; i < varArray.length; i++) {
				if (varArray[i].getName().equals("attribute")) {
					varArray[i].set(returnRes, attr);
				}
			}

		} catch (Exception e) {
			 LOGGER.warn("error!");
			e.printStackTrace();
		}

		 LOGGER.info("------END CborToResource------");

		return returnRes;

	}

	@Override
	public Object notificationToRequest(NotificationDTO notification) throws Exception{
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

		ObjectMapper mapper = new ObjectMapper(new CBORFactory());
		byte[] bytes = mapper.writeValueAsBytes(jsonRes);

		return bytes;
	}

}

class UnknownObject {
	@JsonIgnore
	private Map<String, Object> anyProperties = new HashMap<>();

	@JsonAnyGetter
	public Map<String, Object> getAnyProperties() {
		return this.anyProperties;
	}

	@JsonAnySetter
	public void setAnyProperties(String name, Object value) {
		this.anyProperties.put(name, value);
	}
}