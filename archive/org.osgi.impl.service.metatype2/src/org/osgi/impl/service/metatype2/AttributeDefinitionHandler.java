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
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype2.ExtendedAttributeDefinition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 * 
 * @version $Revision$
 */
class AttributeDefinitionHandler extends AbstractHandler implements Cloneable {
  static final String ATTRIBUTE_TAG = "Attribute";
  
  private static final String TYPE_ATTRIBUTE = "type";
  private static final String CARDINALITY_ATTRIBUTE = "cardinality";
  
  private static final String[] TYPE_NAMES = {
    "String", "Long", "Double", "Float", "Integer", "Byte", "Char", "Boolean",
    "Short", "ObjectClass" 
  };
  
  private static final int[] TYPE_CODES = {
    AttributeDefinition.STRING, AttributeDefinition.LONG, 
    AttributeDefinition.DOUBLE, AttributeDefinition.FLOAT, 
    AttributeDefinition.INTEGER, AttributeDefinition.BYTE, 
    AttributeDefinition.CHARACTER, AttributeDefinition.BOOLEAN,
    AttributeDefinition.SHORT, ExtendedAttributeDefinition.OBJECT_CLASS
  };
  
  private String id;
  private int type;
  private int cardinality;

  private Vector options;
  private Hashtable properties;
  private Vector attributeDefinitionsVector;
  private ExtendedAttributeDefinition[] attributeDefinitions;
  
  private String[] optionLabels;
  private String[] optionValues;
  private Vector icons;
  
  protected Hashtable definitionsTable;

  private Bundle bundle;

  private Localizator localizator;
   
  AttributeDefinitionHandler(XMLReader xmlReader, 
    DefaultHandler parentHandler, Attributes attributes, 
    Hashtable definitionsTable, Bundle bundle) throws SAXException {
    
    super(ATTRIBUTE_TAG, xmlReader, parentHandler);
    this.definitionsTable = definitionsTable;
    this.bundle = bundle;
    
    id = getRequiredAttribute(attributes, ID_ATTRIBUTE);
    
    String typeString = getRequiredAttribute(attributes, TYPE_ATTRIBUTE);
    type = typeStringToCode(typeString);
    
    String cardinalityString = attributes.getValue(CARDINALITY_ATTRIBUTE);
    cardinality = cardinalityStringToInt(cardinalityString);
  }
  
  String getID() {
    return id;
  }
  
  Dictionary getProperties() {
    return properties;
  }
  
  ExtendedAttributeDefinition[] getAttributeDefinitions() {
    return attributeDefinitions;
  }
  
  public InputStream getIcon(int size) throws IOException {
    return getIcon(icons, localizator, size);
  }
  
  int getCardinality() {
    return cardinality;
  }
  
  int getType() {
    return type;
  }
  
  String[] getOptionLabels() {
    if (optionLabels == null) {
      return null;
    }
    
    String[] result = new String [optionLabels.length];
    for (int i = 0; i < result.length; i++) {
      result [i] = localizator.getLocalized(optionLabels [i]);
    }
                                  
    return result;
  }
  
  String[] getOptionValues() {
    return optionValues;
  }
  
  //******* DefaultHandler overriden methods *******//
  public void startElement(String namespaceURI, String localName, 
                           String qualifiedName, Attributes attributes) throws SAXException  {
    
    String tag = getElementName(localName, qualifiedName);
    
    if ( ATTRIBUTE_TAG.equals(tag) ) {
      handleAttribute(attributes);
    } else if (IconHandler.ICON_TAG.equals(tag)) {
      handleIcon(attributes);
    } else if (OptionHandler.OPTION_TAG.equals(tag)) {
      handleOption(attributes);
    } else if (PropertyHandler.PROPERTY_TAG.equals(tag)) {
      handleProperty(attributes);
    } else {
      super.startElement(namespaceURI, localName, qualifiedName, attributes);
    }
  }
  
  public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
    if (attributeDefinitionsVector != null) {
      attributeDefinitions = new ExAttributeDefinitionImpl [attributeDefinitionsVector.size()];
      attributeDefinitionsVector.copyInto(attributeDefinitions);
      
      attributeDefinitionsVector = null;
    }
    
    if (options != null) {
      optionLabels = new String [options.size()];
      optionValues = new String [options.size()];
      
      for (int i = 0; i < options.size(); i++) {
        OptionHandler option = (OptionHandler)options.elementAt(i);
        
        optionLabels [i] = option.getLabel();
        optionValues [i] = option.getValue();
      }
    }
    
    definitionsTable = null;
    
    super.endElement(namespaceURI, localName, qualifiedName);
  }
  //******* END DefaultHandler overriden methods *******//
  
  protected int typeStringToCode(String type) throws SAXException {
    for (int i = 0; i < TYPE_NAMES.length; i++) {
      if ( TYPE_NAMES [i].equals(type) ) {
        return TYPE_CODES [i];
      }
    }
    
    throw new SAXException("Invalid Attribute type '" + type + "'!");
  }
  
  private void handleProperty(Attributes attributes) throws SAXException {
    if (properties == null) {
      properties = new Hashtable();
    }
    
    PropertyHandler property = new PropertyHandler(xmlReader, this, attributes);
    properties.put(property.getKey(), property.getValue());
  }
  
  private void handleIcon(Attributes attributes) throws SAXException {
    IconHandler icon = new IconHandler(xmlReader, this, attributes, bundle);
    
    if (icons == null) {
      icons = new Vector();
    }
    
    icons.addElement(icon);
  }
  
  private void handleOption(Attributes attributes) throws SAXException {
    if (options == null) {
      options = new Vector();
    }
    
    OptionHandler option = new OptionHandler(xmlReader, this, attributes);
    options.addElement(option);
  }
  
  private void handleAttribute(Attributes attributes) throws SAXException {
    if (type != ExtendedAttributeDefinition.OBJECT_CLASS) {
      throw new SAXException("Attribute reference should be defined only if the type of the attribute element is ObjectClass");
    }
  
    ReferenceHandler reference = new ReferenceHandler(xmlReader,
      this, attributes, definitionsTable, ATTRIBUTE_TAG); 
    
    if (attributeDefinitionsVector == null) {
      attributeDefinitionsVector = new Vector(5, 2);
    }
    
    attributeDefinitionsVector.addElement( 
        new ExAttributeDefinitionImpl(reference) );
  }
  
  private static int cardinalityStringToInt(String cardinality) throws SAXException {
    if (cardinality == null) {
      return 0;
    }
    
    return parseIntAttribute(CARDINALITY_ATTRIBUTE, cardinality);
  }

  public Object clone() throws CloneNotSupportedException {
    AttributeDefinitionHandler result = (AttributeDefinitionHandler) super.clone();
    
    result.attributeDefinitions = cloneDefinitions(attributeDefinitions);
    
    return result;
  }
  
  void setLocalizator(Localizator localizator) {
    this.localizator = localizator;
    
    setDefinitionsLocalizator(attributeDefinitions, localizator);
  }

  protected static void setDefinitionsLocalizator(ExtendedAttributeDefinition[] definitions, Localizator localizator) {
    if (definitions == null) {
      return;
    }
    
    for (int i = 0; i < definitions.length; i++) {
      ExAttributeDefinitionImpl definition = (ExAttributeDefinitionImpl) definitions [i]; 
      definition.setLocalizator(localizator);
    }
  }
  
  protected static ExtendedAttributeDefinition[] cloneDefinitions(ExtendedAttributeDefinition[] definitions) throws CloneNotSupportedException {
    if (definitions == null) {
      return null;
    }
    
    ExtendedAttributeDefinition[] result = new ExtendedAttributeDefinition [definitions.length];
    for (int i = 0; i < definitions.length; i++) {
      ExAttributeDefinitionImpl definition = (ExAttributeDefinitionImpl)definitions [i];
      
      result [i] = (ExtendedAttributeDefinition) definition.clone();
    }
    
    return result;
  }
  
}
