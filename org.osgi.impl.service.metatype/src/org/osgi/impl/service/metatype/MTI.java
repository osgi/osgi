/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.impl.service.metatype;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.kxml2.io.KXmlParser;
import org.osgi.framework.*;
import org.osgi.service.metatype.*;
import org.xmlpull.v1.*;

/**
 * The MTI implements the MetaTypeInformation interface. It maintains a registry
 * of OCDs and PIDs.
 * 
 * @version $Revision$
 */
public class MTI implements MetaTypeInformation, Serializable {
	final static String[]	EMPTY_STRINGS			= {};

	/*
	 * The given name space.
	 */
	final static String		NAMESPACE				= "http://www.org.osgi/xmlns/metatype/v1.0.0/md";

	/**
	 * This list is ordered according to the type numbers in AttributeDefinition
	 * ... if you change the order, strange effects are to be expected. The list
	 * is used to translate a type name in the <AD>element to the
	 * AttributeDefinition type int.
	 */
	final static String[]	TYPE_NAMES				= {"<>", "String", "Long",
			"Integer", "Short", "Character", "Byte", "Double", "Float",
			"BigInteger", "BigDecimal", "Boolean",	};

	/**
	 * This list is ordered according to the type numbers in AttributeDefinition
	 * ... if you change the order, strange effects are to be expected. The list
	 * is used to create arrays for cardinality more than 0.
	 */
	final static Object[][]	EMPTY_ARRAYS			= {null, new String[0],
			new Long[0], new Integer[0], new Short[0], new Character[0],
			new Byte[0], new Double[0], new Float[0], null, null,
			new Boolean[0],							};

	Map						ocds					= new Hashtable();
	Map						pids					= new Hashtable();
	Map						factories				= new Hashtable();
	String					localizationBaseName	= null;

	Bundle					bundle;
	private String[]		locales;

	/**
	 * This method should be local, however, it is public so I can test it with
	 * JUnit.
	 * 
	 * @param bundle
	 * @param localizationBaseName
	 */
	public MTI(Bundle bundle, String localizationBaseName) {
		this.bundle = bundle;
		if (localizationBaseName == null)
			localizationBaseName = (String) bundle.getHeaders().get(
					Constants.BUNDLE_LOCALIZATION);
		if (localizationBaseName == null)
			localizationBaseName = "META-INF/bundle";

		this.localizationBaseName = localizationBaseName;
	}

	/**
	 * Parse the meta data from a bundle.
	 * 
	 * <pre>
	 *   MetaData ::= OCD * Designate *
	 * </pre>
	 * @param url Points to the resource containing the meta data
	 * @throws Exception If anything fails
	 */
	public void parseMetaData(URL url) throws Exception {
		XmlPullParser parser = new KXmlParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
		parser.setInput(new InputStreamReader(url.openStream()));

		parser.nextTag(); // Skip document start
		parser.require(XmlPullParser.START_TAG, NAMESPACE, "MetaData");
		while (parser.nextTag() == XmlPullParser.START_TAG
				&& parser.getName().equals("OCD"))
			parseOCD(parser);

		while (parser.getEventType() == XmlPullParser.START_TAG
				&& parser.getName().equals("Designate")) {
			parseDesignate(parser);
			parser.nextTag();
		}

		parser.require(XmlPullParser.END_TAG, NAMESPACE, "MetaData");
	}

	/**
	 * 
	 * <pre>
	 *   Designate ::= Object ?
	 * </pre>
	 * 
	 * @param parser
	 * @throws Exception
	 */
	void parseDesignate(XmlPullParser parser) throws Exception {
		parser.require(XmlPullParser.START_TAG, null, "Designate");
		String pid = parser.getAttributeValue(null, "pid");
		boolean factory = "true".equals(parser.getAttributeValue(null,
				"factory"));
		if (parser.nextTag() == XmlPullParser.START_TAG
				&& parser.getName().equals("Object")) {
			O value = parseObject(parser);
			if (factory)
				factories.put(pid, value);
			else
				pids.put(pid, value);
		}
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, null, "Designate");
	}

	/**
	 * 
	 * <pre>
	 *   OCD ::= AD * Icon *
	 * </pre>
	 * @param parser
	 * @throws Exception
	 */
	private void parseOCD(XmlPullParser parser) throws Exception {
		parser.require(XmlPullParser.START_TAG, null, "OCD");

		OCD ocd = new OCD(this);
		ocd.description = parser.getAttributeValue(null, "description");
		ocd.id = parser.getAttributeValue(null, "id");
		ocd.name = parser.getAttributeValue(null, "name");

		while (parser.nextTag() == XmlPullParser.START_TAG
				&& parser.getName().equals("AD")) {
			AD ad = parseAD(parser);
			ocd.addAD(ad);
		}
		while (parser.getEventType() == XmlPullParser.START_TAG
				&& parser.getName().equals("Icon")) {
			parseIcon(ocd, parser);
			parser.nextTag();
		}

		ocds.put(ocd.id, ocd);
		parser.require(XmlPullParser.END_TAG, null, "OCD");
	}

	/*
	 * <pre>
	 *   Object ::= Attribute *
	 * </pre>
	 * 
	 */
	private O parseObject(XmlPullParser parser) throws Exception {
		parser.require(XmlPullParser.START_TAG, null, "Object");
		O o = new O();
		String type = parser.getAttributeValue(null, "type");
		o.ocd = (OCD) ocds.get(type);
		if (o.ocd == null)
			throw new RuntimeException("Invalid type reference: " + type);

		while (parser.nextTag() == XmlPullParser.START_TAG
				&& parser.getName().equals("Attribute")) {
			String adId = parser.getAttributeValue(null, "name");
			AD ad = o.ocd.getAD(adId);
			if (ad == null)
				throw new RuntimeException("Invalid attribute reference: "
						+ adId);
			Object[] assoc = parseAttribute(parser, ad);
			o.properties.put(assoc[0], assoc[1]);
		}
		parser.require(XmlPullParser.END_TAG, null, "Object");
		return o;
	}

	/**
	 * 
	 * <pre>
	 *   Attribute ::= 
	 * </pre>
	 * @param parser
	 * @param ad
	 * @return
	 * @throws Exception
	 */
	private Object[] parseAttribute(XmlPullParser parser, AD ad)
			throws Exception {
		parser.require(XmlPullParser.START_TAG, null, "Attribute");
		String key = parser.getAttributeValue(null, "name");
		String content = parser.getAttributeValue(null, "content");
		Object value = parseContent(ad, content);
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, null, "Attribute");
		return new Object[] {key, value};
	}

	/**
	 * A content string is a single string with space separated fields.
	 * 
	 * @param ad	The AD for the content
	 * @param content A string with space separated fields
	 * @return A scalar, Vector or [] depending on ad
	 */
	private Object parseContent(AD ad, String content) {
		Object value = null;
		if (ad.cardinality == 0)
			value = parseSimple(ad.type, content);
		else {
			StringTokenizer st = new StringTokenizer(content, "\r\n \t");
			Vector v = new Vector();
			while (st.hasMoreTokens()) {
				v.add(parseSimple(ad.type, st.nextToken()));
			}
			if (ad.cardinality < 0)
				value = v;
			else
				value = v.toArray(EMPTY_ARRAYS[ad.type]);
		}
		return value;
	}

	/**
	 * Parse the value of the field depending on its
	 * scalar type.
	 * 
	 * @param type	Scalar type from AttributeDefinition
	 * @param content String containing scalar
	 * @return object
	 */
	private Object parseSimple(int type, String content) {
		content = content.trim();
		switch (type) {
			case AttributeDefinition.BOOLEAN :
				return Boolean.valueOf(content);

			case AttributeDefinition.STRING :
				return content;

			case AttributeDefinition.BYTE :
				return new Byte(content);

			case AttributeDefinition.SHORT :
				return new Short(content);

			case AttributeDefinition.CHARACTER :
				return new Character((char) Integer.parseInt(content, 10));

			case AttributeDefinition.INTEGER :
				return new Integer(content);

			case AttributeDefinition.LONG :
				return new Long(content);

			case AttributeDefinition.FLOAT :
				return Float.valueOf(content);
			case AttributeDefinition.DOUBLE :
				return Double.valueOf(content);
		}
		throw new RuntimeException("Invalid data type: " + type);
	}

	/**
	 * Parse the icon from the input
	 * 
	 * @param ocd
	 * @param parser
	 * @throws Exception
	 */
	private void parseIcon(OCD ocd, XmlPullParser parser) throws Exception {
		parser.require(XmlPullParser.START_TAG, null, "Icon");
		int size = Integer.parseInt(parser.getAttributeValue(null, "size"));
		String url = parser.getAttributeValue(null, "resource");
		ocd.addIcon(size, url);
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, null, "Icon");
	}

	/**
	 * Utiltity to find the index of a string. Simple linear
	 * search.
	 * 
	 * @param typeNames		List of string
	 * @param searched		Value to be searched 
	 * @return
	 */
	private int lookup(String[] typeNames, String searched) {
		for (int i = 0; i < typeNames.length; i++) {
			if (searched.equals(typeNames[i])) {
				return i;
			}
		}
		throw new RuntimeException("Invalid type name: " + searched );
	}

	/**
	 * <pre>
	 *   AD ::= Option*
	 * </pre>
	 * @param ocd		The parent class
	 * @param parser
	 * @throws Exception
	 */
	private AD parseAD(XmlPullParser parser) throws Exception {
		parser.require(XmlPullParser.START_TAG, null, "AD");
		AD ad = new AD();
		ad.description = parser.getAttributeValue(null, "description");
		ad.id = parser.getAttributeValue(null, "id");
		ad.name = parser.getAttributeValue(null, "name");
		ad.type = lookup(TYPE_NAMES, parser.getAttributeValue(null, "type"));
		String cardinality = parser.getAttributeValue(null, "cardinality");
		if (cardinality != null)
			ad.cardinality = Integer.parseInt(cardinality);

		String defaultValue = parser.getAttributeValue(null, "default");
		if (defaultValue != null) {
			StringTokenizer st = new StringTokenizer(defaultValue, " \t\r\n");
			int n = st.countTokens();
			ad.defaultValue = new String[n];
			for (int i = 0; i < n; i++)
				ad.defaultValue[i] = st.nextToken();
		}

		while (parser.nextTag() == XmlPullParser.START_TAG
				&& parser.getName().equals("Option")) {
			String[] assoc = parseOption(parser);
			ad.addOption(assoc[0], assoc[1]);
		}

		parser.require(XmlPullParser.END_TAG, null, "AD");
		return ad;
	}

	/**
	 * Parse an option.
	 * 
	 * @param parser
	 * @param ad
	 * @throws Exception
	 */
	private String[] parseOption(XmlPullParser parser) throws Exception {
		parser.require(XmlPullParser.START_TAG, null, "Option");
		String label = parser.getAttributeValue(null, "label");
		String value = parser.getAttributeValue(null, "value");
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, null, "Option");
		return new String[] { label, value };
	}

	/**
	 * Return the pids for this bundle.
	 * 
	 * @return
	 * @see org.osgi.service.metatype.MetaTypeInformation#getPids()
	 */
	public String[] getPids() {
		return (String[]) pids.keySet().toArray(EMPTY_STRINGS);
	}

	/**
	 * Return the factory pids for this bundle.
	 * 
	 * @return
	 * @see org.osgi.service.metatype.MetaTypeInformation#getFactoryPids()
	 */
	public String[] getFactoryPids() {
		return (String[]) factories.keySet().toArray(EMPTY_STRINGS);
	}

	/**
	 * Return the associated bundle.
	 * 
	 * @return
	 * @see org.osgi.service.metatype.MetaTypeInformation#getBundle()
	 */
	public Bundle getBundle() {
		return (Bundle) bundle;
	}

	/**
	 * Get the associated OCD. This is an OCDProxy that is customized
	 * for the given locale.
	 * 
	 * @param id
	 * @param locale
	 * @return
	 * @see org.osgi.service.metatype.MetaTypeProvider#getObjectClassDefinition(java.lang.String, java.lang.String)
	 */
	public ObjectClassDefinition getObjectClassDefinition(String id,
			String locale) {

		if (locale == null)
			locale = Locale.getDefault().toString();

		OCD ocd = (OCD) ocds.get(id);

		if (locale.equals(""))
			return new OCDProxy(ocd, null);

		locale = "_" + locale;
		URL url = null;

		while (true) {
			String resource = localizationBaseName + locale + ".properties";
			url = bundle.getResource(resource);
			if (url != null)
				break;

			int n = locale.lastIndexOf('_');
			if (n < 0)
				break;

			locale = locale.substring(0, n);
		}
		return new OCDProxy(ocd, url);
	}

	/**
	 * Analyze the bundles for the locales. Note that depending on the
	 * resolve state, the answer may vary.
	 * 
	 * @return
	 * @see org.osgi.service.metatype.MetaTypeProvider#getLocales()
	 */
	public String[] getLocales() {

		if ((bundle.getState() & (Bundle.RESOLVED | Bundle.STARTING | Bundle.ACTIVE)) != 0) {

			Vector v = new Vector();

			for (Enumeration e = bundle.getResources(localizationBaseName); e
					.hasMoreElements();) {
				URL locale = (URL) e.nextElement();
				v.add(parseLocaleResource(localizationBaseName, locale
						.getFile()));
			}
			return locales = (String[]) v.toArray(EMPTY_STRINGS);
		}
		else
			return null;
	}

	String parseLocaleResource(String baseName, String resource) {
		String suffix = resource.substring(baseName.length(), resource.length()
				- baseName.length() - ".properties".length());
		System.out.println("Suffix " + suffix);
		return suffix;
	}
}
