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

import org.osgi.service.metatype2.ExtendedAttributeDefinition;


/**
 * 
 * @version $Revision$
 */
public class ExAttributeDefinitionImpl implements ExtendedAttributeDefinition, 
  Cloneable {

  protected ReferenceHandler reference;
  
  private AttributeDefinitionHandler adHandler;

  private Localizator localizator;

  public ExAttributeDefinitionImpl(ReferenceHandler reference) {
    this.reference = reference;
    this.adHandler = (AttributeDefinitionHandler)reference.getReferencedObject();
  }

  //******* ExtendedAttributeDefinition interface methods *******//
  
  public Dictionary getProperties() {
    return adHandler.getProperties();
  }

  public ExtendedAttributeDefinition[] getAttributeDefinitions() {
    return adHandler.getAttributeDefinitions();
  }

  public InputStream getIcon(int size) throws IOException {
    return adHandler.getIcon(size);
  }

  public String getID() {
    return reference.getId();
  }
  
  public String getName() {
    return reference.getName();
  }

  public String getDescription() {
    return reference.getDescription();
  }

  public int getCardinality() {
    return adHandler.getCardinality();
  }

  public int getType() {
    return adHandler.getType();
  }

  public String[] getOptionValues() {
    return adHandler.getOptionValues();
  }

  public String[] getOptionLabels() {
    String[] optionLabels = adHandler.getOptionLabels();
    
    if (optionLabels == null) {
      return null;
    }
    
    String[] result = new String [optionLabels.length];
    for (int i = 0; i < optionLabels.length; i++) {
      result [i] = localizator.getLocalized(optionLabels [i]);
    }
    
    return result;
  }

  public String validate(String value) {
    return null;
  }

  public String[] getDefaultValue() {
    return null;
  }

  public Object clone() throws CloneNotSupportedException {
    ExAttributeDefinitionImpl result = (ExAttributeDefinitionImpl)super.clone();
    
    result.reference = (ReferenceHandler) reference.clone();
    
    return result;
  }
  
  void setLocalizator(Localizator localizator) {
    this.localizator = localizator;
    
    reference.setLocalizator(localizator);
  }
  
  //******* END ExtendedAttributeDefinition interface methods *******//
  
}
