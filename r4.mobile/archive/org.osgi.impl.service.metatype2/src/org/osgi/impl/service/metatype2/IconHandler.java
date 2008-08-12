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
import java.net.URL;

import org.osgi.framework.Bundle;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 * 
 * @version $Revision$
 */
class IconHandler extends AbstractHandler {

  static final String ICON_TAG = "Icon";
  
  private static final String RESOURCE_ATTRIBUTE = "resource";
  private static final String SIZE_ATTRIBUTE = "size";
   
  private String iconPath;
  private int size;

  private Bundle bundle;
  
  public IconHandler(XMLReader xmlReader, DefaultHandler parentHandler, 
    Attributes attributes, Bundle bundle) throws SAXException {
    
    super(ICON_TAG, xmlReader, parentHandler);
    
    this.bundle = bundle;
    iconPath = getRequiredAttribute(attributes, RESOURCE_ATTRIBUTE);

    String sizeString = getRequiredAttribute(attributes, SIZE_ATTRIBUTE);
    size = parseIntAttribute(SIZE_ATTRIBUTE, sizeString);
  }

  InputStream getIcon(Localizator localizator) throws IOException {
    String realPath = localizator.getLocalized(iconPath);
    
    URL iconURL = bundle.getResource(realPath);
    
    return iconURL.openStream();
  }
  
  int getSize() {
    return size;
  }

}
