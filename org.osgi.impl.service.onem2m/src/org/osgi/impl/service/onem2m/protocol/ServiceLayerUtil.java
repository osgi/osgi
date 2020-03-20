package org.osgi.impl.service.onem2m.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceLayerUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLayerUtil.class);

	public static final String PROTOCOL = "protocol";
	public static final String SERIALIZE = "serialize";
	public static final String POA = "poa";
	public static final String ORIGIN = "origin";

	public static final String PROTOCOL_HTTP = "http";
	public static final String PROTOCOL_XML = "xml";
	public static final String PROTOCOL_COAP = "coap";
	public static final String PROTOCOL_SERVICE = "service";
	public static final String SERIALIZE_JSON = "json";
	public static final String SERIALIZE_XML = "xml";
	public static final String SERIALIZE_CBOR = "cbor";
	public static final String SERIALIZE_SERVICE = "service";

	public static final String PROPERTY_KEY = "symbolicName";
	public static final String NOTIFICATION_URI = "notificationURI";

	private static String		PROPERTY_URI		= "conf/aeConfig.properties";

    public static Map<String, String> getProperty(final String key, BundleContext context) {
    	// Get Properties
    	Properties properties = new Properties();
    	String value = null;
    	try {
			URL entry = context.getBundle().getEntry(PROPERTY_URI);
			InputStream istream = entry.openStream();
            properties.load(istream);
            value = properties.getProperty(key);
        } catch (IOException e) {
        	LOGGER.warn("property read error.", e);
        	return null;
        }

        if(value == null || value.length() == 0){
        	LOGGER.warn("no property");
        	return null;
        }

        String[] valueArray = value.split(",");

        if(valueArray.length != 4){
        	LOGGER.warn("property is injustice");
        	return null;
        }

        Map<String, String> property = new HashMap<String, String>();

        property.put(PROTOCOL, valueArray[0]);
        property.put(SERIALIZE, valueArray[1]);
        property.put(POA, valueArray[2]);
        property.put(ORIGIN, valueArray[3]);

        LOGGER.debug("System property value ");
        LOGGER.debug("    " + PROTOCOL + " = " + property.get(PROTOCOL));
        LOGGER.debug("    " + SERIALIZE + " = " + property.get(SERIALIZE));
        LOGGER.debug("    " + POA + " = " + property.get(POA));
        LOGGER.debug("    " + ORIGIN + " = " + property.get(ORIGIN));

        return property;
    }
}
