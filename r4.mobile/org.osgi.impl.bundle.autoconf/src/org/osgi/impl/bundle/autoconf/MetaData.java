/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.bundle.autoconf;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.osgi.service.metatype.AttributeDefinition;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 *
 * Java class representing metadata read from the autoconf.xsd
 * 
 * @version $Revision$
 */
public class MetaData  {
    public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    public Designate[] designates;
    public OCD[] ocds;
    public String localization;
    
    public static class Designate {
    	public String pid;
    	public String factoryPid;
    	public String bundle;
    	public boolean optional;
    	public Object[] objects;
		public boolean	merge;
    };
    
    public static class Object {
    	public String ocdref;
    	public Attribute[] attributes;
    }
    
    public static class Attribute {
    	public String adref,content;
    }

    public static class OCD {
    	public String name,description,id;
    	public AD[] ads;
    	public Icon[] icons;
    }
    
    public static class AD {
		public AD() {}
    	public AD(AttributeDefinition rad) {
			name = rad.getName();
			description = rad.getDescription();
			id = rad.getID();
			cardinality = rad.getCardinality();
			type = rad.getType();
			defaultValue = rad.getDefaultValue();
		}
		public String name,description,id;
    	public long min,max;
    	public int cardinality;
    	public int type;
    	public String[] defaultValue;
    	public Option[] options;
    }
    
    public static class Icon {
    	public String resource;
    	public int size;
    }
    
    public static class Option {
    	public String label;
    	public String value;
    }

    static final ErrorHandler errorHandler = new ErrorHandler() {
		public void error(SAXParseException exception) throws SAXException { throw exception; }
		public void fatalError(SAXParseException exception) throws SAXException { throw exception; }
		public void warning(SAXParseException exception) throws SAXException {}
	};
	
	class Parser implements ContentHandler {
		public static final int START_DOCUMENT = 0;
		public static final int METADATA = 1;
		public static final int OCD = 2;
		public static final int DESIGNATE = 3;
		public static final int OBJECT = 4;
		public static final int AD = 5;
		public static final int ICON = 6;
		public static final int OPTION = 7;
		public static final int ATTRIBUTE = 8;
		private int state;
		
		private Designate currentDesignate;
		private Object currentObject;
		private OCD currentOCD;
		private AD currentAD;
		private ArrayList designateA = new ArrayList(10);
		private ArrayList ocdsA = new ArrayList(10);
		private ArrayList objectsA = new ArrayList(10);
		private ArrayList adsA = new ArrayList(10);
		private ArrayList attributesA = new ArrayList(10);
		private ArrayList iconsA = new ArrayList(10);
		private ArrayList optionsA = new ArrayList(10);
		
		public void setDocumentLocator(Locator arg0) {
		}

		public void startDocument() throws SAXException {
			state = START_DOCUMENT;
		}

		public void endDocument() throws SAXException {
		}

		public void startPrefixMapping(String arg0, String arg1) throws SAXException {
		}

		public void endPrefixMapping(String arg0) throws SAXException {
		}

		public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException {
			switch(state) {
				case START_DOCUMENT:
					if (!"http://www.osgi.org/xmlns/metatype/v1.0.0".equals(namespaceURI)||
						!"MetaData".equals(localName)) {
						throw new SAXException("document should start with a MetaData element");
					}
					state = METADATA;
					break;
				case METADATA:
					localization = attr.getValue("localization");
					if ("OCD".equals(localName)) {
						state = OCD;
						currentOCD = new OCD();
						currentOCD.id = attr.getValue("id");
						currentOCD.name = attr.getValue("name");
						currentOCD.description = attr.getValue("description");
						adsA.clear();
						iconsA.clear();
						ocdsA.add(currentOCD);
					} else if ("Designate".equals(localName)){
						state = DESIGNATE;
						currentDesignate = new Designate();
						currentDesignate.pid = attr.getValue("pid");
						currentDesignate.factoryPid = attr.getValue("factoryPid");
						if ("".equals(currentDesignate.factoryPid)) currentDesignate.factoryPid = null;
						currentDesignate.bundle = attr.getValue("bundle");
						currentDesignate.optional = getBool(attr,"optional",false);
						currentDesignate.merge = getBool(attr,"merge",false);
						objectsA.clear();
						designateA.add(currentDesignate);
					} else {
						throw new IllegalStateException(localName);
					}
					break;
				case DESIGNATE:
					state = OBJECT;
					currentObject = new Object();
					attributesA.clear();
					objectsA.add(currentObject);
					currentObject.ocdref = attr.getValue("ocdref");
					break;
				case OBJECT:
					// attributes
					state = ATTRIBUTE;
					Attribute a = new Attribute();
					a.adref = attr.getValue("adref");
					a.content = attr.getValue("content");
					attributesA.add(a);
					break;
				case OCD:
					if ("AD".equals(localName)) {
						state = AD;
						currentAD = new AD();
						adsA.add(currentAD);
						currentAD.name = attr.getValue("name");
						currentAD.description = attr.getValue("description");
						currentAD.id = attr.getValue("id");
						String type = attr.getValue("type");
						if (type.equals("String")) {
							currentAD.type = AttributeDefinition.STRING;
						} else if (type.equals("Long")) {
							currentAD.type = AttributeDefinition.LONG;
						} else if (type.equals("Double")) {
							currentAD.type = AttributeDefinition.DOUBLE;
						} else if (type.equals("Float")) {
							currentAD.type = AttributeDefinition.FLOAT;
						} else if (type.equals("Integer")) {
							currentAD.type = AttributeDefinition.INTEGER;
						} else if (type.equals("Byte")) {
							currentAD.type = AttributeDefinition.BYTE;
						} else if (type.equals("Char")) {
							currentAD.type = AttributeDefinition.CHARACTER;
						} else if (type.equals("Boolean")) {
							currentAD.type = AttributeDefinition.BOOLEAN;
						} else if (type.equals("Short")) {
							currentAD.type = AttributeDefinition.SHORT;
						}
						String card = attr.getValue("cardinality");
						if (card==null) {
							currentAD.cardinality = 0;
						} else {
							currentAD.cardinality = Integer.parseInt(card);
							// TODO check if schema integer and Java integer formats are compatible
						}
						
						// TODO min, max
						String defaultValue = attr.getValue("default");
						if (defaultValue!=null) {
							if (currentAD.cardinality==0) {
								currentAD.defaultValue = new String[] { defaultValue };
							} else {
								ArrayList values = new ArrayList();
								StringBuffer tmp = new StringBuffer();
								for(int i=0;i<defaultValue.length();i++) {
									char c = defaultValue.charAt(i);
									if (c==',') {
										values.add(tmp.toString());
										tmp = new StringBuffer();
										continue;
									}
									if (c=='\\') {
										i++;
										c = defaultValue.charAt(i);
										if ((c!='\\')&&(c!=',')) {
											tmp.append('\\');
										}
									}
									tmp.append(c);
								}
								values.add(tmp.toString());
								currentAD.defaultValue = new String[values.size()];
								currentAD.defaultValue = (String[]) values.toArray(currentAD.defaultValue);
							}
						}
						optionsA.clear();
						
					} else {
						state = ICON;
						Icon icon = new Icon();
						icon.resource = attr.getValue("resource");
						icon.size = Integer.parseInt(attr.getValue("size"));
						iconsA.add(icon);
					}
					break;
				case AD:
					state = OPTION;
					Option option = new Option();
					option.label = attr.getValue("label");
					option.value = attr.getValue("value");
					optionsA.add(option);
					break;
			    default:
			    	throw new IllegalStateException(""+state);
			}
			
		}

		/**
		 * gets a boolean value from an attribute
		 */
		private boolean getBool(Attributes attr, String string, boolean defaultValue) {
			String v = attr.getValue(string);
			if (v==null) return defaultValue;
			if (v.equals("0")||v.equals("false")) return false;
			return true;
		}

		public void endElement(String arg0, String arg1, String arg2) throws SAXException {
			switch(state) {
				case OCD:
					state = METADATA;
					currentOCD.ads = new AD[adsA.size()];
					adsA.toArray(currentOCD.ads);
					currentOCD.icons = new Icon[iconsA.size()];
					iconsA.toArray(currentOCD.icons);
					break;
				case DESIGNATE:
					state = METADATA;
					currentDesignate.objects = new Object[objectsA.size()];
					objectsA.toArray(currentDesignate.objects);
					break;
				case OBJECT:
					state = DESIGNATE;
					currentObject.attributes = new Attribute[attributesA.size()];
					attributesA.toArray(currentObject.attributes);
					break;
				case AD:
					state = OCD;
					currentAD.options = new Option[optionsA.size()];
					optionsA.toArray(currentAD.options);
					break;
				case ICON:
					state = OCD;
					break;
				case OPTION:
					state = AD;
					break;
				case ATTRIBUTE:
					state = OBJECT;
					break;
				case METADATA:
					designates = new Designate[designateA.size()];
					designateA.toArray(designates);
					ocds = new OCD[ocdsA.size()];
					ocdsA.toArray(ocds);
					break;
			    default:
			    	throw new IllegalStateException(""+state);
			}
		}

		/**
		 * @param arg0
		 * @param arg1
		 * @param arg2
		 * @throws SAXException
		 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
		 */
		public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
			// TODO Auto-generated method stub
			
		}

		/**
		 * @param arg0
		 * @param arg1
		 * @param arg2
		 * @throws SAXException
		 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
		 */
		public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
			// TODO Auto-generated method stub
			
		}

		/**
		 * @param arg0
		 * @param arg1
		 * @throws SAXException
		 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
		 */
		public void processingInstruction(String arg0, String arg1) throws SAXException {
			// TODO Auto-generated method stub
			
		}

		/**
		 * @param arg0
		 * @throws SAXException
		 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
		 */
		public void skippedEntity(String arg0) throws SAXException {
			// TODO Auto-generated method stub
			
		}
		
	}
	
    public MetaData(SAXParser sp,final InputSource is) throws ParserConfigurationException, IOException, SAXException, PrivilegedActionException {
		final XMLReader xr = sp.getXMLReader();
		xr.setErrorHandler(errorHandler);
		xr.setContentHandler(new Parser());

		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public java.lang.Object run() throws Exception {
					xr.parse(is);
					return null;
				}});
		}
		catch (PrivilegedActionException e) {
			Exception original = e.getException();
			if (original==null) throw e;
			if (original instanceof ParserConfigurationException) { throw (ParserConfigurationException) original; }
			if (original instanceof IOException) { throw (IOException) original; }
			if (original instanceof SAXException) { throw (SAXException) original; }
			throw e;
		}
		// if there was any trouble, contenthandler functions threw error, so that's it
	}


}
