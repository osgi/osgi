package org.osgi.impl.service.upnp.cd.control;

import java.io.DataOutputStream;
import java.util.Dictionary;
import org.osgi.impl.service.upnp.cd.ssdp.UPnPExporter;
import org.osgi.service.upnp.*;

// This class contains the implementation of Control interface defined in the api package.
public class ControlImpl {
	// This is a mapping of the control Url and the Action objects
	private SOAPMaker		maker;
	private SOAPErrorCodes	errorCodes;
	private SOAPParser		parser;

	public ControlImpl() {
		maker = new SOAPMaker();
		errorCodes = new SOAPErrorCodes();
		parser = new SOAPParser();
	}

	// This method will be called by the HttpServer to send the control/query
	// request,
	// which is coming from the CP side.
	public synchronized void sendHttpRequest(Dictionary headers, String xml,
			DataOutputStream dos) {
		boolean invalidReq = false;
		String httpError = null;
		boolean man = false;
		String result = null;
		try {
			String controlUrl = (String) headers.get("post");
			if (controlUrl.startsWith("/")) {
				controlUrl = controlUrl.substring(1);
			}
			if (controlUrl == null) {
				controlUrl = (String) headers.get("m-post");
				man = true;
			}
			if (controlUrl == null) {
				writedata(SOAPConstants.http + SOAPConstants.ERROR_412, dos);
				return;
			}
			if (headers.get("host") == null) {
				writedata(SOAPConstants.http + SOAPConstants.ERROR_412, dos);
				return;
			}
			String cType = (String) headers.get("content-type");
			if (cType == null) {
				writedata(SOAPConstants.http + SOAPConstants.ERROR_412, dos);
				return;
			}
			int indexSemic = cType.indexOf(";");
			if (indexSemic == -1) {
				writedata(SOAPConstants.http + SOAPConstants.ERROR_412, dos);
				return;
			}
			if (!(cType.substring(0, indexSemic)).equals("text/xml")) {
				invalidReq = true;
				writedata(SOAPConstants.http + SOAPConstants.ERROR_415, dos);
				return;
			}
			String soapAction;
			if (!man) {
				soapAction = (String) headers.get("soapaction");
			}
			else {
				String manH = (String) headers.get("man");
				int indexSemi = manH.indexOf(";");
				if (indexSemi == -1) {
					invalidReq = true;
					writedata(SOAPConstants.http + SOAPConstants.ERROR_412, dos);
					return;
				}
				if (!manH.substring(0, indexSemi - 1).equals(
						SOAPConstants.httpEnv)) {
					invalidReq = true;
					writedata(SOAPConstants.http + SOAPConstants.ERROR_412, dos);
					return;
				}
				String ns = manH.substring(indexSemi + 5);
				soapAction = (String) headers.get(ns + "-soapaction");
			}
			if (!soapAction.startsWith("\"") || !soapAction.endsWith("\"")) {
				writedata(SOAPConstants.http + SOAPConstants.ERROR_400, dos);
				return;
			}
			UPnPService upnpservice = UPnPExporter.getControlEntry(controlUrl);
			if (upnpservice == null) {
				result = maker.createResponseError("404");
				writedata(result, dos);
				return;
			}
			if (soapAction
					.equals("\"urn:schemas-upnp-org:control-1-0#QueryStateVariable\"")) {
				return;
			}
			else {
				ParsedRequest req = parser.controlReqParse(xml);
				String actionName = req.getActionName();
				Dictionary params = req.getArguments(upnpservice, actionName);
				UPnPAction upnpaction = upnpservice.getAction(actionName);
				if (upnpaction == null) {
					result = maker.createResponseError("404");
					writedata(result, dos);
					return;
				}
				else {
					new InvokeDeviceCallback(upnpaction, params, maker, dos,
							req).start();
				}
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			result = maker.createResponseError("501");
			writedata(result, dos);
			return;
		}
	}

	public class InvokeDeviceCallback extends Thread {
		UPnPAction			action;
		Dictionary			parameters;
		SOAPMaker			soapmaker;
		DataOutputStream	dos;
		ParsedRequest		request;

		public InvokeDeviceCallback(UPnPAction action, Dictionary params,
				SOAPMaker maker, DataOutputStream dos, ParsedRequest request) {
			this.action = action;
			this.parameters = params;
			this.soapmaker = maker;
			this.dos = dos;
			this.request = request;
		}

		public void run() {
			try {
				Dictionary outParams = action.invoke(parameters);
				Dictionary ConvertedParams = request.getParams(action,
						outParams);
				String result = maker.createControlResponseOK(action.getName(),
						request.getServiceType(), ConvertedParams);
				dos.writeBytes(result);
				dos.close();
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public void writedata(String result, DataOutputStream dos) {
		try {
			dos.writeBytes(result);
			dos.flush();
			dos.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
