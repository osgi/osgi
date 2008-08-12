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

import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.metatype.MetaTypeProvider;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


/**
 * 
 * @version $Revision$
 */
class BundlesTracker implements SynchronousBundleListener {
  
  private static final String METATYPE_HEADER = "Meta-Type";
  
  private static final String XML_KEY = "xml";
  private static final String CATAGORY_KEY = "category";
  private static final String VERSION_KEY = "version";
  private static final String ID_KEY = "id";
  
  private Hashtable bundlesTable;
  private Logger logger;
  private BundleContext bc;
  private MetaDataServiceImpl metaDataService;
  
  BundlesTracker(BundleContext bc, Logger logger, MetaDataServiceImpl metaDataService) {
    this.logger = logger; 
    this.bc = bc;
    this.metaDataService = metaDataService;
    
    bc.addBundleListener(this);
    
    Bundle[] bundles = bc.getBundles();
    for (int i = 0; i < bundles.length; i++) {
      MetaTypeData[] metaTypes = getMetaTypes(bundles [i]);
      addMetaTypes(bundles [i], metaTypes);
    }
  }
  
  void close() {
    bc.removeBundleListener(this);
  }
  
  //******* BundleListener interface methods *******//
  
  /* (non-Javadoc)
   * @see org.osgi.framework.BundleListener#bundleChanged(org.osgi.framework.BundleEvent)
   */
  public void bundleChanged(BundleEvent event) {
    Bundle bundle = event.getBundle();
    
    switch ( event.getType() ) {
      case BundleEvent.INSTALLED : {
        MetaTypeData[] metaTypes = getMetaTypes(bundle);
        addMetaTypes(bundle, metaTypes);
        break;
      }
      
      case BundleEvent.UPDATED : {
        MetaTypeData[] newMetaTypes = getMetaTypes(bundle);
        updatedMetaTypes(bundle, newMetaTypes);
        break;
      }
      
      case BundleEvent.UNINSTALLED : {
        removeMetaTypes(bundle);
        break;
      }
    }

  }
 
  private void updatedMetaTypes(Bundle bundle, MetaTypeData[] newMetaTypes) {
    MetaTypeData[] oldMetaTypes = bundlesTable != null ? (MetaTypeData[]) bundlesTable.get(bundle) : null;
    
    if (newMetaTypes == null) {
      if (oldMetaTypes != null) {
        removeMetaTypes(bundle);
      }
      return;
    }
    
    // newMetaTypes != null
    if (oldMetaTypes == null) {
      addMetaTypes(bundle, newMetaTypes);
      return;
    }
    
    // both newMetaTypes and oldMetaTypes not-null
    for (int i = 0; i < oldMetaTypes.length; i++) {
      MetaTypeData data = oldMetaTypes [i];
      if ( isInArray(newMetaTypes, data) ) {
        metaDataService.replaceMetaTypeData(data);
      } else {
        metaDataService.removeMetaTypeData(data);
      }
    }
    
    for (int i = 0; i < newMetaTypes.length; i++) {
      MetaTypeData data = newMetaTypes [i];
      if ( !isInArray(oldMetaTypes, data) ) {
        metaDataService.addMetaTypeData(data);
      }
    }

    bundlesTable.put(bundle, newMetaTypes);
  }
  
  private boolean isInArray(MetaTypeData[] metaTypes, MetaTypeData data) {
    for (int i = 0; i < metaTypes.length; i++) {
      if ( metaTypes [i].isSame(data.getCategory(), data.getId()) ) {
        return true;
      }
    }
    
    return false;
  }

  private void addMetaTypes(Bundle bundle, MetaTypeData[] metaTypes) {
    if (metaTypes == null) {
      return;
    }
    
    if (bundlesTable == null) {
      bundlesTable = new Hashtable();
    }
    
    bundlesTable.put(bundle, metaTypes);

    for (int i = 0; i < metaTypes.length; i++) {
      metaDataService.addMetaTypeData(metaTypes [i]);
    }
  }
  
  private void removeMetaTypes(Bundle bundle) {
    if (bundlesTable == null) {
      return;
    }
    
    MetaTypeData[] metaTypes = (MetaTypeData[]) bundlesTable.remove(bundle);
    if (metaTypes != null) {
      for (int i = 0; i < metaTypes.length; i++) {
        metaDataService.removeMetaTypeData(metaTypes [i]);
      }
    }
  }

  //******* END BundleListener interface methods *******//
  
  private MetaTypeData[] getMetaTypes(Bundle bundle) {
    Dictionary headers = bundle.getHeaders();
    String metaTypeHeader = headers != null ? (String)headers.get(METATYPE_HEADER) : null;
    
    if (metaTypeHeader == null) {
      return null;
    }
    
    StringTokenizer tokenizer = new StringTokenizer(metaTypeHeader, ",");
    Vector metaTypeDatas = new Vector(2);
    
    while ( tokenizer.hasMoreTokens() ) {
      String token = tokenizer.nextToken();
      
      MetaTypeData data = parseXMLDeclaration(bundle, token);
      if (data != null) {
        metaTypeDatas.addElement(data);
      }
    }
    
    MetaTypeData[] result = new MetaTypeData [metaTypeDatas.size()];
    metaTypeDatas.copyInto(result);
    
    return result;
  }
  
  private MetaTypeData parseXMLDeclaration(Bundle bundle, String declaration) {
    StringTokenizer tokenizer = new StringTokenizer(declaration, ";");

    URL xmlURL = null;
    String metaTypeID = null;
    String metaTypeCategory = null;
    String metaTypeVersion = null;
    while ( tokenizer.hasMoreTokens() ) {
      String token = tokenizer.nextToken();
      int indexOfEqualSign = token.indexOf('=');
      
      if (indexOfEqualSign == -1 || indexOfEqualSign >= token.length() - 1) {
        logger.logError("Invalid MetaType header '" + declaration + "'!");
        return null;
      }
      
      String key = token.substring(0, indexOfEqualSign).trim();
      String value = token.substring(indexOfEqualSign + 1).trim();
      
      if ( key.equalsIgnoreCase(XML_KEY) ) {
        xmlURL = getXMLResource(bundle, value);
      } else if ( key.equalsIgnoreCase(ID_KEY) ) {
        metaTypeID = value;
      } else if ( key.equalsIgnoreCase(CATAGORY_KEY) ) {
        metaTypeCategory = value;
      } else if ( key.equalsIgnoreCase(VERSION_KEY) ) {
        metaTypeVersion = value;
      } else {
        logger.logDebug("Unknown key '" + key + 
            "' met in MetaType header '" + declaration + "'!");
      }
    }
    
    if (xmlURL == null) {
      logger.logError("Invalid MetaType header '" + declaration + 
          "' : no or illegal XML file location specified!");
      return null;
    }
    
    if (metaTypeID == null) {
      logger.logError("Invalid MetaType header '" + declaration + "' : no MeatType ID specified!");
      return null;
    }
    
    MetaTypeProvider provider = parseXML(xmlURL, metaTypeID, bundle);
    
    return provider != null ? 
      new MetaTypeData(metaTypeID, metaTypeCategory, metaTypeVersion, provider) : null;
  }

  private MetaTypeProvider parseXML(URL xmlURL, String metaTypeID, Bundle bundle) {
    SAXParserFactory factory = metaDataService.getSAXFactory();
    factory.setValidating(false);

    try {
      SAXParser parser = factory.newSAXParser();
      XMLReader reader = parser.getXMLReader();
    
      InputStream in = xmlURL.openStream();
      InputSource source = new InputSource(in);
      MetaTypeParser metaTypeParser = new MetaTypeParser(logger, reader, bundle);
      reader.parse(source);
      
      return metaTypeParser.getMetaTypeProvider();
    } catch (Throwable e) {
      logger.logError("MetaType '" + metaTypeID + "' (" + xmlURL + 
        ") can not be  parsed!", e);
      
      return null;
    }
  }
  
  private URL getXMLResource(Bundle bundle, String xmlPath) {
    try {
      return bundle.getResource(xmlPath);
    } catch (Throwable ex) {
      logger.logError("Failed to load MetaType XML!", ex);
    }
    
    return null;
  }
}
