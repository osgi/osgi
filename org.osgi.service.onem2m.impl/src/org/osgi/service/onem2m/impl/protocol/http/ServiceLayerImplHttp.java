package org.osgi.service.onem2m.impl.protocol.http;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.osgi.service.onem2m.impl.protocol.ServiceLayerUtil;
import org.osgi.service.onem2m.impl.serialization.BaseSerialize;
import org.osgi.service.onem2m.impl.serialization.LongShortConverter;
import org.osgi.service.onem2m.impl.serialization.cbor.CborSerialize;
import org.osgi.service.onem2m.impl.serialization.json.JsonSerialize;
import org.osgi.service.onem2m.impl.serialization.xml.XmlSerialize;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServiceLayerImplHttp implements ServiceLayer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLayerImplHttp.class);

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	public static final MediaType XML = MediaType.parse("application/xml; charset=utf-8");
	public static final MediaType CBOR = MediaType.parse("application/cbor; charset=utf-8");
	public static final String JSON_CONTENT_TYPE = "application/vnd.onem2m-res+json;";
	public static final String JSON_ACCEPT = "application/json;";
	public static final String XML_CONTENT_TYPE = "application/vnd.onem2m-res+xml;";
	public static final String XML_ACCEPT = "application/xml;";
	public static final String CBOR_CONTENT_TYPE = "application/vnd.onem2m-res+cbor;";
	public static final String CBOR_ACCEPT = "application/cbor;";
	public static final String HEADER_RESOURCE_TYPE = "ty=";

	private String serialize_type;
	private final String origin;
	private final BaseSerialize serialize;
	private OkHttpClient client = new OkHttpClient();
	private MediaType media;
	private String contentType;
	private String accept;


	public ServiceLayerImplHttp(String origin, BaseSerialize serialize){
		this.origin = origin;
		this.serialize = serialize;
		if(this.serialize.getClass() == JsonSerialize.class){
			serialize_type = ServiceLayerUtil.SERIALIZE_JSON;
			media = JSON;
			contentType = JSON_CONTENT_TYPE;
			accept = JSON_ACCEPT;
		} else if(this.serialize.getClass() == XmlSerialize.class){
			serialize_type = ServiceLayerUtil.SERIALIZE_XML;
			media = XML;
			contentType = XML_CONTENT_TYPE;
			accept = XML_ACCEPT;
		} else if(this.serialize.getClass() == CborSerialize.class){
			serialize_type = ServiceLayerUtil.SERIALIZE_CBOR;
			media = CBOR;
			contentType = CBOR_CONTENT_TYPE;
			accept = CBOR_ACCEPT;
		}
	}

	@Override
	public Promise<ResponsePrimitiveDTO> request(RequestPrimitiveDTO request) {

		Deferred<ResponsePrimitiveDTO> dret = new Deferred<ResponsePrimitiveDTO>();

		Request httpReq = makeCRUDRequest(request);

		if (httpReq != null) {
			client.newCall(httpReq).enqueue(new CRUDCallback(request, dret, serialize_type));
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
	public Promise<ResourceDTO> retrieve(String uri) {
		LOGGER.info("START RETRIEVE");
		LOGGER.debug("Request Uri is [" + uri + "].");

		// Setting RequestPrimitiveDTO
		RequestPrimitiveDTO req = new RequestPrimitiveDTO();
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

		// Setting RequestPrimitiveDTO
		RequestPrimitiveDTO req = new RequestPrimitiveDTO();
		
		if( targetAttributes != null && targetAttributes.size()==0) {
			req.content.listOfURIs = null;
		}else {
			req.content.listOfURIs = targetAttributes;
		}
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

		Request httpReq = makeNotifyRequest(uri, this.origin, notification);

		if (httpReq != null) {
			client.newCall(httpReq).enqueue(new NotifyCallback(dret));
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

	private ResourceDTO parseResponse(Object res) {
		ResourceDTO returnRes = null;

		try {
            // Execute format conversion processing
            returnRes = serialize.responseToResource(res);
			LOGGER.debug(returnRes.toString());

		} catch (Exception e) {
			LOGGER.warn("JSON serialize error.", e);;
		}
		return returnRes;
	}

	private List<String> dataListing(Object res) {
		String strRes = null;
		if(res instanceof byte[]){
			strRes = new String((byte[])res);
		} else {
			strRes = (String) res;
		}

		List<String> resArrayList = new ArrayList<String>();

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

		// Setting of header common parts
		Headers.Builder hb = new Headers.Builder();
		hb.add("X-M2M-Origin", req.from);
		hb.add("X-M2M-RI", String.valueOf(System.currentTimeMillis()));
		hb.add("Accept", accept);

		// Setting different parts by header operation
		RequestBody body = null;
		String contentTypeStr = null;
		MediaType mt = null;
		switch (req.operation) {
		case Create:
		case Update:
			contentTypeStr = contentType + HEADER_RESOURCE_TYPE + req.content.resource.resourceType;
			mt = MediaType.parse(contentTypeStr);
			break;
		case Retrieve:
		case Delete:
			contentTypeStr = contentType;
			break;
		default:
			LOGGER.warn("Operation is invalid.");
			return null;
		}
		hb.add("Content-Type", contentTypeStr);
		Headers header = hb.build();

		// Implementation of operation specific processing
		String json;
		switch (req.operation) {
		case Create:
			try {
				json = (String) serialize.resourceToRequest(req.content.resource);
			} catch (Exception e) {
				LOGGER.error("serialize error.", e);
				return null;
			}
			// Body setting
			body = RequestBody.create(mt, json);
			request = new Request.Builder().url(req.to).headers(header).post(body).build();
			break;
		case Retrieve:
			request = new Request.Builder().url(req.to).headers(header).get().build();
			break;
		case Update:
			try {
				json = (String) serialize.resourceToRequest(req.content.resource);
			} catch (Exception e) {
				LOGGER.error("serialize error.", e);
				return null;
			}
			// Body setting
			body = RequestBody.create(JSON, json);
			request = new Request.Builder().url(req.to).headers(header).put(body).build();
			break;
		case Delete:
			request = new Request.Builder().url(req.to).headers(header).delete().build();
			break;
		default:
			// NOP
		}

		return request;
	}

	private Request makeNotifyRequest(String uri, String from, NotificationDTO notification) {
		Headers header = new Headers.Builder()
				.add("X-M2M-Origin", from)
				.add("X-M2M-RI", String.valueOf(System.currentTimeMillis()))
				.add("Content-Type", contentType)
				.add("Accept", accept).build();

		String json;
		try {
			json = (String) serialize.notificationToRequest(notification);
		} catch (Exception e) {
			LOGGER.warn("Serialize Failed.", e);
			return null;
		}

		RequestBody body = RequestBody.create(media, json);

		return new Request.Builder().url(uri).headers(header).post(body).build();
	}

	class CRUDCallback implements Callback {

		private Deferred<ResponsePrimitiveDTO> deferred;
		private RequestPrimitiveDTO request;
		private String serialize;

		public CRUDCallback(RequestPrimitiveDTO request, Deferred<ResponsePrimitiveDTO> deferred, String serialize) {
			this.request = request;
			this.deferred = deferred;
			this.serialize = serialize;
		}

		@Override
		public void onFailure(Call arg0, IOException arg1) {
			LOGGER.warn("Failure.", arg1);
		}

		@Override
		public void onResponse(Call arg0, Response res) throws IOException {
			ResponsePrimitiveDTO ret = new ResponsePrimitiveDTO();
			ret.content = new PrimitiveContentDTO();

			Object response = null;
			try {
				if(serialize == ServiceLayerUtil.SERIALIZE_JSON
						|| serialize == ServiceLayerUtil.SERIALIZE_XML){
					response = res.body().string();
				} else if(serialize == ServiceLayerUtil.SERIALIZE_CBOR){
					response = res.body().bytes();
				}
			} catch (IOException e) {
				LOGGER.warn("", e);
				throw e;
			}

			ret.responseStatusCode = Integer.parseInt(res.headers().get("X-M2M-RSC"));

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

	class NotifyCallback implements Callback {

		private Deferred<ResponsePrimitiveDTO> deferred;

		public NotifyCallback(Deferred<ResponsePrimitiveDTO> deferred) {
			this.deferred = deferred;
		}

		@Override
		public void onFailure(Call arg0, IOException arg1) {
			LOGGER.warn("Failure.", arg1);
		}

		@Override
		public void onResponse(Call arg0, Response res) throws IOException {
			ResponsePrimitiveDTO ret = new ResponsePrimitiveDTO();

			// Set status code in DTO
			ret.responseStatusCode = Integer.parseInt(res.headers().get("X-M2M-RSC"));
			deferred.resolve(ret);
		}
	}
}