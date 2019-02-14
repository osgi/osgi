package org.osgi.service.onem2m.impl.protocol.coap;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionNumberRegistry;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.AttributeDTO;
import org.osgi.service.onem2m.dto.FilterCriteriaDTO;
import org.osgi.service.onem2m.dto.FilterCriteriaDTO.FilterUsage;
import org.osgi.service.onem2m.dto.NotificationDTO;
import org.osgi.service.onem2m.dto.PrimitiveContentDTO;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO.DiscoveryResultType;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO.Operation;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.service.onem2m.dto.ResponsePrimitiveDTO;
import org.osgi.service.onem2m.impl.serialization.BaseSerialize;
import org.osgi.service.onem2m.impl.serialization.LongShortConverter;
import org.osgi.service.onem2m.impl.serialization.cbor.CborSerialize;
import org.osgi.service.onem2m.impl.serialization.json.JsonSerialize;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

public class ServiceLayerImplCoap implements ServiceLayer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLayerImplCoap.class);

	private final String origin;
	private final BaseSerialize serialize;

	//Option
	private static final int X_M2M_ORIGIN = 256;
	private static final int X_M2M_RI = 257;
	private static final int ONEM2M_TY = 267;
	private static final int ONEM2M_RSC  = 265;

	public ServiceLayerImplCoap(String origin, BaseSerialize serialize){
		this.origin = origin;
		this.serialize = serialize;
	}

	@Override
	public Promise<ResponsePrimitiveDTO> request(RequestPrimitiveDTO request) {

		Deferred<ResponsePrimitiveDTO> dret = new Deferred<ResponsePrimitiveDTO>();

		Request coapReq = makeCRUDRequest(request);

		if (coapReq != null) {
			CoapClient client = null;
			try {
				URI uri = new URI(request.to);
				client = new CoapClient(uri);
				coapReq.setURI(uri);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			LOGGER.debug("--------RequestPrimitiveDTO--------");
			LOGGER.debug(request.toString());
			LOGGER.debug("RequestPrimitiveDTO.to = " + request.to);
			LOGGER.debug("-----------coap.request------------");
			LOGGER.debug("URI = " + coapReq.getURI());
			List<Option> ops = coapReq.getOptions().getOthers();
			for(Option op:ops){
				if(op.getValue() != null){
					LOGGER.debug("Option " + op.toString());
				}
			}
			if(null != coapReq.getPayload()){
				LOGGER.debug("Payload = " + new String(coapReq.getPayload()));
			}
			LOGGER.debug("---------coap.CoapClient-----------");
			LOGGER.debug("URI = " + client.getURI());
			LOGGER.debug("-----------------------------------");
			client.advanced(new CRUDCallback(request, dret), coapReq);
		}

		return dret.getPromise();
	}

	@Override
	public Promise<ResourceDTO> create(String uri, ResourceDTO resource) {
		LOGGER.info("START CREATE");
		LOGGER.debug("Request Uri is [" + uri + "].");

		// When DTO is NULL End without request processing
		if (resource == null) {
			LOGGER.warn("END CREATE");
			return null;
		}

		// Setting RequestPrimitiveDTO
		RequestPrimitiveDTO req = new RequestPrimitiveDTO();
		req.content = new PrimitiveContentDTO();
		req.content.resource = resource;
		req.to = uri;
		req.operation = Operation.Create;

		// Set the source of the request
		req.from = this.origin;

		// Execute request transmission processing
		Promise<ResponsePrimitiveDTO> res = this.request(req);

		LOGGER.info("END CREATE");
		return res.map(p -> p.content.resource);
	}

	@Override
	public Promise<ResourceDTO> retrieve(String uri, ResourceDTO resource) {
		LOGGER.info("START RETRIEVE");
		LOGGER.debug("Request Uri is [" + uri + "].");

		// When DTO is NULL End without request processing
		if (resource == null) {
			LOGGER.warn("END RETRIEVE");
			return null;
		}
		// Setting RequestPrimitiveDTO
		RequestPrimitiveDTO req = new RequestPrimitiveDTO();
		req.content = new PrimitiveContentDTO();
		req.content.resource = resource;
		req.to = uri;
		req.operation = Operation.Retrieve;

		// Set the source of the request
		req.from = this.origin;

		// Execute request transmission processing
		Promise<ResponsePrimitiveDTO> res = this.request(req);

		// RETRIEVE processing end
		LOGGER.info("END RETRIEVE");

		return res.map(p -> p.content.resource);
	}

	@Override
	public Promise<ResourceDTO> retrieve(String uri, List<String> targetAttributes) {
		LOGGER.info("START RETRIEVE");
		LOGGER.debug("Request Uri is [" + uri + "].");

		// When List is NULL or size 0, it terminates without request processing
		if (targetAttributes == null || targetAttributes.size() == 0) {
			LOGGER.warn("END RETRIEVE");
			return null;
		}

		// Setting RequestPrimitiveDTO
		RequestPrimitiveDTO req = new RequestPrimitiveDTO();
		req.content = new PrimitiveContentDTO();
		req.content.listOfURIs = targetAttributes;
		req.operation = Operation.Retrieve;

		// Set the source of the request
		req.from = this.origin;

		uri += "?atrl=";
		for(String param : targetAttributes) {
			uri += param + "+";
		}

		req.to = uri.substring(0, uri.length() - 1);

		LOGGER.debug("Request Uri is [" + req.to + "].");

		// Execute request transmission processing
		Promise<ResponsePrimitiveDTO> res = this.request(req);

		// RETRIEVE processing end
		LOGGER.info("END RETRIEVE");

		return res.map(p -> p.content.resource);
	}

	@Override
	public Promise<ResourceDTO> update(String uri, ResourceDTO resource) {
		LOGGER.info("START UPDATE");
		LOGGER.debug("Request Uri is [" + uri + "].");

		// When DTO is NULL End without request processing
		if (resource == null) {
			LOGGER.warn("END UPDATE");
			return null;
		}

		// Setting RequestPrimitiveDTO
		RequestPrimitiveDTO req = new RequestPrimitiveDTO();
		req.content = new PrimitiveContentDTO();
		req.content.resource = resource;
		req.to = uri;
		req.operation = Operation.Update;

		// Set the source of the request
		req.from = this.origin;

		// Execute request transmission processing
		Promise<ResponsePrimitiveDTO> res = this.request(req);

		LOGGER.info("END UPDATE");

		return res.map(p -> p.content.resource);
	}

	@Override
	public Promise<Boolean> delete(String uri) {
		LOGGER.info("START DELETE");
		LOGGER.debug("Request Uri is [" + uri + "].");

		// Setting RequestPrimitiveDTO
		RequestPrimitiveDTO req = new RequestPrimitiveDTO();
		req.content = new PrimitiveContentDTO();
		req.to = uri;
		req.operation = Operation.Delete;

		// Set the source of the request
		req.from = this.origin;

		// Execute request transmission processing
		Promise<ResponsePrimitiveDTO> res = this.request(req);

		LOGGER.info("END DELETE");

		return res.map(p -> {
			if (p.responseStatusCode >= 2000 && p.responseStatusCode < 3000) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		});
	}

	@Override
	public Promise<List<String>> discovery(String uri, FilterCriteriaDTO fc) {
		// Setting RequestPrimitiveDTO
		RequestPrimitiveDTO req = new RequestPrimitiveDTO();
		req.content = new PrimitiveContentDTO();
		req.operation = Operation.Retrieve;
		req.to = uri;
		req.filterCriteria = fc;
		req.discoveryResultType = DiscoveryResultType.structured;

		// Set the source of the request
		req.from = this.origin;

		LOGGER.info("START DISCOVERY");
		LOGGER.debug("Request Uri(BEFORE) is [" + uri + "].");

		try {
			// Add parameters to URI
			req.to = discoveryFilter(req);
		} catch (Exception e) {
			LOGGER.warn("Create filter error.", e);
			return null;
		}

		LOGGER.debug("Request Uri(AFTER) is [" + req.to + "].");

		// Execute request transmission processing
		Promise<ResponsePrimitiveDTO> res = this.request(req);

		LOGGER.info("END DISCOVERY");

		return res.map(p -> p.content.listOfURIs);
	}

	@Override
	public Promise<List<String>> discovery(String uri, FilterCriteriaDTO fc, DiscoveryResultType drt) {
		LOGGER.info("START DISCOVERY_RESULT_TYPE");

		// Setting RequestPrimitiveDTO
		RequestPrimitiveDTO req = new RequestPrimitiveDTO();
		req.content = new PrimitiveContentDTO();
		req.operation = Operation.Retrieve;
		req.to = uri;
		req.filterCriteria = fc;
		req.discoveryResultType = drt;

		// Set the source of the request
		req.from = this.origin;

		LOGGER.debug("Request Uri(BEFORE) is [" + uri + "].");

		try {
			// Add parameters to URI
			req.to = discoveryFilter(req);
		} catch (Exception e) {
			LOGGER.warn("Create filter error.", e);
			return null;
		}

		LOGGER.debug("Request Uri(AFTER) is [" + req.to + "].");

		// Execute request transmission processing
		Promise<ResponsePrimitiveDTO> res = this.request(req);

		LOGGER.info("END DISCOVERY_RESULT_TYPE");

		return res.map(p -> p.content.listOfURIs);
	}

	@Override
	public Promise<Boolean> notify(String uri, NotificationDTO notification) {
		// Start NOTIFY request transmission processing
		LOGGER.info("START NOTIFY");
		LOGGER.debug("Request Uri is [" + uri + "].");

		Deferred<ResponsePrimitiveDTO> dret = new Deferred<ResponsePrimitiveDTO>();

		CoapClient coapReq = makeNotifyRequest(uri, this.origin, notification);

		if (coapReq != null) {
			byte[] postPayload;
			try {
				postPayload = (byte[]) serialize.notificationToRequest(notification);
			} catch (Exception e) {
				LOGGER.warn("CborSerialize Failed.", e);
				return dret.getPromise().map(  r -> Boolean.FALSE );
			}
			coapReq.post(new NotifyCallback(dret), postPayload, -1);
		}

		// NOTIFY request transmission processing end
		LOGGER.info("END NOTIFY");

		return dret.getPromise().map(  r -> {
			if( r.responseStatusCode >=2000 && r.responseStatusCode< 3000){
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		});
	}

	private ResourceDTO parseResponse(byte[] byteRes) {
		ResourceDTO returnRes = null;

	    try {
	        if(this.serialize.getClass() == JsonSerialize.class){
	            // Execute format conversion processing
	            returnRes = serialize.responseToResource(new String(byteRes));

	        } else if(this.serialize.getClass() == CborSerialize.class){
	            // Execute format conversion processing
	            returnRes = serialize.responseToResource(byteRes);

	        }

	        // Confirm the converted character string
	        LOGGER.debug(returnRes.toString());

	    } catch (Exception e) {
	        LOGGER.warn("JSON serialize error.", e);
	    }

		return returnRes;
	}

	private List<String> dataListing(byte[] byteRes) {
		ObjectMapper mapperCbor = new ObjectMapper(new CBORFactory());
		List<String> resArrayList = new ArrayList<String>();

		String strRes;
		try {
			strRes = mapperCbor.readTree(byteRes).textValue();
		} catch (IOException e) {
			LOGGER.warn("target data is not cbor.", e);
			return null;
		}

		// Delete border
		strRes = strRes.substring(strRes.indexOf("[") + 1);
		strRes = strRes.substring(0, (strRes.lastIndexOf("]") - 1));

		// Delete unnecessary character string · space
		strRes = strRes.replace("\"", "").replace(" ", "");

		// Put the edited character string in the list
		String resArr[] = strRes.split(",", 0);
		resArrayList = Arrays.asList(resArr);

		LOGGER.debug("<Response Data List>");

		for (String str : resArrayList) {
			LOGGER.debug("[" + str + "]");
		}
		return resArrayList;
	}

	public static String discoveryFilter(RequestPrimitiveDTO req) {

		if (req.to == null) {
			LOGGER.warn("URI is NULL");
			return req.to;
		}

		String ex = req.to;

		try {
			Boolean questionFlg = false;
			Boolean filterFlg = req.filterCriteria != null ? true : false;
			Boolean resultTypeFlg = req.discoveryResultType != null ? true : false;

			if (filterFlg) {
				// Get field of filterCriteria
				FilterCriteriaDTO fc = req.filterCriteria;
				Field[] field = fc.getClass().getFields();

				// Process only the number of items in the field
				for (Field s : field) {
					if (s.get(fc) != null) {
						if (!questionFlg) {
							ex += "?";
							questionFlg = true;
						}
						// Whether the type is List
						if (s.getType().equals(List.class)) {
							Type t = s.getGenericType();
							if (t instanceof ParameterizedType) {
								ParameterizedType paramType = (ParameterizedType) t;
								Type[] argTypes = paramType.getActualTypeArguments();
								if (argTypes.length > 0) {
									Type at = argTypes[0];
									if (at.equals(String.class)) {
										for (String str : (List<String>) s.get(fc)) {
											ex += LongShortConverter.l2s(s.getName()) + "=" + str + "&";
										}
										continue;
									} else if (at.equals(Integer.class)) {
										for (Integer strInt : (List<Integer>) s.get(fc)) {
											ex += LongShortConverter.l2s(s.getName()) + "=" + strInt.toString() + "&";
										}
										continue;
									} else if (at.equals(AttributeDTO.class)) {
										for (AttributeDTO ad : (List<AttributeDTO>) s.get(fc)) {
											ex += LongShortConverter.l2s(s.getName()) + "=" + ad.name + "&";
										}
										continue;
									}
								}
							}
						}
						// Whether it is filterOperation
						else if ("filterOperation".equals(s.getName())) {
							ex += LongShortConverter.l2s(s.getName()) + "=" + fc.filterOperation.getValue();
						}
						// Whether it is filterUsage
						else if ("filterUsage".equals(s.getName())) {
							ex += LongShortConverter.l2s(s.getName()) + "=" + fc.filterUsage.getValue();
						}

						else {
							for (int i = 0; field.length > i; i++) {
								if (field[i].getName().equals(s.getName())) {
									ex += LongShortConverter.l2s(s.getName()) + "=" + s.get(fc);
									break;
								}
								else if (field.length == (i + 1)) {
									LOGGER.warn("This Column is NOT COVERED to \"ChangeName => \" " + s.getName());
								}
							}
						}
					} else {
						// If the value is NULL, go to the next item
						continue;
					}
					ex += "&";
				}
			}

			// Setting the ResultType and the URI that added the query more than once
			if (resultTypeFlg && questionFlg) {
				ex += "drt=" + req.discoveryResultType.getValue();
			}
			// Setting of ResultType setting
			else if (resultTypeFlg) {
				ex += "?drt=" + req.discoveryResultType.getValue();
			}
			// URI with no ResultType setting and one or more queries added
			else if (questionFlg) {
				ex = ex.substring(0, ex.length() - 1);
			}
		} catch (Exception e) {
			LOGGER.warn("Create filter error.", e);
		}
		return ex;
	}

	private Request makeCRUDRequest(RequestPrimitiveDTO req) {
		Request request = null;
		switch(req.operation){
		case Create:
			request = new Request(Code.POST);
			request.getOptions().addOption(new Option(ONEM2M_TY, req.content.resource.resourceType));
			break;
		case Retrieve:
			request = new Request(Code.GET);
			break;
		case Update:
			request = new Request(Code.PUT);
			request.getOptions().addOption(new Option(ONEM2M_TY, req.content.resource.resourceType));
			break;
		case Delete:
			request = new Request(Code.DELETE);
			break;
		}
		request.getOptions().addOption(new Option(X_M2M_ORIGIN, req.from));
		request.getOptions().addOption(new Option(X_M2M_RI, String.valueOf(System.currentTimeMillis())));

		if(serialize.getClass() == CborSerialize.class){

			if(req.operation == Operation.Create || req.operation == Operation.Update){
				byte[] postPayload = null;
				try {
					postPayload = (byte[]) serialize.resourceToRequest(req.content.resource);
				} catch (Exception e) {
					LOGGER.warn("CborSerialize Failed.", e);
					return null;
				}
				request.setPayload(postPayload);
			}
		} else if(serialize.getClass() == JsonSerialize.class){

			if(req.operation == Operation.Create || req.operation == Operation.Update){

				String postPayload = null;
				try {
					postPayload = (String) serialize.resourceToRequest(req.content.resource);
				} catch (Exception e) {
					LOGGER.warn("JsonSerialize Failed.", e);
					return null;
				}
				request.setPayload(postPayload);
			}
		}




		return request;

	}

	private CoapClient makeNotifyRequest(String uri, String from, NotificationDTO notification) {

		CoapClient request = new CoapClient(uri);

		OptionSet options = new OptionSet();
		options.addOption(new Option(X_M2M_ORIGIN, from));
		options.addOption(new Option(X_M2M_RI, String.valueOf(System.currentTimeMillis())));
		options.addOption(new Option(OptionNumberRegistry.ACCEPT, MediaTypeRegistry.APPLICATION_CBOR));

		return request;

	}

	class CRUDCallback implements CoapHandler {

		private Deferred<ResponsePrimitiveDTO> deferred;
		private RequestPrimitiveDTO request;

		public CRUDCallback(RequestPrimitiveDTO request, Deferred<ResponsePrimitiveDTO> deferred) {
			this.request = request;
			this.deferred = deferred;
		}

		@Override
		public void onError() {
			LOGGER.warn("Failure.");
		}

		@Override
		public void onLoad(CoapResponse res) {
			ResponsePrimitiveDTO ret = new ResponsePrimitiveDTO();
			ret.content = new PrimitiveContentDTO();

			byte[] response = res.getPayload();

			List<Option> opList = res.getOptions().getOthers();
			for(Option op : opList){
				if(op.getNumber() == ONEM2M_RSC){
					ret.responseStatusCode = op.getIntegerValue();
				}
			}

			switch (request.operation) {
			case Create:
			case Update:
				ret.content.resource = parseResponse(response);
				break;
			case Retrieve:
				// DISCOVERY
				if (request.filterCriteria != null && request.filterCriteria.filterUsage == FilterUsage.DiscoveryCriteria) {
					ret.content.listOfURIs = dataListing(response);
				}
				// RETRIEVE
				else {
					ret.content.resource = parseResponse(response);
				}
				break;
			case Delete:
				// NOP
				break;
			default:
				LOGGER.warn("Operation is invalid.");
			}

			deferred.resolve(ret);
		}

	}

	class NotifyCallback implements CoapHandler {

		private Deferred<ResponsePrimitiveDTO> deferred;

		public NotifyCallback(Deferred<ResponsePrimitiveDTO> deferred) {
			this.deferred = deferred;
		}

		@Override
		public void onError() {
			LOGGER.warn("Failure.");
		}

		@Override
		public void onLoad(CoapResponse res) {
			ResponsePrimitiveDTO ret = new ResponsePrimitiveDTO();

			// Set status code in DTO
			List<Option> opList = res.getOptions().getOthers();
			for(Option op : opList){
				if(op.getNumber() == ONEM2M_RSC){
					ret.responseStatusCode = op.getIntegerValue();
				}
			}
			deferred.resolve(ret);
		}
	}
}