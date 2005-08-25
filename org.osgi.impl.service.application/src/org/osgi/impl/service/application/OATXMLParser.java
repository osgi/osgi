/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.impl.service.application;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import java.security.*;
import java.util.LinkedList;

import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


class OATServiceData {	
	public final static int DYNAMIC = 1;
	public final static int STATIC  = 2;
	
	public final static int CARDINALITY0_1 = 1;
	public final static int CARDINALITY1_1 = 2;
	public final static int CARDINALITY0_n = 3;
	public final static int CARDINALITY1_n = 4;
	
	private String name_;	
	private String interface_;
	private int    policy_;
	private int    cardinality_;
	private String target_;
	
	public OATServiceData( String name, String iface, String target, int policy, int card ) {
		name_ = name;
		interface_ = iface;
		target_ = target;
		policy_ = policy;
		cardinality_ = card;
	}
	
	public String getName() {
		return name_;
	}
	
	public String getInterface() {
		return interface_;
	}
	
	public int getPolicy() {
		return policy_;
	}
	
	public int getCardinality() {
		return cardinality_;
	}
	
	public String getTarget() {
		return target_;
	}
}


class OATApplicationData {
	private String           baseClass_;
	private OATServiceData[] services_;
	
	public OATApplicationData( String baseClass, OATServiceData[] services ) {
		baseClass_ = baseClass;
		services_ = services;
	}
	
	public String getBaseClass() {
		return baseClass_;
	}
	
	public OATServiceData[] getServices() {
		return services_;
	}
}

public class OATXMLParser {
  public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
  public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  
  private boolean errorAtParse;

	public OATApplicationData parse( BundleContext bc, URL url, String baseClass ) throws Exception {		    
		if( url == null ) {
			Activator.log( LogService.LOG_ERROR, "Cannot open the OAT XML file!", null );
			throw new Exception( "Cannot open the OAT XML file!" );			
		}
		
		InputStream in = url.openStream();
		if ( in == null ) {
  			Activator.log( LogService.LOG_ERROR, "Cannot open the OAT XML file!", null );
				throw new Exception( "Cannot open the OAT XML file!" );
		}
		
	  validateXMLStream( bc, in );
	  in.close();
		in = url.openStream();
		if ( in == null ) {
			Activator.log( LogService.LOG_ERROR, "Cannot open the OAT XML file!", null );
			throw new Exception( "Cannot open the OAT XML file!" );
	  } 
		
		ServiceReference domParserReference = bc.getServiceReference( DocumentBuilderFactory.class.getName() );
		if (domParserReference == null) {
			Activator.log( LogService.LOG_ERROR, "Cannot find the DOM parser factory!", null );
			throw new Exception( "Cannot find the DOM parser factory!" );
		}
			
		DocumentBuilderFactory domFactory = (DocumentBuilderFactory) bc.getService( domParserReference );
		domFactory.setValidating( false );

    DocumentBuilder domParser = domFactory.newDocumentBuilder();
		
		if( domParser == null ) {
				Activator.log( LogService.LOG_ERROR, "Cannot create DOM parser!", null );
				bc.ungetService( domParserReference );
				throw new Exception( "Cannot create DOM parser!" );
		}

		Document doc = domParser.parse( in );
		in.close();
		
		OATApplicationData result = fetchApplicationData( doc, baseClass );
		
		bc.ungetService( domParserReference );
		return result;
	}

	private String getAttributeValue( Node node, String attribName ) {
		NamedNodeMap nnm = node.getAttributes();

		if(nnm != null ) {
			int len = nnm.getLength() ;

			for ( int i = 0; i < len; i++ ) {
				Attr attr = (Attr)nnm.item(i);
				if( attr.getNodeName().equals( attribName ) )
					return attr.getNodeValue();
			}
		}

		return null;
	}
	
	private OATApplicationData fetchApplicationData( Document doc, String baseClass ) throws Exception {
		
		NodeList listNodeList = doc.getElementsByTagName( "descriptor" );

		if( listNodeList.getLength() != 1 )
			throw new Exception( "Exactly one descriptor tag must be present in the apps.xml file!" );

		Node listNode = listNodeList.item( 0 );
		
		Node applicationNode = listNode.getFirstChild();

		while( applicationNode != null ) {
			if( applicationNode.getNodeType() == Node.ELEMENT_NODE  && 
					applicationNode.getNodeName().equals( "application" ) &&
					getAttributeValue( applicationNode, "class" ) != null &&
					getAttributeValue( applicationNode, "class" ).equals( baseClass )) {
				
				/* the desired application element found */
				
				LinkedList requiredServices = new LinkedList();
				
				NodeList nodeList = applicationNode.getChildNodes();

				for(int i=0; i < nodeList.getLength(); i++ ) {
					Node node = nodeList.item( i );
					if( node.getNodeType() == Node.ELEMENT_NODE && 
							node.getNodeName().equals( "reference" ) ) {
						
						String name_ = getAttributeValue( node, "name" );
						if( name_ == null )
							throw new Exception ("Internal parser error! Reference name not found!");
						String service_ = getAttributeValue( node, "interface" );
						if( service_ == null )
							throw new Exception ("Internal parser error! Service not found!");
						String target_ = getAttributeValue( node, "target" );
						
						int policy_ = OATServiceData.STATIC;						
						String policyStr = getAttributeValue( node, "policy" );
						if( policyStr != null ) {
							if( policyStr.equals("dynamic") )
			            policy_ = OATServiceData.DYNAMIC;
							else if( !policyStr.equals("static") )
								throw new Exception ("Internal parser error! Invalid policy value!");
						}
						
						int cardinality_ = OATServiceData.CARDINALITY1_1;
						String cardinalityStr = getAttributeValue( node, "cardinality" );
						if( cardinalityStr != null ) {
							if( cardinalityStr.equals("0..1") )
								cardinality_ = OATServiceData.CARDINALITY0_1;
							else if( cardinalityStr.equals("0..n") )
								cardinality_ = OATServiceData.CARDINALITY0_n;
							else if( cardinalityStr.equals("1..n") )
								cardinality_ = OATServiceData.CARDINALITY1_n;
							else if( !cardinalityStr.equals("1..1"))
								throw new Exception ("Internal parser error! Invalid cardinality value!");
						}
						
						requiredServices.add( new OATServiceData( name_, service_, target_, 
								                                      policy_, cardinality_ ) );
					}
				}
				
				OATServiceData services_[] = new OATServiceData[ requiredServices.size() ];
				int i=0;
				while( requiredServices.size() != 0 )
					services_[ i++ ] = (OATServiceData)requiredServices.removeFirst();
				return new OATApplicationData( baseClass, services_ );
			}
			applicationNode = applicationNode.getNextSibling();
	  }
		throw new Exception( "Application description wasn't found for '"+ baseClass +"' base class!" );
	}
	
	private void validateXMLStream( BundleContext bc, InputStream stream ) throws Exception {
		final InputSource is = new InputSource( stream );
		
		errorAtParse = false;
		
		ServiceReference saxParserReferences[] = bc.getServiceReferences( SAXParserFactory.class.getName(), "(&(parser.namespaceAware=true)(parser.validating=true))" );
		if (saxParserReferences == null || saxParserReferences.length == 0 ) {
			Activator.log( LogService.LOG_ERROR, "Cannot find the SAX parser factory!", null );
			throw new Exception( "Cannot find the SAX parser factory!" );
		}		
		
		SAXParserFactory saxParserFactory = (SAXParserFactory)bc.getService( saxParserReferences[ 0 ] );
		
		try {
		  saxParserFactory.setValidating(true);
		  saxParserFactory.setNamespaceAware(true);

		  SAXParser saxParser = saxParserFactory.newSAXParser();
		  saxParser.setProperty( JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA );
			URL oatSchemaURL = OATXMLParser.class.getResource("OATXMLSchema.xsd");
			saxParser.setProperty(JAXP_SCHEMA_SOURCE, oatSchemaURL.openStream() );
			
			final XMLReader xr = saxParser.getXMLReader();
			xr.setErrorHandler( new ErrorHandler() {

				public void warning(SAXParseException arg0) throws SAXException {
					System.err.println( "WARNING: " + arg0 );					
				}

				public void error(SAXParseException arg0) throws SAXException {
					System.err.println( "ERROR: " + arg0 );					
				}

				public void fatalError(SAXParseException arg0) throws SAXException {
					System.err.println( "FATAL ERROR: " + arg0 );					
				}} );

  			AccessController.doPrivileged(new PrivilegedExceptionAction() {
	  			public java.lang.Object run() throws Exception {
		  			xr.parse( is );
			  		return null;
				  }});
		}
		finally {
    	bc.ungetService( saxParserReferences[ 0 ] );			
		}
		
		if( errorAtParse )
			throw new Exception( "" );		
	}
}
