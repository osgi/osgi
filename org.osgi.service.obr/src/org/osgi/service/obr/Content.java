package org.osgi.service.obr;

import java.net.URL;
import java.util.Map;

import org.osgi.framework.wiring.Resource;

interface Content {
	final String	LICENCE_ATTRIBUTE		= "license";
	final String	CHECKSUM_ATTRIBUTE		= "checksum";
	final String	CHECKSUM_ALGO_ATTRIBUTE	= "checksumAlgo";
	final String	SCM_LOCATION_ATTRIBUTE	= "scm";

	/**
	 * URL where this content can be pulled from.
	 */
	URL getLocation();

	/**
	 * Other values like license, checksum, etc
	 */
	Map<String, Object> getAttributes();

	/**
	 * The resource to which this content is attached
	 */
	Resource getResource();
}
