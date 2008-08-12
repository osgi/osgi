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


import org.osgi.framework.Bundle;
import org.osgi.service.metatype.MetaTypeProvider;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


/**
 * 
 * @version $Revision$
 */
class MetaTypeParser extends AbstractHandler {
  private MetaTypeProviderHandler provider;
  private Bundle bundle;
  private Logger logger;
  
  MetaTypeParser(Logger logger, XMLReader reader, Bundle bundle) {
    super(null, reader, null);
    this.logger = logger;
    this.bundle = bundle;
  }
  
  MetaTypeProvider getMetaTypeProvider() {
    return provider;
  }
  
  // ******* DefaultHandler overriden methods *******//
  
  public void startElement(String namespaceURI, String localName, 
    String qualifiedName, Attributes attributes) throws SAXException {
   
    String tag = getElementName(localName, qualifiedName);
    
    if ( !MetaTypeProviderHandler.META_DATA_TAG.equals(tag) ) {
      throw new SAXException(UNEXPECTED_ELEMENT_MESSAGE + tag);
    }
    
    provider = new MetaTypeProviderHandler(logger, xmlReader, this, attributes, bundle);
  }
  
  // ******* END DefaultHandler overriden methods *******//

}
