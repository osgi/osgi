/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2000, 2004). All Rights Reserved.
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
package org.osgi.impl.service.cu.admin;

import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cu.ControlUnitConstants;
import org.osgi.service.cu.admin.ControlUnitAdminListener;
import org.osgi.service.cu.admin.spi.ControlUnitFactory;

/**
 * This class is used to track the registered in the framework 
 * {@link org.osgi.service.cu.spi.ControlUnitFactory ControlUnitFactory}s.
 * 
 * For every registered <code>ControlUnitFactory</code> this class creates
 * a {@link org.osgi.impl.service.cu.admin.FactoryCUProvider} and
 * inserts it in the supplied upon creation 
 * {@link org.osgi.impl.service.cu.admin.Provider}s table under key equal
 * to the type of the factory.
 * 
 * @version $Revision$
 */
class FactoryTracker extends ManagedObjectsTracker {

  private Hashtable providers;
  
  /**
   * Constructor.
   * 
   * @param bc {@link BundleContext} of the ControlUnitAdmin bundle
   * @param cuProviders {@link org.osgi.impl.service.cu.admin.Provider}s table
   */
  FactoryTracker(BundleContext bc, Hashtable providers) {
    super(bc, new Class[] {ControlUnitFactory.class});
    
    this.providers = providers;
  }
  
  //******* ManagedObjectsTracker methods implementations *******//
  
  /* (non-Javadoc)
   * @see org.osgi.impl.service.cu.ManagedObjectsTracker#onManagedObjectFound(org.osgi.framework.ServiceReference, java.lang.String, boolean)
   */
  protected boolean onManagedObjectFound(ServiceReference reference, String type, boolean isRegistering) {
    ControlUnitFactory factory = (ControlUnitFactory)bc.getService(reference);
    
    if (factory == null) {
      return false;
    }
    
    synchronized (providers) {
      if ( providers.containsKey(type) ) {
        logError("Factory for type '" + type + "' registered, but there is already provider for this type of control units!");
        return false;
      }
      
      factory.setControlUnitCallback(cuAdminCallback);
      
      FactoryCUProvider provider = new FactoryCUProvider(factory, getParentTypes(reference), 
        getVersion(reference));
      
      providers.put(type, provider);
    }
    
    if (isRegistering) {
      cuAdminCallback.controlUnitEvent(ControlUnitAdminListener.CONTROL_UNIT_TYPE_ADDED, 
        type, null);
    }
    
    return true;
  }
  
  /* (non-Javadoc)
   * @see org.osgi.impl.service.cu.ManagedObjectsTracker#onManagedObjectModified(org.osgi.framework.ServiceReference, java.lang.String)
   */
  protected void onManagedObjectModified(ServiceReference reference, String type) {}
  
  /* (non-Javadoc)
   * @see org.osgi.impl.service.cu.ManagedObjectsTracker#onReleasingManagedObject(org.osgi.framework.ServiceReference, java.lang.String, boolean)
   */
  protected void onReleasingManagedObject(ServiceReference reference, String type, boolean isUnregistering) {
    synchronized (providers) {
      ControlUnitFactory factory = (ControlUnitFactory)providers.get(type);
      
      factory.setControlUnitCallback(null);
      
      bc.ungetService(reference);
      
      providers.remove(type);
    }
    
    if (isUnregistering) {
      cuAdminCallback.controlUnitEvent(ControlUnitAdminListener.CONTROL_UNIT_TYPE_REMOVED, 
          type, null);
    }
  }
  
  //******* End. ManagedObjectsTracker methods implementations *******//
  
  private String[] getParentTypes(ServiceReference reference) {
    Object parentTypeProperty = reference.getProperty(ControlUnitConstants.PARENT_TYPE);
    if (parentTypeProperty == null) {
      return null;
    }
    
    if (parentTypeProperty instanceof String) {
      return new String[] { (String)parentTypeProperty };
    }
    
    if ( parentTypeProperty instanceof String[] ) {
      return (String [])parentTypeProperty;
    }
    
    logError("CU Registration Property '" + ControlUnitConstants.PARENT_TYPE + "' is '" + 
      parentTypeProperty.getClass().getName() + "'!");
      
    return null;
  }
  
}