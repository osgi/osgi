package org.osgi.impl.service.upnp.cd.ssdp;

public interface SSDPConstants {
	static final String	NOTIFY			= "NOTIFY * HTTP/1.1";
	static final String	MSEARCH			= "M-SEARCH * HTTP/1.1";
	static final String	MSEARCH_RESP	= "HTTP/1.1 200 OK";
	static final int	HOST_PORT		= 1900;
	static final String	HOST_IP			= "239.255.255.250";
	static final String	HOST			= "HOST: " + HOST_IP + ":" + HOST_PORT;
	static final String	CACHE			= "CACHE-CONTROL:";
	static final String	LOC				= "LOCATION:";
	static final String	NT				= "NT:";
	static final String	NTS				= "NTS:";
	static final String	SERVER			= "SERVER:";
	static final String	USN				= "USN:";
	static final String	ALIVE			= "ssdp:alive";
	static final String	BYEBYE			= "ssdp:byebye";
	static final String	DISCOVER		= "\"ssdp:discover\"";
	static final String	ROOTDEVICE		= "upnp:rootdevice";
	static final String	EXT				= "EXT:";
	static final String	ST				= "ST:";
	static final String	DATE1			= "DATE:";
	static final String	MAN				= "MAN:";
	static final String	MX				= "MX:";
}
