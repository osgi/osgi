package org.osgi.impl.service.onem2m.protocol.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.onem2m.OneM2MException;
import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.FilterCriteriaDTO;
import org.osgi.service.onem2m.dto.NotificationDTO;
import org.osgi.service.onem2m.dto.PrimitiveContentDTO;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO.DesiredIdentifierResultType;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO.Operation;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.service.onem2m.dto.ResponsePrimitiveDTO;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("boxing")
public class ServiceLayerImplService implements ServiceLayer {
	static final Logger		LOGGER	= LoggerFactory
			.getLogger(ServiceLayerImplService.class);
	CseService					cse;
	private final String origin;
	@SuppressWarnings("unused")
	private BundleContext context;
	@SuppressWarnings("unused")
	private Bundle bundleFor;

	public ServiceLayerImplService(String origin, BundleContext context, Bundle bundle) {
		this.cse = new CseService(context, bundle);
		bundleFor = bundle;
		this.origin = origin;
		this.context = context;
	}

	class LowLevelThread extends Thread {
		Deferred<ResponsePrimitiveDTO> dret;
		RequestPrimitiveDTO request;

		LowLevelThread(RequestPrimitiveDTO request, Deferred<ResponsePrimitiveDTO> dret) {
			this.dret = dret;
			this.request = request;
		}

		@Override
		public void run() {
			ResponsePrimitiveDTO ret = null;
			try {
				switch (request.operation) {
				case Create:
					ret = cse.create(request);
					break;

				case Retrieve:
					ret = cse.retrieve(request);
					break;

				case Update:
					ret = cse.update(request);
					break;

				case Delete:
					ret = cse.delete(request);
					break;

				case Notify:
					ret = cse.notify(request);
					break;
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
				LOGGER.warn("Exception Caught:" + e);
				dret.fail(e);
				return;
			}
			dret.resolve(ret);
		}
	}

	@Override
	public Promise<ResponsePrimitiveDTO> request(RequestPrimitiveDTO request) {

		Deferred<ResponsePrimitiveDTO> dret = new Deferred<ResponsePrimitiveDTO>();

		Thread t = new LowLevelThread(request, dret);
		t.start();

		return dret.getPromise();
	}

	class HighLevelThread extends Thread {
		Deferred<ResourceDTO> deferredResource;
		RequestPrimitiveDTO request;

		HighLevelThread(RequestPrimitiveDTO request, Deferred<ResourceDTO> dres) {
			this.request = request;
			this.deferredResource = dres;
		}

		@Override
		public void run() {
			ResponsePrimitiveDTO ret = null;
			try {
				switch (request.operation) {
				case Create:
					ret = cse.create(request);
					break;

				case Retrieve:
					ret = cse.retrieve(request);
					break;

				case Update:
					ret = cse.update(request);
					break;

				case Delete:
					ret = cse.delete(request);
					break;

				case Notify:
					ret = cse.notify(request);
					break;
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
				LOGGER.warn("Exception Caught:" + e);
				deferredResource.fail(e);
				return;
			}
			if (ret.responseStatusCode >= 2000 && ret.responseStatusCode < 3000) {
				deferredResource.resolve(ret.content.resource);
			} else {
				LOGGER.warn("error code:" + ret.responseStatusCode);
				OneM2MException ex = new OneM2MException(
						"Unknown(Not implemented yet)", ret.responseStatusCode);
				deferredResource.fail(ex);
			}
		}
	}

	@Override
	public Promise<ResourceDTO> create(String uri, ResourceDTO resource) {
		LOGGER.info("START CREATE uri:" + uri + " resource:" + resource);

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
		Deferred<ResourceDTO> d = new Deferred<ResourceDTO>();

		Thread thread = new HighLevelThread(req, d);
		thread.start();

		LOGGER.info("END CREATE");
		return d.getPromise();
		// return res.map(p -> p.content.resource);
	}

	@Override
	public Promise<ResourceDTO> retrieve(String uri) {
		LOGGER.info("START RETRIEVE uri:" + uri);

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
		LOGGER.info("START RETRIEVE uri:" + uri + " targetAttribute:" + targetAttributes);

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
		for (String param : targetAttributes) {
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
		LOGGER.info("START UPDATE uri:" + uri + " resource" + resource);

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
		LOGGER.info("START DELETE uri:" + uri);

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
		return discovery(uri, fc, DesiredIdentifierResultType.structured);
	}

	@Override
	public Promise<List<String>> discovery(String uri, FilterCriteriaDTO fc,
			DesiredIdentifierResultType drt) {
		LOGGER.info("START DISCOVERY_RESULT_TYPE uri:" + uri + " fc:" + fc);

		// Setting RequestPrimitiveDTO
		RequestPrimitiveDTO req = new RequestPrimitiveDTO();
		req.content = new PrimitiveContentDTO();
		req.operation = Operation.Retrieve;
		req.to = uri;
		req.filterCriteria = fc;
		req.desiredIdentifierResultType = drt;

		// Set the source of the request
		req.from = this.origin;

		LOGGER.debug("Request Uri(BEFORE) is [" + uri + "].");

		// Execute request transmission processing
		Promise<ResponsePrimitiveDTO> res = this.request(req);

		LOGGER.info("END DISCOVERY_RESULT_TYPE");

		return res.map(p -> p.content.listOfURIs);
	}

	@Override
	public Promise<Boolean> notify(String uri, NotificationDTO notification) {
		LOGGER.info("START NOTIFY uri:" + uri + " notification:" + notification);

		// Setting RequestPrimitiveDTO
		RequestPrimitiveDTO req = new RequestPrimitiveDTO();
		req.to = uri;
		req.operation = Operation.Notify;

		// Set the source of the request
		req.from = this.origin;

		// Execute request transmission processing
		Promise<ResponsePrimitiveDTO> res = this.request(req);

		// RETRIEVE processing end
		LOGGER.info("END RETRIEVE");

		return res.map(p -> {
			return (p.responseStatusCode >= 2000 & p.responseStatusCode < 3000);
		});
	}

	@SuppressWarnings("unused")
	private List<String> dataListing(Object res) {
		String strRes = null;
		if (res instanceof byte[]) {
			strRes = new String((byte[]) res);
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

}
