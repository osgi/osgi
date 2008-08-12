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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 * 
 * @version $Revision$
 */
class ReferenceHandler extends AbstractHandler implements Cloneable {
  
  private static final String REF_ID_ATTRIBUTE = "refid";
  
  private String id;
  private String name;
  private String description;
  
  private AttributeDefinitionHandler referencedObject;

  private Localizator localizator;
  
  ReferenceHandler(XMLReader xmlReader, DefaultHandler parentHandler, 
    Attributes attributes, Hashtable definitionsTable, String tagName) throws SAXException {
    
    super(tagName, xmlReader, parentHandler);
  
    String referenceID = getRequiredAttribute(attributes, REF_ID_ATTRIBUTE);
    referencedObject = (AttributeDefinitionHandler) definitionsTable.get(referenceID);
    if (referencedObject == null) {
      throw new SAXException("Referenced - in element '" + tagName + "' - object with ID '" + referenceID + "' not declared!");
    }
    
    id = getRequiredAttribute(attributes, ID_ATTRIBUTE);
    name = attributes.getValue(NAME_ATTRIBUTE);
    description = attributes.getValue(DESCRIPTION_ATTRIBUTE);
  }

  String getId() {
    return id;
  }
  
  String getDescription() {
    return localizator.getLocalized(description);
  }
  
  String getName() {
    return localizator.getLocalized(name);
  }
  
  Object getReferencedObject() {
    return referencedObject;
  }

  public Object clone() throws CloneNotSupportedException {
    ReferenceHandler result = (ReferenceHandler)super.clone();
    
    result.referencedObject = (AttributeDefinitionHandler)referencedObject.clone();
    
    return result;
  }
  
  void setLocalizator(Localizator localizator) {
    this.localizator = localizator;
    
    referencedObject.setLocalizator(localizator);
  }

}
