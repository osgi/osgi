package org.osgi.impl.service.upnp.cd.event;

public class GenaConstants {
	public static final String	GENA_SUBSCRIBE		= "SUBSCRIBE";
	public static final String	GENA_UNSUBSCRIBE	= "UNSUBSCRIBE";
	public static final String	GENA_NOTIFY			= "NOTIFY";
	public static final String	GENA_SERVER_VERSION	= "HTTP/1.1";
	public final static String	GENA_HOST			= "HOST";
	public final static String	GENA_CALLBACK		= "CALLBACK";
	public final static String	GENA_NT				= "NT";
	public final static String	GENA_NTS			= "NTS";
	public final static String	GENA_SEQ			= "SEQ";
	public final static String	GENA_SID			= "SID";
	public static String		GENA_CALLBACK_URL;
	public final static String	GENA_BODY			= "GENA-BODY";
	public final static String	GENA_CONTENT_TYPE	= "CONTENT-TYPE";
	public final static String	GENA_CONTENT_LENGTH	= "CONTENT-LENGTH";
	public final static String	GENA_TIMEOUT		= "TIMEOUT";
	public final static String	GENA_OK_200			= " 200 OK";
	public final static String	GENA_ERROR_400		= " 400 Bad Request";
	public final static String	GENA_ERROR_412		= " 412 PreCondition Failed";
	public final static String	GENA_ERROR_503		= " 503 Service Unavailable";

	public GenaConstants(String ip) {
		try {
			GENA_CALLBACK_URL = "http://" + ip;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void setPort(int port) {
		GENA_CALLBACK_URL = GENA_CALLBACK_URL + ":" + port;
	}
}
