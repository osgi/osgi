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

import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.service.metatype2.ActionDefinition;
import org.osgi.service.metatype2.ExtendedAttributeDefinition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 * 
 * @version $Revision$
 */
class ActionHandler extends AttributeDefinitionHandler {

  static final String ACTION_TAG = "Action";
  
  private static final String ARGUMENT_TAG = "Argument";
  private static final String VOID_TYPE = "Void";

  private Vector inputArgumentsVector;
  private ExtendedAttributeDefinition[] inputArguments;
  
  ActionHandler(XMLReader xmlReader, DefaultHandler parentHandler, 
    Attributes attributes, Hashtable actionsTable, Bundle bundle) throws SAXException {
    super(xmlReader, parentHandler, attributes, actionsTable, bundle);
  }
  
  ExtendedAttributeDefinition[] getInputArgumentDefinitions() {
    return inputArguments;
  }
  
  public Object clone() throws CloneNotSupportedException {
    ActionHandler result = (ActionHandler)super.clone();
    
    result.inputArguments = cloneDefinitions(inputArguments);
    
    return result;
  }
  
  void setLocalizator(Localizator localizator) {
    super.setLocalizator(localizator);
    
    setDefinitionsLocalizator(inputArguments, localizator);
  }
  
  //******* AbstractHandler overriden methods *******//
  
  public void startElement(String namespaceURI, String localName, 
                           String qualifiedName, Attributes attributes) throws SAXException  {
    
    String tag = getElementName(localName, qualifiedName);
    
    if ( ARGUMENT_TAG.equals(tag) ) {
      ReferenceHandler reference = new ReferenceHandler(xmlReader,
        this, attributes, definitionsTable, ARGUMENT_TAG); 
      
      if (inputArgumentsVector == null) {
        inputArgumentsVector = new Vector(5, 2);
      }
      
      inputArgumentsVector.addElement( new ExAttributeDefinitionImpl(reference) );
    } else {
      super.startElement(namespaceURI, localName, qualifiedName, attributes);
    }
  }

  public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
    if (inputArgumentsVector != null) {
      inputArguments = new ExAttributeDefinitionImpl [inputArgumentsVector.size()];
      inputArgumentsVector.copyInto(inputArguments);
      
      inputArgumentsVector = null;
    }
    
    super.endElement(namespaceURI, localName, qualifiedName);
  }

  //******* END AbstractHandler overriden methods *******//
  
  protected int typeStringToCode(String type) throws SAXException {
    if ( VOID_TYPE.equals(type) ) {
      return ActionDefinition.VOID;  
    }
    
    return super.typeStringToCode(type);
  }
  
}
