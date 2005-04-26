/*
* $Header$
* 
* Copyright (c) OSGi Alliance (2004). All Rights Reserved.
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
package org.osgi.impl.service.metatype2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 * 
 * @version $Revision$
 */
abstract class AbstractHandler extends DefaultHandler {
  protected static final String UNEXPECTED_ELEMENT_MESSAGE = "Unexpected element : ";
  protected static final String ID_ATTRIBUTE = "id";
  protected static final String NAME_ATTRIBUTE = "name";
  protected static final String DESCRIPTION_ATTRIBUTE = "description";
  
  protected XMLReader xmlReader;

  private DefaultHandler parentHandler;
  private String tagName;
  
  AbstractHandler(String tagName, XMLReader reader, DefaultHandler parentHandler) {
    this.tagName = tagName;
    this.xmlReader = reader;
    this.parentHandler = parentHandler;
    
    xmlReader.setContentHandler(this);
  }
  
  public void startElement(String namespaceURI, String localName, 
                           String qualifiedName, Attributes attributes) throws SAXException {
    
    throw new SAXException("Unexpected element '" +
        getElementName(localName, qualifiedName) + "' found in element '" + tagName + "'!");    
  }
  
  public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
    if (parentHandler != null) {
      xmlReader.setContentHandler(parentHandler);
    }
  }
  
  public void warning(SAXParseException spe) throws SAXException {
    throw spe;
  }

  public void error(SAXParseException spe) throws SAXException {
    throw spe;
  }

  public void fatalError(SAXParseException spe) throws SAXException {
    throw spe;
  }
  
  protected String getElementName(String localName, String qualifiedName) {
    return localName != null && localName.length() > 0 ? localName : qualifiedName;
  }
  
  public void characters(char[] chars, int start, int end) throws SAXException {
    String str = new String(chars, start, end).trim();
    if (str.length() > 0) {
      throw new SAXException("Unexpected text : '" + str + "' in element '" + tagName + "'!");
    }
  }
  
  protected String getRequiredAttribute(Attributes attributes, String name) throws SAXException {
    String value = attributes.getValue(name);
    
    if (value == null) {
      throw new SAXException("Missing required attribute '" + name + 
        "' in element '" + tagName + "'!");
    }
    
    return value;
  }
  
  protected static int parseIntAttribute(String name, String value) throws SAXException {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException ex) {
      throw new SAXException("Value '" + value + "' of '" + 
         name + "' is not a parsable integer!");
    }
  }

  protected static InputStream getIcon(Vector icons, Localizator localizator, int size) throws IOException {
    if (icons == null) {
      return null;
    }
    
    for (int i = 0; i < icons.size(); i++) {
      IconHandler icon = (IconHandler)icons.elementAt(i);
      
      if (icon.getSize() == size) {
        return icon.getIcon(localizator);
      }
    }
    
    return ((IconHandler)icons.elementAt(0)).getIcon(localizator);
  }
  
  protected Hashtable cloneDefinitionsTable(Hashtable table) throws CloneNotSupportedException {
    if (table == null) {
      return null;
    }
    
    Hashtable result = new Hashtable(table.size(), 1);
    Enumeration keys = table.keys();
    while ( keys.hasMoreElements() ) {
      Object key = keys.nextElement();
      ExAttributeDefinitionImpl definition = (ExAttributeDefinitionImpl) table.get(key);
      
      result.put(key, definition.clone());
    }
    
    return result;
  }

}
