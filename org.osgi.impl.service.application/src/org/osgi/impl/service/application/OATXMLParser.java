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

import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.w3c.dom.Document;

public class OATXMLParser {
  public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
  public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  
  private boolean errorAtParse;

	public void parse( BundleContext bc, URL url ) throws Exception {		    
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
		
		bc.ungetService( domParserReference );
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
