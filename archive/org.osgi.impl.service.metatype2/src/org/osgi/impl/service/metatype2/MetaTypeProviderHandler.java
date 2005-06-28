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

import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 * 
 * @version $Revision$
 */
public class MetaTypeProviderHandler extends AbstractHandler implements MetaTypeProvider {

  static final String META_DATA_TAG = "ExtendedMetaData";
  
  private static final String LOCALE_ATTRIBUTE = "localization";
  private static final String RESOURCE_FILE_EXTENTION = ".properties";
  private static final String LOCALE_SEPARATOR = "_";
  private static final char SLASH = '/';
  
  private String localization;
  private String[] locales;
  private Hashtable attributesTable;
  private Hashtable actionsTable;
  private Hashtable ocdTable;
  private Bundle bundle;

  private Localizator defaultLocalizator;

  private Logger logger;

  public MetaTypeProviderHandler(Logger logger, XMLReader xmlReader, 
    DefaultHandler parentHandler, Attributes attributes, Bundle bundle) {
    
    super(META_DATA_TAG, xmlReader, parentHandler);
    this.logger = logger;
    this.bundle = bundle;
    
    localization = attributes.getValue(LOCALE_ATTRIBUTE);
  }

  /* (non-Javadoc)
   * @see org.osgi.service.metatype.MetaTypeProvider#getObjectClassDefinition(java.lang.String, java.lang.String)
   */
  public ObjectClassDefinition getObjectClassDefinition(String id, String locale) {
    ObjectClassDefinitionHandler ocd = (ObjectClassDefinitionHandler)ocdTable.get(id);
    
    if (ocd == null) {
      throw new IllegalArgumentException("There is no ObjectClassDefinition with id '" + id + "'!");
    }
    
    if (locale == null) {
      if (defaultLocalizator == null) {
        defaultLocalizator = getDefaultLocalizator();
        
        ocd.setLocalizator(defaultLocalizator);
      }
      
      return ocd;
    }
    
    ObjectClassDefinitionHandler result = (ObjectClassDefinitionHandler)ocd.clone();
    
    ResourceBundle resourceBundle = getResourceBundle(locale);
    Localizator localizator = new Localizator(resourceBundle);

    result.setLocalizator(localizator);
    
    return result;
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.metatype.MetaTypeProvider#getLocales()
   */
  public String[] getLocales() {
    return locales;
  }
  
  //******* DefaultHandler interface methods *******//
  
  public void startElement(String namespaceURI, String localName, 
                           String qualifiedName, Attributes attributes) throws SAXException  {
    
    String tag = getElementName(localName, qualifiedName);
    
    if ( AttributeDefinitionHandler.ATTRIBUTE_TAG.equals(tag) ) {
      AttributeDefinitionHandler attribute = new AttributeDefinitionHandler(xmlReader,
        this, attributes, attributesTable, bundle);
      
      storeAttribute(attribute);
    } else if ( ActionHandler.ACTION_TAG.equals(tag) ) {
      ActionHandler action = new ActionHandler(xmlReader, this, attributes, 
        attributesTable, bundle);
      
      storeAction(action);
    } else if ( ObjectClassDefinitionHandler.OCD_TAG.equals(tag) ){
      ObjectClassDefinitionHandler ocd = new ObjectClassDefinitionHandler(xmlReader,
        this, attributes, attributesTable, actionsTable, bundle);
      
      storeObjectClassDefinition(ocd);
    } else {
      super.startElement(namespaceURI, localName, qualifiedName, attributes);
    }
  }

  public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
    if (ocdTable == null) {
      throw new SAXException("There should be at least one " +
        ObjectClassDefinitionHandler.OCD_TAG + " element in " + META_DATA_TAG + "!");
    }

    if (localization == null) {
      localization = Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME;
    }
    
    locales = getLocales(bundle, localization);
    
    attributesTable = null;
    actionsTable = null;
    
    super.endElement(namespaceURI, localName, qualifiedName);
  }

  //******* END DefaultHandler interface methods *******//
  
  private void storeObjectClassDefinition(ObjectClassDefinitionHandler ocd) {
    if (ocdTable == null) {
      ocdTable = new Hashtable();
    }
    
    ocdTable.put(ocd.getID(), ocd);    
  }

  private void storeAction(ActionHandler action) {
    if (actionsTable == null) {
      actionsTable = new Hashtable();
    }
    
    actionsTable.put(action.getID(), action);
  }

  private void storeAttribute(AttributeDefinitionHandler attribute) {
    if (attributesTable == null) {
      attributesTable = new Hashtable();
    }
    
    attributesTable.put(attribute.getID(), attribute);
  }
  
  private static String[] getLocales(Bundle bundle, String localization) {
    if (localization == null) {
      return null;
    }
    
    int indexOfSlash = localization.lastIndexOf(SLASH);
    String baseDirectory = indexOfSlash != -1 ? 
      localization.substring(0, indexOfSlash) : "";
    
    Enumeration paths = bundle.getEntryPaths(baseDirectory);
    if (paths == null) {
      return null;
    }
    
    String baseFileName = localization + LOCALE_SEPARATOR;
    Vector locales = new Vector(5, 2);
    while(paths.hasMoreElements()) {
      String path = (String)paths.nextElement();
      
      if ( path.startsWith(baseFileName) && path.toLowerCase().endsWith(RESOURCE_FILE_EXTENTION) ) {
        String locale = path.substring(baseFileName.length(), 
          path.length() - RESOURCE_FILE_EXTENTION.length() );
        
        locales.addElement(locale);
      }
    }
    
    if (locales.size() > 0) {
      String[] result = new String [locales.size()];
      locales.copyInto(result);
      
      return result;
    }
    
    return null;
  }
  
  private Localizator getDefaultLocalizator() {
    return new Localizator( getResourceBundle(null) );
  }
  
  private ResourceBundle getResourceBundle(String locale) {
    if ( !isLocaleValid(locale) ) {
      throw new IllegalArgumentException("Invalid locale '" + locale + "'!");
    }

    String fileLocation = localization + 
      (locale != null ? LOCALE_SEPARATOR + locale : "") + RESOURCE_FILE_EXTENTION;
    
    URL propertiesURL = bundle.getResource(fileLocation);
    ResourceBundle resourceBundle = null;
    
    try {
      resourceBundle = new PropertyResourceBundle(propertiesURL.openStream());
    } catch (Throwable ex) {
      logger.logError("Localization file '" + fileLocation + "' not found!", ex);
    }
    
    return resourceBundle;
  }
  
  private boolean isLocaleValid(String locale) {
    if (locale == null) {
      return true;
    }

    int firstUnderlineIndex = locale.indexOf(LOCALE_SEPARATOR);
    int secondUnderlineIndex = locale.indexOf(LOCALE_SEPARATOR);
    
    if (firstUnderlineIndex == -1 && locale.length() == 2) {
      return true;
    }
    
    if ( firstUnderlineIndex == 2 && 
         (secondUnderlineIndex == 5 || secondUnderlineIndex == 2) ) {
      return true;
    }
    
    return false;
  }

}
