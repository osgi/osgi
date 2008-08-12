package org.osgi.impl.service.upnp.cd.control;

// This class parses the SOAP message.
public class SOAPParser {
	public ParsedRequest	theParsedRequest;

	// This method takes the query requestSOAP message and then parse it and
	// returns the
	// QueryStateVariable which has to be queried.
	public String queryReqParse(String xml) {
		if (xml == null) {
			return null;
		}
		int i = xml.indexOf(SOAPConstants.varName);
		int j = xml.lastIndexOf(SOAPConstants.varName);
		if (i != -1 && j != -1 && i != j) {
			String var = xml.substring(i + SOAPConstants.varName.length() + 1,
					j - 2);
			return var;
		}
		return null;
	}

	// This method is called to parse control request SOAP message
	public synchronized ParsedRequest controlReqParse(String xml) {
		String serviceType;
		String actionName;
		ParsedRequest req = new ParsedRequest();
		if (xml == null) {
			return null;
		}
		int i = xml.indexOf(SOAPConstants.startBody);
		int j = xml.indexOf(SOAPConstants.stopBody);
		if (i != -1 && j != -1 && i != j) {
			xml = (xml.substring(i + SOAPConstants.startBody.length(), j))
					.trim();
			actionName = (xml.substring(3, xml.indexOf("xmlns") - 1)).trim();
			int startPos = xml.indexOf(SOAPConstants.saUrn)
					+ SOAPConstants.saUrn.length();
			int endPos = xml.indexOf("\"", xml.indexOf(SOAPConstants.saUrn)
					+ SOAPConstants.saUrn.length());
			serviceType = xml.substring(startPos, endPos);
			xml = (xml.substring(endPos + 2, xml.lastIndexOf("</u:"
					+ actionName))).trim();
			req.setServiceType(serviceType);
			req.setActionName(actionName);
			while (true) {
				int startArgIndex = xml.indexOf('<');
				if (startArgIndex == -1) {
					break;
				}
				String argName = xml.substring(startArgIndex + 1, xml
						.indexOf(">"));
				int endArgIndex = xml.indexOf("</" + argName + ">");
				String argValue = xml.substring(startArgIndex
						+ argName.length() + 2, endArgIndex);
				req.setArgument(argName, argValue);
				xml = xml.substring(endArgIndex + 3 + argName.length());
			}
			return req;
		}
		return null;
	}
}
