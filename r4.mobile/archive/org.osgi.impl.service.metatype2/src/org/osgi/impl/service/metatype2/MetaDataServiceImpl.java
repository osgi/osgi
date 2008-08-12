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

import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cu.ControlUnitConstants;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;
import org.osgi.service.metatype2.MetaDataListener;
import org.osgi.service.metatype2.MetaDataService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;


/**
 * 
 * @version $Revision$
 */
class MetaDataServiceImpl implements MetaDataService, ServiceTrackerCustomizer {

  private static final Object NULL_KEY = new Object();
  
  private Hashtable categoriesTable;
  private BundleContext bc;
  
  private BundlesTracker bundlesTracker;
  private MetaDataListenersTracker listenersTracker;
  private ServiceTracker providersTracker;
  private Logger logger;
  
  private SAXParserFactory saxFactory;
  
  MetaDataServiceImpl(SAXParserFactory saxFactory) {
    this.saxFactory = saxFactory;
  }
  
  void setSAXFactory(SAXParserFactory saxFactory) {
    this.saxFactory = saxFactory;
  }
  
  SAXParserFactory getSAXFactory() {
    return saxFactory;
  }

  void start(BundleContext bc) {
    this.bc = bc;
    
    logger = new Logger(bc);
    
    listenersTracker = new MetaDataListenersTracker(bc, logger);
    
    providersTracker = new ServiceTracker(bc, MetaTypeProvider.class.getName(), this);
    bundlesTracker = new BundlesTracker(bc, logger, this);

    providersTracker.open();
    listenersTracker.open();
  }

  void stop() {
    bundlesTracker.close();
    providersTracker.close();
    listenersTracker.close();
    
    logger.close();
  }
  
  //******* ServiceTrackerCustomizer interface methods *******//
  
  public Object addingService(ServiceReference reference) {
    String id = getMetaTypeID(reference);
    
    String category = getProperty(reference, METATYPE_CATEGORY);
    String version = getProperty(reference, ControlUnitConstants.VERSION);
    MetaTypeProvider provider = (MetaTypeProvider)bc.getService(reference);

    MetaTypeData data = new MetaTypeData(id, category, version, provider);
    
    addMetaTypeData(data);
    return data;
  }

  public void modifiedService(ServiceReference reference, Object service) {
    MetaTypeData data = (MetaTypeData)service;
    
    String newId = getMetaTypeID(reference);
    String newCategory = getProperty(reference, METATYPE_CATEGORY);
    String newVersion = getProperty(reference, ControlUnitConstants.VERSION);
    
    data.setVersion(newVersion);
    
    if ( data.isSame(newCategory, newId) ) {
      replaceMetaTypeData(data);
    } else {
      removeMetaTypeData(data);
      
      data.setId(newId);
      data.setCategory(newCategory);
      
      addMetaTypeData(data);
    }
  }

  public void removedService(ServiceReference reference, Object service) {
    MetaTypeData data = (MetaTypeData)service;
    removeMetaTypeData(data);
    bc.ungetService(reference);
  }
  
  //******* END. ServiceTrackerCustomizer interface methods *******//
  
  //******* MetaDataService interface methods *******//
  
  public ObjectClassDefinition getObjectClassDefinition(String category,
    String metaTypeID, String ocdID, String locale) throws IllegalArgumentException {
    
    assertNotNull(metaTypeID, "MetaType ID");
    assertNotNull(ocdID, "object class ID");
    
    MetaTypeProvider provider = getMetaTypeProvider(category, metaTypeID);
    
    return provider != null ? provider.getObjectClassDefinition(ocdID, locale) : 
      null;
  }

  public String[] getAvailableMetaTypes(String category) {
    if (categoriesTable == null) {
      return null;
    }
   
    Hashtable metaTypes = (Hashtable)categoriesTable.get( getCategoryKey(category) );
    
    return metaTypes != null ? keysToStringArray(metaTypes) : null;
  }

  public String[] getAvailableCatagories() {
    if (categoriesTable == null) {
      return null;
    }
    
    return keysToStringArray(categoriesTable);
  }

  public String[] getMetaTypeLocales(String category, String id) {
    assertNotNull(id, "MetaType ID");
    
    MetaTypeProvider provider = getMetaTypeProvider(category, id);
    
    return provider != null ? provider.getLocales() : null;
  }

  public String getMetaTypeVersion(String category, String id) {
    assertNotNull(id, "MetaType ID");
    
    MetaTypeData data = getMetaTypeData(category, id);
    
    return data != null ? data.getVersion() : null;
  }

  //******* END MetaDataService interface methods *******//

  void addMetaTypeData(MetaTypeData data) {
    if (categoriesTable == null) {
      categoriesTable = new Hashtable();
    }
    
    synchronized (categoriesTable) {
      Object categoryKey = getCategoryKey( data.getCategory() );
      Hashtable metaTypes = (Hashtable)categoriesTable.get(categoryKey);
      if (metaTypes == null) {
        metaTypes = new Hashtable();
      } else if ( metaTypes.containsKey(data.getId()) ) {
        logger.logError("There is already MetaType with category '" + 
          data.getCategory() + "' and ID '" + data.getId() + "' provided in the framework! The new MetaType would be ignored. ");
        
        return;
      }
      
      metaTypes.put(data.getId(), data);
      categoriesTable.put(categoryKey, metaTypes);
    }
    
    listenersTracker.notifyListeners(data.getCategory(), data.getId(), 
        MetaDataListener.ADDED);
  }
  
  void replaceMetaTypeData(MetaTypeData data)  {
    synchronized (categoriesTable) {
      Object categoryKey = getCategoryKey( data.getCategory() );
      
      Hashtable metaTypes = (Hashtable)categoriesTable.get(categoryKey);
      metaTypes.put(data.getId(), data);
    }
    
    listenersTracker.notifyListeners(data.getCategory(), data.getId(),
      MetaDataListener.MODIFIED);
  }
  
  void removeMetaTypeData(MetaTypeData data) {
    synchronized (categoriesTable) {
      Object categoryKey = getCategoryKey( data.getCategory() );
      
      Hashtable metaTypes = (Hashtable)categoriesTable.get(categoryKey);
      if (metaTypes != null) {
        metaTypes.remove( data.getId() );
        
        if (metaTypes.size() == 0) {
          categoriesTable.remove(categoryKey);
        }
      }
    }
    
    listenersTracker.notifyListeners(data.getCategory(), data.getId(), 
      MetaDataListener.REMOVED);
  }
  
  private MetaTypeProvider getMetaTypeProvider(String category, String id) {
    MetaTypeData data = getMetaTypeData(category, id);
    
    return data != null ? data.getProvider() : null;
  }
  
  private MetaTypeData getMetaTypeData(String category, String id) {
    if (categoriesTable == null) {
      return null;
    }
    
    Hashtable metaTypes = (Hashtable)categoriesTable.get( getCategoryKey(category) );
    if (metaTypes == null) {
      return null;
    }
    
    return (MetaTypeData)metaTypes.get(id);
  }
  
  private Object getCategoryKey(String category) {
    return category != null ? category : NULL_KEY;  
  }
  
  private String[] keysToStringArray(Hashtable table) {
    synchronized (table) {
      int categoriesCount = table.containsKey(NULL_KEY) ? 
        table.size() - 1 : table.size();
      
      if (categoriesCount == 0) {
        return null;
      }
      
      String[] result = new String [categoriesCount];
      
      Enumeration keys = table.keys();
      int counter = 0;
      while ( keys.hasMoreElements() ) {
        Object key = keys.nextElement();
        
        if (key != NULL_KEY) {
          result [counter++] = key.toString();
        }
      }
  
      return result;
    }
  }
  
  private void assertNotNull(Object argument, String name) {
    if (argument == null) {
      throw new NullPointerException(name + " can not be null!");
    }
  }
  
  private String getMetaTypeID(ServiceReference reference) {
    String pid = getProperty(reference, Constants.SERVICE_PID);
    
    return pid != null ? pid : reference.getProperty(Constants.SERVICE_ID).toString();
  }
  
  private String getProperty(ServiceReference reference, String name) {
    Object property = reference.getProperty(name);
    
    if (property == null) {
      return null;
    }  
    
    if ( property instanceof String) {
      return (String)property;
    } 
    
    logger.logError("Property '" + name + "' is not String!", reference);
    
    return null;
  }
  
}
