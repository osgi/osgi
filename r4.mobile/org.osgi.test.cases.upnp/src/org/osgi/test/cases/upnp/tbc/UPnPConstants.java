package org.osgi.test.cases.upnp.tbc;

import java.net.*;

/**
 * 
 * 
 */
public final class UPnPConstants {
	//test props
	public static final String	EV_TEST				= "upnp.test.event";
	public static final String	DE_TEST				= "upnp.test.description";
	public static final String	CO_TEST				= "upnp.test.control";
	public static final String	CRLF				= "\r\n";
	public static String		LOCAL_HOST			= "127.0.0.1";
	public static final String	DUPnPMCPort			= "upnp.mc.port";
	public static final String	UPnPMCAddress		= "239.255.255.250";
	public static final String	SEARCH_ADDRESS		= "239.255.255.250:1900";
	public static int			UPnPMCPort;
	public static int			HTTP_PORT			= 8080;
	public static final String	QD					= "::";
	public static final String	DD					= ":";
	public static final String	CC					= ";";
	public static final String	LB					= "<";
	public static final String	LCB					= "</";
	public static final String	RB					= ">";
	public static final String	ULB					= "<u:";
	public static final String	UCLB				= "</u:";
	//http defined
	public static final String	MC_NOTIFY			= "NOTIFY * HTTP/1.1\r\n";
	public static final String	M_SEARCH			= "M-SEARCH * HTTP/1.1\r\n";
	public static final String	HOK					= "HTTP/1.1 200 OK\r\n";
	public static final String	ERROR				= "HTTP/1.1 500 Internal Server Error";
	public static final String	HVER				= "HTTP/1.1";
	public static final String	POST				= "POST ";
	public static final String	M_POST				= "MPOST ";
	public static final String	SUBSCR				= "SUBSCRIBE ";
	//Headers
	public static final String	H_HOST				= "HOST: ";
	public static final String	H_CC				= "CACHE-CONTROL: max-age = ";
	public static final String	H_LOC				= "LOCATION: ";
	public static final String	H_DATE				= "DATE: ";
	public static final String	H_NT				= "NT: ";
	public static final String	H_NTS				= "NTS: ";
	public static final String	H_SERVER			= "SERVER: ";
	public static final String	H_USN				= "USN: ";
	public static final String	H_SID				= "SID: ";
	public static final String	H_CALLBACK			= "CALLBACK: ";
	public static final String	H_TIMEOUT			= "TIMEOUT: Second-";
	public static final String	H_SOAPACTION		= "SOAPACTION: ";
	public static final String	H_MAN				= "MAN: ";
	public static final String	H_CL				= "CONTENT-LENGTH: ";
	public static final String	H_CT				= "CONTENT-TYPE: ";
	public static final String	H_SEQ				= "SEQ: ";
	public static final String	H_MX				= "MX: ";
	public static final String	H_ST				= "ST: ";
	//header names
	public static final String	N_SID				= "SID";
	public static final String	N_NT				= "NT";
	public static final String	N_CALLBACK			= "CALLBACK";
	public static final String	N_TIMEOUT			= "TIMEOUT";
	public static final String	N_SERVER			= "SERVER";
	public static final String	N_DATE				= "DATE";
	public static final String	N_SOAPACTION		= "SOAPACTION";
	public static final String	N_MAN				= "MAN";
	public static final String	N_CL				= "CONTENT-LENGTH";
	public static final String	N_CT				= "CONTENT-TYPE";
	public static final String	N_EXT				= "EXT";
	//default header values
	public static String		V_MC_HOST			= "239.255.255.250:";
	public static final String	V_CC				= "1800";
	public static final String	V_MA				= "max-age = ";
	public static final String	V_NTS_A				= "ssdp:alive";
	public static final String	V_NTS_B				= "ssdp:byebye";
	public static final String	V_NTS_D				= "\"ssdp:discover\"";
	public static final String	V_ST_ALL			= "ssdp:all";
	public static final String	V_SERVER			= "Win/5.0 UPnP/1.0 TC/1.0";
	public static final String	V_ROOT				= "upnp:rootdevice";
	public static final String	V_SEC				= "Second-";
	public static final String	V_NT				= "upnp:event";
	public static final String	V_NTS_E				= "upnp:propchange";
	public static final String	V_MAN				= "\"http://schemas.xmlsoap.org/soap/envelope/\"";
	public static final String	V_NS				= "ns=";
	public static final String	V_CT				= "text/xml";
	public static final String	V_CT1				= "charset=\\utf-8\\";
	public static final String	V_FCT				= "text/xml; charset=\"utf-8\"";
	//Methods
	public static final String	SUBSCRIBE			= "SUBSCRIBE";
	public static final String	UNSUBSCRIBE			= "UNSUBSCRIBE";
	public static final String	MPOST				= "M-POST";
	public static final String	NOTIFY				= "NOTIFY";
	//ids
	public static String		UDN_RCD				= "uuid:prosyst-com:rcd:";														//r
																																		 // continues
																																		 // device
	public static String		UDN_RBD				= "uuid:prosyst-com:rbd:";														//r
																																		 // bye
																																		 // device
	public static String		UDN_RNBD			= "uuid:prosyst-com:rnbd:";														//r no
																																	   // bye
																																	   // device
	public static String		UDN_WLD				= "uuid:prosyst-com:wld:";														//w
																																		 // location
																																		 // device
	public static String		UDN_WND				= "uuid:prosyst-com:wnd:";														//w
																																		 // nts
																																		 // device
	public static String		UDN_WCCLD			= "uuid:prosyst-com:wccld:";													//w
																																	 // cache-control
																																	 // letter
																																	 // device
	public static String		UDN_WCCND			= "uuid:prosyst-com:wccnd:";													//w
																																	 // cache-control
																																	 // negative
																																	 // device
	public static String		UDN_WADS			= "uuid:prosyst-com:wads:";														//w
																																	   // advertisement
																																	   // devices
	public static String		UDN_ROOT			= "uuid:prosyst-com:root:";
	public static String		UDN_EMB1			= "uuid:prosyst-com:emb1:";
	public static String		UDN_EMB2			= "uuid:prosyst-com:emb2:";
	public static final String	DEVICE_TYPE			= "urn:prosyst-com:device:Test:1";
	public static final String	SCONT_TYPE			= "urn:prosyst-com:service:control:1";
	public static final String	SCONT_ID			= "urn:prosyst-com:serviceId:ControlService";
	public static final String	SEV_TYPE			= "urn:prosyst-com:service:event:1";
	public static final String	SEV_ID				= "urn:prosyst-com:serviceId:EventService";
	public static final String	SSEV_TYPE			= "urn:prosyst-com:service:sevent:1";
	public static final String	SSEV_ID				= "urn:prosyst-com:serviceId:ESubService";
	public static final String	SREV_TYPE			= "urn:prosyst-com:service:revent:1";
	public static final String	SREV_ID				= "urn:prosyst-com:serviceId:EReSubService";
	public static final String	SUEV_TYPE			= "urn:prosyst-com:service:uevent:1";
	public static final String	SUEV_ID				= "urn:prosyst-com:serviceId:EUnSubService";
	public static final String	SSIM_TYPE			= "urn:prosyst-com:service:test:1";
	public static final String	SSIM_ID				= "urn:prosyst-com:serviceId:testService";
	//servlet registration uri's
	public static final String	SR_DESC				= "/description";
	public static final String	SR_IM				= "/image";
	public static final String	SR_CON				= "/control";
	public static final String	SR_EV				= "/event";
	public static final String	SR_PRES				= "/presentation";
	public static final String	SR_DISC				= "/disc";
	public static final String	SR_DISCS			= "/discs";
	public static final String	SR_DISCC			= "/discc";
	public static final String	SR_DISCE			= "/disce";
	public static final String	SR_DISCP			= "/discp";
	public static final String	DSI					= "dsi";
	//error responses
	public static final String	ERR_ISID			= "Invalid SID";
	public static final String	ERR_MSID			= "Missing SID";
	public static final String	ERR_INT				= "Invalid NT";
	public static final String	ERR_MICB			= "Missing or invalid CALLBACK";
	public static final String	ERR_INCHEAD			= "Incompatible headers";
	public static final String	ERR_NM				= "No Man";
	public static final String	ERR_WB				= "Wrong Body";
	public static final String	ERR_MNA				= "Method Not Allowed";
	public static final String	ERR_ISE				= "Internal Server Error";
	public static final String	ERR_UAS				= "Unable to accept subscription";
	public static final String	ERR_UAR				= "Unable to accept renewal";
	public static final String	ERR_UAU				= "Unable to accept unsubscribtion";
	//parser constants
	public static final String	ENV_ST				= "<s:Envelope";
	public static final String	ENV_XMLNS			= "xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"";
	public static final String	ENV_S				= "s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">";
	public static final String	BODY_ST				= "<s:Body>";
	public static final String	BODY_END			= "</s:Body>";
	public static final String	ENV_END				= "</s:Envelope>";
	public static final String	FAULT_ST			= "<s:Fault>";
	public static final String	FAULT_CODE			= "<faultcode>s:Client</faultcode>";
	public static final String	FAULT_STRING		= "<faultstring>UPnPError</faultstring>";
	public static final String	DETAIL_ST			= "<detail>";
	public static final String	FAULT_END			= "</s:Fault>";
	public static final String	DETAIL_END			= "</detail>";
	public static final String	UPNPERROR_ST		= "<UPnPError xmlns=\"urn:schemas-upnp-org:control-1-0\">";
	public static final String	UPNPERROR_END		= "</UPnPError>";
	public static final String	ERR_CODE			= "errorCode";
	public static final String	ERR_DESC			= "errorDescription";
	public static final String	VARNAME				= "u:varName";
	public static final String	RET					= "return";
	public static final String	RESP_ST				= "Response xmlns:u=\"";
	public static final String	RESP_END			= "Response";
	public static final String	PROPSET_ST			= "<e:propertyset xmlns:e=\"urn:schemas-upnp-org:event-1-0\">";
	public static final String	PROPSET_END			= "</e:propertyset>";
	public static final String	PROP_ST				= "<e:property>";
	public static final String	PROP_END			= "</e:property>";
	public static final String	XML_ST				= "<?xml version=\"1.0\"?>";
	public static final String	ROOT_ST				= "<root xmlns=\"urn:schemas-upnp-org:device-1-0\">";
	public static final String	ROOT_END			= "</root>";
	public static final String	SPEC_EL				= "<specVersion>\r\n<major>1</major>\r\n<minor>0</minor>\r\n</specVersion>";
	public static final String	URLB_ST				= "<URLBase>";
	public static final String	URLB_END			= "</URLBase>";
	public static final String	UDN_ST				= "<UDN>";
	public static final String	UDN_END				= "</UDN>";
	public static final String	URL_ENC				= "http://";
	public static final String	DEV_ST				= "<device>";
	public static final String	DEV_END				= "</device>";
	public static final String	DEVT_ST				= "<deviceType>";
	public static final String	DEVT_END			= "</deviceType>";
	public static final String	FN_ST				= "<friendlyName>";
	public static final String	FN_END				= "</friendlyName>";
	public static final String	MAN_ST				= "<manufacturer>";
	public static final String	MAN_END				= "</manufacturer>";
	public static final String	MAN_URL_ST			= "<manufacturerURL>";
	public static final String	MAN_URL_END			= "</manufacturerURL>";
	public static final String	MOD_DESC_ST			= "<modelDescription>";
	public static final String	MOD_DESC_END		= "</modelDescription>";
	public static final String	MOD_NAM_ST			= "<modelName>";
	public static final String	MOD_NAM_END			= "</modelName>";
	public static final String	MOD_NUMB_ST			= "<modelNumber>";
	public static final String	MOD_NUMB_END		= "</modelNumber>";
	public static final String	MOD_URL_ST			= "<modelURL>";
	public static final String	MOD_URL_END			= "</modelURL>";
	public static final String	SN_ST				= "<serialNumber>";
	public static final String	SN_END				= "</serialNumber>";
	public static final String	UPC_ST				= "<UPC>";
	public static final String	UPC_END				= "</UPC>";
	public static final String	SL_ST				= "<serviceList>";
	public static final String	SL_END				= "</serviceList>";
	public static final String	SER_ST				= "<service>";
	public static final String	SER_END				= "</service>";
	public static final String	SER_TYPE_ST			= "<serviceType>";
	public static final String	SER_TYPE_END		= "</serviceType>";
	public static final String	SER_ID_ST			= "<serviceId>";
	public static final String	SER_ID_END			= "</serviceId>";
	public static final String	SCPD_URL_ST			= "<SCPDURL>";
	public static final String	SCPD_URL_END		= "</SCPDURL>";
	public static final String	CONT_URL_ST			= "<controlURL>";
	public static final String	CONT_URL_END		= "</controlURL>";
	public static final String	EVSUB_URL_ST		= "<eventSubURL>";
	public static final String	EVSUB_URL_END		= "</eventSubURL>";
	public static final String	PRES_URL_ST			= "<presentationURL>";
	public static final String	PRES_URL_END		= "</presentationURL>";
	public static final String	ARG_NAME			= "<argumentName>";
	public static final String	ARG_NAME_END		= "</argumentName>";
	//action names
	public static final String	ACT_POS				= "PostOutSucc";
	public static final String	ACT_PIS				= "PostInSucc";
	public static final String	ACT_MPOS			= "MPostOutSucc";
	public static final String	ACT_MPIS			= "MPostInSucc";
	public static final String	ACT_PF				= "PostFail";
	public static final String	ACT_MPF				= "MPostFail";
	public static final String	ACT_PB				= "PostBlock";
	public static final String	ACT_MPB				= "MPostBlock";
	public static final String	ACT_QSV				= "QueryStateVariable";
	// in\out actions constants
  public static final String V_OUT_INT = "42";
  public static final String V_OUT_UI4 = "265";
  public static final String V_OUT_NUMBER = "42.0";
  public static final String V_OUT_FLOAT = "42.0";
  public static final String V_OUT_CHAR = "\42";
  public static final String V_OUT_STRING = "The answer is 42.";
  public static final String V_OUT_STR = "Whats the question?";
  public static final String V_OUT_BOOLEAN = "true";
  public static final String V_OUT_HEX = "Out bin.hex value";
  public static final String V_OUT_OUT_OK = "OK";
  public static final String V_OUT_OUT_FAILED = "FAILED";
  public static final String V_IN_INT = "21";
  public static final String V_IN_UI4 = "423";
  public static final String V_IN_NUMBER = "732.53";
  public static final String V_IN_FLOAT = "123.45";
  public static final String V_IN_CHAR = "v";
  public static final String V_IN_STRING = "pras pras";
  public static final String V_IN_BOOLEAN = "false";
  public static final String V_IN_HEX = "In bin.hex value";
  public static final String V_IN_OUT = "OK?";
  // in\out vars names
  public static final String	N_OUT_INT			= "outINT";
	public static final String	N_OUT_UI4			= "outUI4";
	public static final String	N_OUT_NUMBER		= "outNUMBER";
	public static final String	N_OUT_FLOAT			= "outFLOAT";
	public static final String	N_OUT_CHAR			= "outCHAR";
	public static final String	N_OUT_STRING		= "outSTRING";
	public static final String	N_OUT_BOOLEAN		= "outBOOLEAN";
	public static final String	N_OUT_HEX			= "outBIN_HEX";
	public static final String	N_OUT_DATE			= "outDATE";
	public static final String	N_OUT_DATETIME		= "outDATE_TIME";
	public static final String	N_OUT_DATETIMETz	= "outDATE_TIME_TZ";
	public static final String	N_OUT_TIME			= "out_TIME";
	public static final String	N_OUT_OUT			= "outOUT";
	public static final String	N_IN_INT			= "inINT";
	public static final String	N_IN_UI4			= "inUI4";
	public static final String	N_IN_NUMBER			= "inNUMBER";
	public static final String	N_IN_FLOAT			= "inFLOAT";
	public static final String	N_IN_CHAR			= "inCHAR";
	public static final String	N_IN_STRING			= "inSTRING";
	public static final String	N_IN_BOOLEAN		= "inBOOLEAN";
	public static final String	N_IN_HEX			= "inBIN_HEX";
	public static final String	N_IN_DATE			= "inDATE";
	public static final String	N_IN_DATETIME		= "inDATE_TIME";
	public static final String	N_IN_DATETIMETz		= "inDATE_TIME_TZ";
	public static final String	N_IN_TIME			= "inTIME";
	public static final String	N_IN_OUT			= "inOUT";
	//event names
	public static final String	N_INT				= "INT";
	public static final String	N_UI4				= "UI4";
	public static final String	N_NUMBER			= "NUMBER";
	public static final String	N_FLOAT				= "FLOAT";
	public static final String	N_CHAR				= "CHAR";
	public static final String	N_STRING			= "STRING";
	public static final String	N_BOOLEAN			= "BOOLEAN";
	public static final String	N_HEX				= "BIN_HEX";
	public static final String	N_DATEE				= "DATE";
	public static final String	N_DATETIME			= "DATE_TIME";
	public static final String	N_DATETIMETz		= "DATE_TIME_TZ";
	public static final String	N_TIME				= "outTIME";
	public static final String	N_OUT				= "OUT";
	public static final String	N_OUT_STR			= "STR";
	//event values
	public static final String	V_INT				= "99";
	public static final String	V_UI4				= "444";
	public static final String	V_NUMBER			= "333.33";
	public static final String	V_FLOAT				= "77.72";
	public static final String	V_CHAR				= "a";
	public static final String	V_STRING			= "Wowin";
	public static final String	V_BOOLEAN			= "true";
	public static final String	V_HEX				= "Event bin.hex value";
	public static final String	V_OUT				= "End";
	//description values
	public static final String	V_UPC				= "9876543210";
	public static final String	V_FN				= "Test Device";
	public static final String	V_MANU				= "ProSyst";
	public static final String	V_MANU_URL			= "http://www.prosyst.com";
	public static final String	V_MOD_DESC			= "UPnP Test Device For OSGI";
	public static final String	V_MOD_NAME			= "Test";
	public static final String	V_MOD_NUMB			= "123rty";
	public static final String	V_MOD_URL			= "http://www.prosyst.com";
	public static final String	V_SN				= "qwe456";
	public static final String	V_MT				= "image/gif";
	//exported values
	public static final String	E_UPC				= "123456789";
	public static final String	E_FN				= "UPnP Tester";
	public static final String	E_MANU				= "ProSyst Software AG";
	public static final String	E_MANU_URL			= "http://www.prosyst.com";
	public static final String	E_MOD_NAME			= "upnptester";
	public static final String	E_MOD_NUMB			= "1";
	public static final String	E_MOD_URL			= "http://www.upnptest.com/test.html";
	public static final String	E_SN				= "1230456";
	//export services
	public static final String	E_SERVICE			= "urn:prosyst-com:serviceId:TesterDevice";
	public static final String	E_TESTING_SV		= "Testing";
	public static final String	E_TESTMSG_SV		= "TestMessage";
	public static final String	E_OUT_MSG			= "Printing test message";
	public static final String	E_OUT_MSG1			= "true";
	// events values
	public static final String	E_STRING			= "bla";
	public static final String	E_BOOLEAN			= "false";
	public static final String	E_NUMBER			= "36.7";
	public static final String	E_INT				= "10";
	public static final String	E_CHAR				= "y";
	public static final String	E_FLOAT				= "45.6";

	public static void init() {
		UPnPMCPort = Integer.getInteger(DUPnPMCPort, 1900).intValue();
		V_MC_HOST += UPnPMCPort;
		V_MC_HOST = V_MC_HOST.intern();
		try {
			LOCAL_HOST = InetAddress.getLocalHost().getHostAddress();
			LOCAL_HOST = LOCAL_HOST.intern();
			UDN_ROOT += LOCAL_HOST;
			UDN_ROOT = UDN_ROOT.intern();
			UDN_EMB1 += LOCAL_HOST;
			UDN_EMB1 = UDN_EMB1.intern();
			UDN_EMB2 += LOCAL_HOST;
			UDN_EMB2 = UDN_EMB2.intern();
			UDN_RCD += LOCAL_HOST;
			UDN_RCD = UDN_RCD.intern();
			UDN_RBD += LOCAL_HOST;
			UDN_RBD = UDN_RBD.intern();
			UDN_RNBD += LOCAL_HOST;
			UDN_RNBD = UDN_RNBD.intern();
			UDN_WLD += LOCAL_HOST;
			UDN_WLD = UDN_WLD.intern();
			UDN_WND += LOCAL_HOST;
			UDN_WND = UDN_WND.intern();
			UDN_WCCLD += LOCAL_HOST;
			UDN_WCCLD = UDN_WCCLD.intern();
			UDN_WCCND += LOCAL_HOST;
			UDN_WCCND = UDN_WCCND.intern();
			UDN_WADS += LOCAL_HOST;
			UDN_WADS = UDN_WADS.intern();
		}
		catch (UnknownHostException exc) {
			UDN_ROOT += LOCAL_HOST;
			UDN_ROOT = UDN_ROOT.intern();
			UDN_EMB1 += LOCAL_HOST;
			UDN_EMB1 = UDN_EMB1.intern();
			UDN_EMB2 += LOCAL_HOST;
			UDN_EMB2 = UDN_EMB2.intern();
			UDN_RCD += LOCAL_HOST;
			UDN_RCD = UDN_RCD.intern();
			UDN_RBD += LOCAL_HOST;
			UDN_RBD = UDN_RBD.intern();
			UDN_RNBD += LOCAL_HOST;
			UDN_RNBD = UDN_RNBD.intern();
			UDN_WLD += LOCAL_HOST;
			UDN_WLD = UDN_WLD.intern();
			UDN_WND += LOCAL_HOST;
			UDN_WND = UDN_WND.intern();
			UDN_WCCLD += LOCAL_HOST;
			UDN_WCCLD = UDN_WCCLD.intern();
			UDN_WCCND += LOCAL_HOST;
			UDN_WCCND = UDN_WCCND.intern();
			UDN_WADS += LOCAL_HOST;
			UDN_WADS = UDN_WADS.intern();
		}
	}
}