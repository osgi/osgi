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

import org.osgi.framework.Bundle;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype2.ActionDefinition;
import org.osgi.service.metatype2.ExtendedObjectClassDefinition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 * 
 * @version $Revision$
 */
class ObjectClassDefinitionHandler extends AbstractHandler 
  implements ExtendedObjectClassDefinition, Cloneable {
  
  static final String OCD_TAG = "ObjectClassDefinition";

  private static final String ATTRIBUTE_REF_TAG = "AttributeRef";
  private static final String ACTION_REF_TAG = "ActionRef";
  
  private String id;
  private String name;
  private String description;
  
  private Vector icons;
  
  private Hashtable attributeDefinitions;
  private Hashtable actionDefinitions;
  
  private Hashtable attributeDeclarations;
  private Hashtable actionDeclarations;

  private Bundle bundle;
  
  private Localizator localizator;
  
  ObjectClassDefinitionHandler(XMLReader reader, DefaultHandler parentHandler, 
    Attributes attributes, Hashtable attributeDeclarations, 
    Hashtable actionDeclarations, Bundle bundle) throws SAXException {
    
    super(OCD_TAG, reader, parentHandler);
  
    this.bundle = bundle;
    
    id = getRequiredAttribute(attributes, ID_ATTRIBUTE);
    name = attributes.getValue(NAME_ATTRIBUTE);
    description = attributes.getValue(DESCRIPTION_ATTRIBUTE);
    
    this.attributeDeclarations = attributeDeclarations;
    this.actionDeclarations = actionDeclarations;
  }
  
  void setLocalizator(Localizator localizator) {
    this.localizator = localizator;
    
    setDefinitionsLocalizator(attributeDefinitions, localizator);
    setDefinitionsLocalizator(actionDefinitions, localizator);
  }
  
  protected Object clone() {
    try {
      ObjectClassDefinitionHandler result = (ObjectClassDefinitionHandler)super.clone();
      
      result.attributeDeclarations = cloneDefinitionsTable(attributeDeclarations);
      result.actionDefinitions = cloneDefinitionsTable(actionDefinitions);
      return result;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace(); // never
    }
    
    return null;
  }
  
  //******* ExtendedObjectClassDefinition interface methods *******//

  public String getDescription() {
    return localizator.getLocalized(description);
  }
  
  public String getName() {
    return localizator.getLocalized(name);
  }
  
  public InputStream getIcon(int size) throws IOException {
    return getIcon(icons, localizator, size);
  }
  
  public String getID() {
    return id;
  }
  
  public AttributeDefinition[] getAttributeDefinitions(int filter) {
    if (attributeDefinitions == null || filter == OPTIONAL) {
      return null;
    }
    
    AttributeDefinition[] result = new AttributeDefinition [attributeDefinitions.size()];
    Enumeration definitions = attributeDefinitions.elements();
    int counter = 0;
    while ( definitions.hasMoreElements() ) {
      result [counter++] = (AttributeDefinition)definitions.nextElement();
    }
    
    return result;
  }
  
  public AttributeDefinition getAttributeDefinition(String id) {
    return attributeDefinitions != null ? 
      (AttributeDefinition)attributeDefinitions.get(id) : null; 
  }

  public ActionDefinition[] getActionDefinitions() {
    if (actionDefinitions == null) {
      return null;
    }
    
    ActionDefinition[] result = new ActionDefinition [actionDefinitions.size()];
    Enumeration definitions = actionDefinitions.elements();
    int counter = 0;
    while ( definitions.hasMoreElements() ) {
      result [counter++] = (ActionDefinition)definitions.nextElement();
    }
    
    return result;
  }

  public ActionDefinition getActionDefinition(String id) {
    return actionDefinitions != null ? 
      (ActionDefinition)actionDefinitions.get(id) : null;
  }

  //******* END ExtendedObjectClassDefinition interface methods *******//
  
  //******* AbstractHandler overriden methods *******//
  
  public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes) throws SAXException {
    String tag = getElementName(localName, qualifiedName);
    
    if ( ATTRIBUTE_REF_TAG.equals(tag) ) {
      handleAttributeReference(attributes);
    } else if ( ACTION_REF_TAG.equals(tag) ) {
      handleActionReference(attributes);
    } else if ( IconHandler.ICON_TAG.equals(tag) ) {
      handleIcon(attributes);
    } else {
      super.startElement(namespaceURI, localName, qualifiedName, attributes);
    }
  }

  private void handleIcon(Attributes attributes) throws SAXException {
    IconHandler icon = new IconHandler(xmlReader, this, attributes, bundle);
    
    if (icons == null) {
      icons = new Vector();
    }
    
    icons.addElement(icon);
  }

  public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
    attributeDeclarations = null;
    actionDeclarations = null;
    
    super.endElement(namespaceURI, localName, qualifiedName);
  }
  
  //******* END AbstractHandler overriden methods *******//
  
  private void handleAttributeReference(Attributes attributes) throws SAXException {
    ReferenceHandler reference = new ReferenceHandler(xmlReader,
      this, attributes, attributeDeclarations, ATTRIBUTE_REF_TAG); 
    
    if (attributeDefinitions == null) {
      attributeDefinitions = new Hashtable();
    }
    
    attributeDefinitions.put(reference.getId(), 
      new ExAttributeDefinitionImpl(reference));
  }
  
  private void handleActionReference(Attributes attributes) throws SAXException {
    ReferenceHandler reference = new ReferenceHandler(xmlReader,
      this, attributes, actionDeclarations, ACTION_REF_TAG); 
    
    if (actionDefinitions == null) {
      actionDefinitions = new Hashtable();
    }
    
    actionDefinitions.put(reference.getId(), new ActionDefinitionImpl(reference));
  }
  
  private void setDefinitionsLocalizator(Hashtable definitionsTable, Localizator localizator) {
    if (definitionsTable == null) {
      return;
    }
    
    Enumeration definitions = definitionsTable.elements();
    while ( definitions.hasMoreElements() ) {
      ExAttributeDefinitionImpl definition = (ExAttributeDefinitionImpl)definitions.nextElement();
      
      definition.setLocalizator(localizator);
    }
  }
  
}
