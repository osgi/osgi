package org.osgi.impl.service.upnp.cd.control;

// This interface contains the constant strings, used in the control layer.
interface SOAPConstants {
	public static final String	post			= "POST ";
	public static final String	host			= "HOST: ";
	public static final String	http			= "HTTP/1.1";
	public static final String	contentLength	= "CONTENT-LENGTH: ";
	public static final String	contentType		= "CONTENT-TYPE: text/xml; charset=\"utf-8\"";
	public static final String	soapAction		= "SOAPACTION: ";
	public static final String	startEnvelop	= "<s:Envelop xmlns:s=\"http://schemas.xmlsoap.org/soap/envelop/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">";
	public static final String	startBody		= "<s:Body>";
	public static final String	mpost			= "M-POST ";
	public static final String	stopEnvelop		= "</s:Envelop>";
	public static final String	stopBody		= "</s:Body>";
	public static final String	saUrn			= "\"urn:schemas-upnp-org:service:";
	public static final String	date			= "DATE: ";
	public static final String	ext				= "EXT: ";
	public static final String	server			= "SERVER :";
	public static final String	upnpVersion		= " UPnP/1.0 ";
	public static final String	response		= "Response";
	public static final String	startFault		= "<s:Fault>";
	public static final String	stopFault		= "</s:Fault>";
	public static final String	startDetail		= "<detail>";
	public static final String	stopDetail		= "</detail>";
	public static final String	faultCode		= "<faultcode>s:Client</faultcode>";
	public static final String	faultString		= "<faultstring>UPnPError</faultstring>";
	public static final String	startErrCode	= "<errorCode>";
	public static final String	stopErrCode		= "</errorCode>";
	public static final String	startErrDesc	= "<errorDescription>";
	public static final String	stopErrDesc		= "</errorDescription>";
	public static final String	upnpErr			= "UPnPError";
	public static final String	upnpXmlns		= "xmlns=\"urn:schemas-upnp-org:control-1-0\"";
	public static final String	queryStateVar	= "u:QueryStateVariable";
	public static final String	varName			= "u:varName";
	public static final String	result			= "return";
	public static final String	rn				= "\r\n";
	public static final String	httpEnv			= "\"http://schemas.xmlsoap.org/soap/envelop/\"";
	public static final String	osNameVersion	= System.getProperty("os.name")
														+ "/"
														+ System
																.getProperty("os.version");
	public static final String	productVersion	= "Samsung-UPnP/1.0";
	public static final String	ERROR_412		= " 412 Precondition Failed";
	public static final String	ERROR_415		= " 415 Unsupported Media Type";
	public static final String	ERROR_400		= " 400 Bad Request";
	public static final String	ERROR_500		= " 500 Internal Server Error";
	public static final String	OK_200			= " 200 OK";
}
