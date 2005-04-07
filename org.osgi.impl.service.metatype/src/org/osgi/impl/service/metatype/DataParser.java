/*
 * $Header$
 *
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */
package org.osgi.impl.service.metatype;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.*;

import org.osgi.framework.Bundle;
import org.osgi.service.metatype.AttributeDefinition;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Meta XML Data Parser
 * 
 * @author Julian Chen
 * @version 1.0
 */
public class DataParser {

	public static final String	UNEXPECTED_ELEMENT		= "UNEXPECTED_ELEMENT";		//$NON-NLS-1$
	public static final String	UNEXPECTED_TEXT			= "UNEXPECTED_TEXT";		//$NON-NLS-1$
	public static final String	MISSING_ATTRIBUTE		= "MISSING_ATTRIBUTE";		//$NON-NLS-1$
	public static final String	OCD_ID_NOT_FOUND		= "OCD_ID_NOT_FOUND";		//$NON-NLS-1$
	public static final String	NULL_DEFAULTS			= "NULL_DEFAULTS";			//$NON-NLS-1$
	public static final String	NO_DESIGNATE			= "NO_DESIGNATE";			//$NON-NLS-1$
	public static final String	MISSING_ELEMENT			= "MISSING_ELEMENT";		//$NON-NLS-1$

	private static final String	METADATA				= "MetaData";				//$NON-NLS-1$
	private static final String	LOCALIZATION			= "localization";			//$NON-NLS-1$
	private static final String	OCD						= "OCD";					//$NON-NLS-1$
	private static final String	ICON					= "Icon";					//$NON-NLS-1$
	private static final String	AD						= "AD";						//$NON-NLS-1$
	private static final String	CARDINALITY				= "cardinality";			//$NON-NLS-1$
	private static final String	OPTION					= "Option";					//$NON-NLS-1$
	private static final String	LABEL					= "label";					//$NON-NLS-1$
	private static final String	VALUE					= "value";					//$NON-NLS-1$
	private static final String	MIN						= "min";					//$NON-NLS-1$
	private static final String	MAX						= "max";					//$NON-NLS-1$
	private static final String	TYPE					= "type";					//$NON-NLS-1$
	private static final String	SIZE					= "size";					//$NON-NLS-1$
	private static final String	ID						= "id";						//$NON-NLS-1$
	private static final String	NAME					= "name";					//$NON-NLS-1$
	private static final String	DESCRIPTION				= "description";			//$NON-NLS-1$
	private static final String	RESOURCE				= "resource";				//$NON-NLS-1$
	private static final String	PID						= "pid";					//$NON-NLS-1$
	private static final String	DEFAULT					= "default";				//$NON-NLS-1$
	private static final String	ADREF					= "adref";					//$NON-NLS-1$
	private static final String	CONTENT					= "content";				//$NON-NLS-1$
	private static final String	FACTORY					= "factory";				//$NON-NLS-1$
	private static final String	BUNDLE					= "bundle";					//$NON-NLS-1$
	private static final String	OPTIONAL				= "optional";				//$NON-NLS-1$
	private static final String	OBJECT					= "Object";					//$NON-NLS-1$
	private static final String	OCDREF					= "ocdref";					//$NON-NLS-1$
	private static final String	ATTRIBUTE				= "Attribute";				//$NON-NLS-1$
	private static final String	DESIGNATE				= "Designate";				//$NON-NLS-1$
	private static final String	MERGE					= "merge";					//$NON-NLS-1$
	private static final String	REQUIRED				= "required";				//$NON-NLS-1$

	private static final String	INTEGER					= "Integer";				//$NON-NLS-1$
	private static final String	STRING					= "String";					//$NON-NLS-1$
	private static final String	FLOAT					= "Float";					//$NON-NLS-1$
	private static final String	DOUBLE					= "Double";					//$NON-NLS-1$
	private static final String	BYTE					= "Byte";					//$NON-NLS-1$
	private static final String	LONG					= "Long";					//$NON-NLS-1$
	private static final String	CHAR					= "Char";					//$NON-NLS-1$
	private static final String	BOOLEAN					= "Boolean";				//$NON-NLS-1$
	private static final String	SHORT					= "Short";					//$NON-NLS-1$

	protected Bundle			_dp_bundle;
	protected URL				_dp_url;
	protected SAXParserFactory	_dp_parserFactory;
	protected XMLReader			_dp_xmlReader;

	// DesignateHanders in DataParser class
	Vector						_dp_designateHandlers	= new Vector(7);
	// ObjectClassDefinitions in DataParser class w/ corresponding reference keys
	Hashtable					_dp_OCDs				= new Hashtable(7);
	// ObjectClassDefinitions in DataParser class as a Vector
	Vector						_dp_OCDs_vector			= new Vector(7);
	// Localization in DataParser class
	String						_dp_localization;

	/*
	 * Constructor of class DataParser.
	 */
	public DataParser(Bundle bundle, URL url, SAXParserFactory parserFactory) {

		this._dp_bundle = bundle;
		this._dp_url = url;
		this._dp_parserFactory = parserFactory;
		parserFactory.setValidating(false);
	}

	/*
	 * Main method to parse specific MetaData file.
	 */
	public Vector doParse() {

		try {
			SAXParser saxParser = _dp_parserFactory.newSAXParser();
			_dp_xmlReader = saxParser.getXMLReader();
			_dp_xmlReader.setContentHandler(new RootHandler());
			_dp_xmlReader.setErrorHandler(new MyErrorHandler(System.err));
			InputStream is = _dp_url.openStream();
			InputSource isource = new InputSource(is);
			Logging.log(Logging.DEBUG, "Starting to parse " //$NON-NLS-1$
					+ _dp_url);
			_dp_xmlReader.parse(isource);
		}
		catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			return null;
		}
		catch (SAXException saxe) {
			saxe.printStackTrace();
			return null;
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
		return _dp_OCDs_vector;
	}

	/*
	 * Convert String for expected data type.
	 */
	private static Object convert(String value, int type) {

		if (value == null) {
			return null;
		}

		switch (type) {
			case AttributeDefinition.STRING :
				// Both the min and max of STRING are Integers.
				return new Integer(value);
			case AttributeDefinition.LONG :
				return new Long(value);
			case AttributeDefinition.INTEGER :
				return new Integer(value);
			case AttributeDefinition.SHORT :
				return new Short(value);
			case AttributeDefinition.CHARACTER :
				return new Character(value.charAt(0));
			case AttributeDefinition.BYTE :
				return new Byte(value);
			case AttributeDefinition.DOUBLE :
				return new Double(value);
			case AttributeDefinition.FLOAT :
				return new Float(value);
			case AttributeDefinition.BIGINTEGER :
				try {
					Class bigIntClazz = Class.forName("java.math.BigInteger");//$NON-NLS-1$
					Constructor bigIntConstructor = bigIntClazz
							.getConstructor(new Class[] {String.class});
					return bigIntConstructor.newInstance(new Object[] {value});
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
					return null;
				}
				catch (SecurityException e) {
					e.printStackTrace();
					return null;
				}
				catch (NoSuchMethodException e) {
					e.printStackTrace();
					return null;
				}
				catch (IllegalArgumentException e) {
					e.printStackTrace();
					return null;
				}
				catch (InstantiationException e) {
					e.printStackTrace();
					return null;
				}
				catch (IllegalAccessException e) {
					e.printStackTrace();
					return null;
				}
				catch (InvocationTargetException e) {
					e.printStackTrace();
					return null;
				}
			case AttributeDefinition.BIGDECIMAL :
				try {
					Class bigDecimalClazz = Class
							.forName("java.math.BigDecimal");//$NON-NLS-1$
					Constructor bigDecimalConstructor = bigDecimalClazz
							.getConstructor(new Class[] {String.class});
					return bigDecimalConstructor
							.newInstance(new Object[] {value});
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
					return null;
				}
				catch (SecurityException e) {
					e.printStackTrace();
					return null;
				}
				catch (NoSuchMethodException e) {
					e.printStackTrace();
					return null;
				}
				catch (IllegalArgumentException e) {
					e.printStackTrace();
					return null;
				}
				catch (InstantiationException e) {
					e.printStackTrace();
					return null;
				}
				catch (IllegalAccessException e) {
					e.printStackTrace();
					return null;
				}
				catch (InvocationTargetException e) {
					e.printStackTrace();
					return null;
				}
			case AttributeDefinition.BOOLEAN :
				return new Boolean(value);
			default :
				// Unknown data type
				return null;
		}
	}

	/**
	 * Abstract of all Handlers.
	 */
	private class AbstractHandler extends DefaultHandler {

		protected ContentHandler	_doc_handler;
		protected boolean			_isParsedDataValid	= true;

		public AbstractHandler(ContentHandler parentHandler) {

			this._doc_handler = parentHandler;
			_dp_xmlReader.setContentHandler(this);
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			throw new SAXException(Msg.formatter.getString(UNEXPECTED_ELEMENT,
					qName));
		}

		public void characters(char[] buf, int start, int end)
				throws SAXException {

			String s = new String(buf, start, end).trim();
			if (s.length() > 0) {
				throw new SAXException(Msg.formatter.getString(UNEXPECTED_TEXT,
						s));
			}
		}

		/**
		 * Called when this element and all elements nested into it have been
		 * handled.
		 */
		protected void finished() throws SAXException {
		}

		public void endElement(String namespaceURI, String localName,
				String qName) throws SAXException {

			finished();
			// Let parent resume handling SAX events
			_dp_xmlReader.setContentHandler(_doc_handler);
		}
	}

	/**
	 * Handler for the root element.
	 */
	private class RootHandler extends DefaultHandler {

		public RootHandler() {
			super();
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is AbstractHandler:startElement():" //$NON-NLS-1$
					+ qName);
			String name = getName(localName, qName);
			if (name.equalsIgnoreCase(METADATA)) {
				new MetaDataHandler(this).init(name, attributes);
			}
			else {
				Logging.log(Logging.WARN,
						Msg.formatter.getString(UNEXPECTED_ELEMENT, name));
			}
		}

		public void setDocumentLocator(Locator locator) {
		}
	}

	/**
	 * Handler for the MetaData element.
	 */
	private class MetaDataHandler extends AbstractHandler {

		public MetaDataHandler(ContentHandler handler) {
			super(handler);
		}

		public void init(String name, Attributes attributes)
				throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is MetaDataHandler():init()"); //$NON-NLS-1$
			_dp_localization = attributes.getValue(LOCALIZATION);
			if (_dp_localization == null) {
				// Not a problem, because LOCALIZATION is an optional attribute.
			}
			// The global variable "_dp_localization" will be used within
			// OcdHandler and AttributeDefinitionHandler later.
		}

		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is MetaDataHandler:startElement():" //$NON-NLS-1$
					+ qName);
			String name = getName(localName, qName);
			if (name.equalsIgnoreCase(DESIGNATE)) {
				DesignateHandler designateHandler = new DesignateHandler(
						this);
				designateHandler.init(name, atts);
				if (designateHandler._isParsedDataValid) {
					_dp_designateHandlers.addElement(designateHandler);
				}
			}
			else
				if (name.equalsIgnoreCase(OCD)) {
					OcdHandler ocdHandler = new OcdHandler(this);
					ocdHandler.init(name, atts, _dp_OCDs);
				}
				else {
					Logging.log(Logging.WARN,
							Msg.formatter.getString(UNEXPECTED_ELEMENT, name));
				}
		}

		protected void finished() throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is MetaDataHandler():finished()"); //$NON-NLS-1$
			if (_dp_designateHandlers.size() == 0) {
				// Schema defines at least one DESIGNATE is required.
				_isParsedDataValid = false;
				Logging.log(Logging.ERROR, this, "finished()", //$NON-NLS-1$
						Msg.formatter.getString(MISSING_ELEMENT, DESIGNATE));
				return;
			}
			Enumeration designateHandlerKeys = _dp_designateHandlers.elements();
			while (designateHandlerKeys.hasMoreElements()) {
				DesignateHandler designateHandler = 
						(DesignateHandler) designateHandlerKeys.nextElement();

				ObjectClassDefinitionImpl ocd =
						(ObjectClassDefinitionImpl) _dp_OCDs.get(designateHandler._ocdref);
				if (ocd != null) {
					ocd.setID(designateHandler._pid_val);
					if (designateHandler._factory_val == true) {
						ocd.setType(ObjectClassDefinitionImpl.FPID);
					}
					else {
						ocd.setType(ObjectClassDefinitionImpl.PID);
					}
					_dp_OCDs_vector.addElement(ocd);
				}
				else {
					Logging.log(Logging.ERROR, this, "finished()", //$NON-NLS-1$
							Msg.formatter.getString(OCD_ID_NOT_FOUND,
									designateHandler._ocdref));
				}
			}
		}
	}

	/**
	 * Handler for the ObjectClassDefinition element.
	 */
	private class OcdHandler extends AbstractHandler {

		Hashtable					_parent_OCDs_hashtable;
		// This ID "_refID" is only used for reference by Designate element,
		// not the PID or FPID of this OCD.
		String						_refID;
		ObjectClassDefinitionImpl	_ocd;
		Vector						_ad_vector	= new Vector(7);

		public OcdHandler(ContentHandler handler) {
			super(handler);
		}

		public void init(String name, Attributes atts, Hashtable ocds_hashtable)
				throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is OcdHandler():init()"); //$NON-NLS-1$
			_parent_OCDs_hashtable = ocds_hashtable;

			String ocd_name_val = atts.getValue(NAME);
			if (ocd_name_val == null) {
				_isParsedDataValid = false;
				Logging.log(Logging.ERROR, this,
						"init(String, Attributes, Hashtable)", //$NON-NLS-1$
						Msg.formatter.getString(
								MISSING_ATTRIBUTE, NAME, name));
				return;
			}

			String ocd_description_val = atts.getValue(DESCRIPTION);
			if (ocd_description_val == null) {
				// Not a problem, because DESCRIPTION is an optional attribute.
			}

			_refID = atts.getValue(ID);
			if (_refID == null) {
				_isParsedDataValid = false;
				Logging.log(Logging.ERROR, this,
						"init(String, Attributes, Hashtable)", //$NON-NLS-1$
						Msg.formatter.getString(
								MISSING_ATTRIBUTE, ID, name));
				return;
			}

			_ocd = new ObjectClassDefinitionImpl(ocd_name_val,
					ocd_description_val, _dp_localization);
		}

		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is OcdHandler:startElement():" //$NON-NLS-1$
					+ qName);
			if (!_isParsedDataValid)
				return;

			String name = getName(localName, qName);
			if (name.equalsIgnoreCase(AD)) {
				AttributeDefinitionHandler attributeDefHandler = new AttributeDefinitionHandler(
						this);
				attributeDefHandler.init(name, atts, _ad_vector);
			}
			else
				if (name.equalsIgnoreCase(ICON)) {
					IconHandler iconHandler = new IconHandler(this);
					iconHandler.init(name, atts);
					if (iconHandler._isParsedDataValid) {
						// Because XML schema allows at most one icon for
						// one OCD, if more than one icons are read from 
						// MetaData, then only the final icon will be kept.
						_ocd.setIcon(iconHandler._icon);
					}
				}
				else {
					Logging.log(Logging.WARN,
							Msg.formatter.getString(UNEXPECTED_ELEMENT, name));
				}
		}

		protected void finished() throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is OcdHandler():finished()"); //$NON-NLS-1$
			if (!_isParsedDataValid)
				return;

			if (_ad_vector.size() == 0) {
				// Schema defines at least one AD is required.
				_isParsedDataValid = false;
				Logging.log(Logging.ERROR, this, "finished()", //$NON-NLS-1$
						Msg.formatter.getString(
								MISSING_ELEMENT, AD, _refID));
				return;
			}
			// OCD gets all parsed ADs.
			Enumeration adKey = _ad_vector.elements();
			while (adKey.hasMoreElements()) {
				AttributeDefinitionImpl ad = (AttributeDefinitionImpl) adKey.nextElement();
				_ocd.addAttributeDefinition(ad, ad._isRequired);
			}

			_parent_OCDs_hashtable.put(_refID, _ocd);
		}
	}

	/**
	 * Handler for the Icon element.
	 */
	private class IconHandler extends AbstractHandler {

		Icon	_icon;

		public IconHandler(ContentHandler handler) {
			super(handler);
		}

		public void init(String name, Attributes atts) throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is IconHandler:init()"); //$NON-NLS-1$
			String icon_resource_val = atts.getValue(RESOURCE);
			if (icon_resource_val == null) {
				_isParsedDataValid = false;
				Logging.log(Logging.ERROR, this,
						"init(String, Attributes)", //$NON-NLS-1$
						Msg.formatter.getString(
								MISSING_ATTRIBUTE, RESOURCE, name));
				return;
			}

			String icon_size_val = atts.getValue(SIZE);
			if (icon_size_val == null) {
				// Not a problem, because SIZE is an optional attribute.
				icon_size_val = "0"; //$NON-NLS-1$
			}
			else
				if (icon_size_val.equalsIgnoreCase("")) { //$NON-NLS-1$
					icon_size_val = "0"; //$NON-NLS-1$
				}

			_icon = new Icon(icon_resource_val,
					Integer.parseInt(icon_size_val), _dp_bundle);
		}
	}

	/**
	 * Handler for the Attribute element.
	 */
	private class AttributeDefinitionHandler extends AbstractHandler {

		AttributeDefinitionImpl	_ad;
		int						_dataType;

		Vector _parent_ADs_vector;
		Vector _optionLabel_vector = new Vector(7);
		Vector _optionValue_vector = new Vector(7);
		
		public AttributeDefinitionHandler(ContentHandler handler) {
			super(handler);
		}

		public void init(String name, Attributes atts, Vector ad_vector) throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is AttributeDefinitionHandler():init()"); //$NON-NLS-1$
			_parent_ADs_vector = ad_vector;

			String ad_name_val = atts.getValue(NAME);
			if (ad_name_val == null) {
				// Not a problem, because NAME is an optional attribute.
			}

			String ad_description_val = atts.getValue(DESCRIPTION);
			if (ad_description_val == null) {
				// Not a problem, because DESCRIPTION is an optional attribute.
			}

			String ad_id_val = atts.getValue(ID);
			if (ad_id_val == null) {
				_isParsedDataValid = false;
				Logging.log(Logging.ERROR, this,
						"init(String, Attributes, Vector)", //$NON-NLS-1$
						Msg.formatter.getString(
								MISSING_ATTRIBUTE, ID, name));
				return;
			}

			String ad_type_val = atts.getValue(TYPE);
			if (ad_type_val.equalsIgnoreCase(STRING)) {
				_dataType = AttributeDefinition.STRING;
			}
			else
				if (ad_type_val.equalsIgnoreCase(LONG)) {
					_dataType = AttributeDefinition.LONG;
				}
				else
					if (ad_type_val.equalsIgnoreCase(DOUBLE)) {
						_dataType = AttributeDefinition.DOUBLE;
					}
					else
						if (ad_type_val.equalsIgnoreCase(FLOAT)) {
							_dataType = AttributeDefinition.FLOAT;
						}
						else
							if (ad_type_val.equalsIgnoreCase(INTEGER)) {
								_dataType = AttributeDefinition.INTEGER;
							}
							else
								if (ad_type_val.equalsIgnoreCase(BYTE)) {
									_dataType = AttributeDefinition.BYTE;
								}
								else
									if (ad_type_val.equalsIgnoreCase(CHAR)) {
										_dataType = AttributeDefinition.CHARACTER;
									}
									else
										if (ad_type_val.equalsIgnoreCase(BOOLEAN)) {
											_dataType = AttributeDefinition.BOOLEAN;
										}
										else
											if (ad_type_val.equalsIgnoreCase(SHORT)) {
												_dataType = AttributeDefinition.SHORT;
											}
											else {
												_isParsedDataValid = false;
												Logging.log(Logging.ERROR, this,
														"init(String, Attributes, Vector)", //$NON-NLS-1$
														Msg.formatter.getString(
																MISSING_ATTRIBUTE,
																	TYPE, name));
												return;

											}

			String ad_cardinality_str = atts.getValue(CARDINALITY);
			int ad_cardinality_val = 0;
			if (ad_cardinality_str == null) {
				// Not a problem, because CARDINALITY is an optional attribute.
				// And the default value is 0.
			}
			else {
				ad_cardinality_val = Integer.parseInt(ad_cardinality_str);
			}

			String ad_min_val = atts.getValue(MIN);
			if (ad_min_val == null) {
				// Not a problem, because MIN is an optional attribute.
			}

			String ad_max_val = atts.getValue(MAX);
			if (ad_max_val == null) {
				// Not a problem, because MAX is an optional attribute.
			}

			String ad_defaults_str = atts.getValue(DEFAULT);
			Vector ad_default_vector = new Vector(7);
			if (ad_defaults_str == null) {
				// Not a problem, because DEFAULT is an optional attribute.
				if (ad_cardinality_val == 0) {
					// But when it is not assigned, CARDINALITY cannot be '0'.
					_isParsedDataValid = false;
					Logging.log(Logging.ERROR, this,
							"init(String, Attributes, Vector)", //$NON-NLS-1$
							Msg.formatter.getString(NULL_DEFAULTS));
					return;
				}
			}

			String ad_required_val = atts.getValue(REQUIRED);
			if (ad_required_val == null) {
				// Not a problem, because REQUIRED is an optional attribute.
				// And the default value is 'true'.
				ad_required_val = Boolean.TRUE.toString();
			}

			_ad = new AttributeDefinitionImpl(ad_id_val,
					ad_name_val,
					ad_description_val,
					_dataType,
					ad_cardinality_val,
					convert(ad_min_val, _dataType),
					convert(ad_max_val, _dataType),
					Boolean.valueOf(ad_required_val).booleanValue(),
					_dp_localization);

			if (ad_cardinality_val == 0) {
				// Attribute DEFAULT has one and only one occurance.
				_ad.setDefaultValue(new String[] {ad_defaults_str}, false);
			}
			else {
				// Attribute DEFAULT is a comma delimited list.
				_ad.setDefaultValue(ad_defaults_str, false);
			}
		}

		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is AttributeDefinitionHandler:startElement():" //$NON-NLS-1$
					+ qName);
			if (!_isParsedDataValid)
				return;

			String name = getName(localName, qName);
			if (name.equalsIgnoreCase(OPTION)) {
				OptionHandler optionHandler = new OptionHandler(this);
				optionHandler.init(name, atts);
				if (optionHandler._isParsedDataValid) {
					// Only add valid Option
					_optionLabel_vector.addElement(optionHandler._label_val);
					_optionValue_vector.addElement(optionHandler._value_val);
				}
			}
			else {
				Logging.log(Logging.WARN,
						Msg.formatter.getString(UNEXPECTED_ELEMENT, name));
			}
		}

		protected void finished() throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is AttributeDefinitionHandler():finished()"); //$NON-NLS-1$
			if (!_isParsedDataValid)
				return;

			_ad.setOption(_optionLabel_vector, _optionValue_vector, true);
			_parent_ADs_vector.addElement(_ad);
		}
	}

	/**
	 * Handler for the Option element.
	 */
	private class OptionHandler extends AbstractHandler {

		String	_label_val;
		String	_value_val;

		public OptionHandler(ContentHandler handler) {
			super(handler);
		}

		public void init(String name, Attributes atts) throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is OptionHandler:init()"); //$NON-NLS-1$
			_label_val = atts.getValue(LABEL);
			if (_label_val == null) {
				_isParsedDataValid = false;
				Logging.log(Logging.ERROR, this,
						"init(String, Attributes)", //$NON-NLS-1$
						Msg.formatter.getString(
								MISSING_ATTRIBUTE, LABEL, name));
				return;
			}

			_value_val = atts.getValue(VALUE);
			if (_value_val == null) {
				_isParsedDataValid = false;
				Logging.log(Logging.ERROR, this,
						"init(String, Attributes)", //$NON-NLS-1$
						Msg.formatter.getString(
								MISSING_ATTRIBUTE, VALUE, name));
				return;
			}
		}
	}

//	/**
//	 * Handler for the Simple Value element.
//	 */
//	private class SimpleValueHandler extends AbstractHandler {
//
//		StringBuffer	_buffer	= new StringBuffer();
//		Vector			_parent_value_vector;
//		String			_elementName;
//
//		public SimpleValueHandler(ContentHandler handler) {
//			super(handler);
//		}
//
//		public void init(String name, Attributes atts, Vector value_vector)
//				throws SAXException {
//
//			Logging.log(Logging.TRACE,
//					"Here is SimpleValueHandler():init()"); //$NON-NLS-1$
//			_elementName = name;
//			_parent_value_vector = value_vector;
//		}
//
//		protected void finished() throws SAXException {
//
//			Logging.log(Logging.TRACE,
//					"Here is SimpleValueHandler():finished()"); //$NON-NLS-1$
//			if (_parent_value_vector != null) {
//				_parent_value_vector.addElement(_buffer.toString());
//			}
//		}
//
//		public void characters(char buf[], int offset, int len)
//				throws SAXException {
//
//			Logging.log(Logging.TRACE,
//					"Here is SimpleValueHandler(" //$NON-NLS-1$
//					+ _elementName
//					+ "):characters():[" //$NON-NLS-1$
//					+ new String(buf, offset, len)
//					+ "]"); //$NON-NLS-1$
//			_buffer.append(new String(buf, offset, len));
//		}
//	}

	/**
	 * Handler for the Designate element.
	 */
	private class DesignateHandler extends AbstractHandler {

		String	_pid_val		= null;
		boolean	_factory_val	= false;
		String	_bundle_val		= null;			// Only used by RFC94
		boolean	_optional_val	= false;		// Only used by RFC94
		boolean	_merge_val 		= false;		// Only used by RFC94

		// Referenced OCD ID
		String	_ocdref;

		public DesignateHandler(ContentHandler handler) {
			super(handler);
		}

		public void init(String name, Attributes atts) throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is DesignateHandler():init()"); //$NON-NLS-1$
			_pid_val = atts.getValue(PID);
			if (_pid_val == null) {
				_isParsedDataValid = false;
				Logging.log(Logging.ERROR, this,
						"init(String, Attributes)", //$NON-NLS-1$
						Msg.formatter.getString(
								MISSING_ATTRIBUTE, PID, name));
				return;
			}

			String factory_str = atts.getValue(FACTORY);
			if (factory_str == null) {
				// Not a problem, because FACTORY is an optional attribute.
				// And the default value is "false".
				_factory_val = false;
			}
			else {
				_factory_val = Boolean.valueOf(factory_str).booleanValue();
			}

			_bundle_val = atts.getValue(BUNDLE);
			if (_bundle_val == null) {
				// Not a problem because BUNDLE is an optional attribute.
			}

			String optional_str = atts.getValue(OPTIONAL);
			if (optional_str == null) {
				// Not a problem, because OPTIONAL is an optional attribute.
				// The default value is "false".
				_optional_val = false;
			}
			else {
				_optional_val = Boolean.valueOf(optional_str).booleanValue();
			}

			String merge_str = atts.getValue(MERGE);
			if (merge_str == null) {
				// Not a problem, because MERGE is an optional attribute.
				// The default value is "false".
				_merge_val = false;
			}
			else {
				_merge_val = Boolean.valueOf(merge_str).booleanValue();
			}
		}

		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is DesignateHandler:startElement():" //$NON-NLS-1$
					+ qName);
			if (!_isParsedDataValid)
				return;

			String name = getName(localName, qName);
			if (name.equalsIgnoreCase(OBJECT)) {
				ObjectHandler objectHandler = new ObjectHandler(this);
				objectHandler.init(name, atts);
				if (objectHandler._isParsedDataValid) {
					_ocdref = objectHandler._ocdref;
				}
			}
			else {
				Logging.log(Logging.WARN,
						Msg.formatter.getString(UNEXPECTED_ELEMENT, name));
			}
		}

		protected void finished() throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is DesignateHandler():finished()"); //$NON-NLS-1$
			if (!_isParsedDataValid)
				return;

			if (_ocdref == null) {
				_isParsedDataValid = false;
				// Schema defines at least one OBJECT is required.
				Logging.log(Logging.ERROR, this, "finished()", //$NON-NLS-1$
						Msg.formatter.getString(
								MISSING_ELEMENT, OBJECT, _pid_val));
				return;
				
			}
		}
	}

	/**
	 * Handler for the Object element.
	 */
	private class ObjectHandler extends AbstractHandler {

		String	_ocdref;

		public ObjectHandler(ContentHandler handler) {
			super(handler);
		}

		public void init(String name, Attributes atts) throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is ObjectHandler():init()"); //$NON-NLS-1$
			_ocdref = atts.getValue(OCDREF);
			if (_ocdref == null) {
				_isParsedDataValid = false;
				Logging.log(Logging.ERROR, this,
						"init(String, Attributes)", //$NON-NLS-1$
						Msg.formatter.getString(
								MISSING_ATTRIBUTE, OCDREF, name));
				return;
			}
		}

		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is ObjectHandler:startElement():" //$NON-NLS-1$
					+ qName);
			if (!_isParsedDataValid)
				return;

			String name = getName(localName, qName);
			if (name.equalsIgnoreCase(ATTRIBUTE)) {
				AttributeHandler attributeHandler = new AttributeHandler(this);
				attributeHandler.init(name, atts);
				// The ATTRIBUTE element is only used by RFC94, do nothing for it here.
			}
			else {
				Logging.log(Logging.WARN,
						Msg.formatter.getString(UNEXPECTED_ELEMENT, name));
			}
		}
	}

	/**
	 * Handler for the Attribute element.
	 * 
	 * This Handler is only used by RFC94.
	 */
	private class AttributeHandler extends AbstractHandler {

		String	_adref_val;
		String	_content_val;

		public AttributeHandler(ContentHandler handler) {
			super(handler);
		}

		public void init(String name, Attributes atts) throws SAXException {

			Logging.log(Logging.TRACE,
					"Here is AttributeHandler():init()"); //$NON-NLS-1$
			_adref_val = atts.getValue(ADREF);
			if (_adref_val == null) {
				_isParsedDataValid = false;
				Logging.log(Logging.ERROR, this,
						"init(String, Attributes)", //$NON-NLS-1$
						Msg.formatter.getString(
								MISSING_ATTRIBUTE, ADREF, name));
				return;
			}

			_content_val = atts.getValue(CONTENT);
			if (_content_val == null) {
				_isParsedDataValid = false;
				Logging.log(Logging.ERROR, this,
						"init(String, Attributes)", //$NON-NLS-1$
						Msg.formatter.getString(
								MISSING_ATTRIBUTE, CONTENT, name));
				return;
			}
		}
	}

	/**
	 * Error Handler to report errors and warnings
	 */
	private static class MyErrorHandler implements ErrorHandler {

		/** Error handler output goes here */
		private PrintStream	_out;

		MyErrorHandler(PrintStream out) {
			this._out = out;
		}

		/**
		 * Returns a string describing parse exception details
		 */
		private String getParseExceptionInfo(SAXParseException spe) {
			String systemId = spe.getSystemId();
			if (systemId == null) {
				systemId = "null"; //$NON-NLS-1$
			}
			String info = "URI=" + systemId + //$NON-NLS-1$
					" Line=" + spe.getLineNumber() + //$NON-NLS-1$
					": " + spe.getMessage(); //$NON-NLS-1$

			return info;
		}

		// The following methods are standard SAX ErrorHandler methods.
		// See SAX documentation for more info.

		public void warning(SAXParseException spe) throws SAXException {
			_out.println("Warning: " + getParseExceptionInfo(spe)); //$NON-NLS-1$
		}

		public void error(SAXParseException spe) throws SAXException {
			String message = "Error: " + getParseExceptionInfo(spe); //$NON-NLS-1$
			throw new SAXException(message);
		}

		public void fatalError(SAXParseException spe) throws SAXException {
			String message = "Fatal Error: " + getParseExceptionInfo(spe); //$NON-NLS-1$
			throw new SAXException(message);
		}
	}

	public static String getName(String localName, String qName) {
		return localName != null && localName.length() > 0 ? localName : qName;
	}
}
