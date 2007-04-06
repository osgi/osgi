/*
 * $Id$
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
import org.osgi.service.cu.ControlUnit;
import org.osgi.service.cu.ControlUnitConstants;
import org.osgi.service.cu.admin.ControlUnitAdminListener;
import org.osgi.service.cu.admin.HierarchyListener;
import org.osgi.service.cu.admin.spi.ManagedControlUnit;

/**
 * This class is used to track the registered in the framework
 * {@link org.osgi.service.cu.ControlUnit} and
 * {@link org.osgi.service.cu.spi.ManagedControlUnit}s. 
 * 
 * When a Control Unit is the first registered from given 
 * type it creates a {@link CUTracker} for this type
 * and inserts it in the supplied upon creation 
 * {@link org.osgi.impl.service.cu.admin.Provider}s table under key equal
 * to the type of the control unit. <BR>
 * Any subsequent registered unit from the same type is added to the same 
 * provider. <BR>
 * When a Control Unit is unregistered from the framework
 * it is removed from the corresponding provider and if it is the last one of
 * the given type the provider itself is removed from the providers' table.
 *   
 * @version $Revision$
 */
class CUTracker extends ManagedObjectsTracker {
  
  private Hashtable providers;

  /**
   * @param bc {@link BundleContext BundleContext} of the ControlUnitAdmin bundle
   * @param cuProviders {@link org.osgi.impl.service.cu.admin.Provider CUProvider}s table
   */
  CUTracker(BundleContext bc, Hashtable providers) {
    super(bc, new Class[] {ControlUnit.class, ManagedControlUnit.class});
    
    this.providers = providers;
  }
  
  
  /* (non-Javadoc)
   * @see org.osgi.impl.service.cu.ManagedObjectsTracker#onManagedObjectFound(org.osgi.framework.ServiceReference, java.lang.String, boolean)
   */
  protected boolean onManagedObjectFound(ServiceReference reference, String type, boolean isRegistering) {
    
    String cuID = null;
    boolean isFirstUnit = false;
    String parentType = null;
    String parentID = null;
    synchronized (providers) {
      Object curProvider = providers.get(type);
      
      if ( curProvider != null && !(curProvider instanceof CUProvider) ) {
        logError("Control Unit of type '" + type + "' registered, but there is already a factory registered for this type!");
        return false;
      }
      
      cuID = getID(reference);
      if (cuID == null) {
        return false;
      }
      
      ControlUnit cu = (ControlUnit)bc.getService(reference);
  
      if (cu == null) {
        return false;
      }
      
      String typeVersion = getVersion(reference);
      CUProvider cuProvider = null;
      if (curProvider == null) {
        cuProvider = new CUProvider(cuAdminCallback, typeVersion);
        
        providers.put(type, cuProvider);
      } else {
        cuProvider = (CUProvider)curProvider; 
      }
      
      isFirstUnit = !cuProvider.hasUnits();
      
      parentType = getParentType(reference);
      parentID = getParentID(reference);
      cuProvider.addControlUnit(cuID, cu, parentType, parentID, typeVersion);      
    } //synchronized
            
    if (isRegistering) {
      cuAdminCallback.controlUnitEvent( ControlUnitAdminListener.CONTROL_UNIT_ADDED, type, cuID);
          
      if (isFirstUnit) {
        cuAdminCallback.controlUnitEvent( ControlUnitAdminListener.CONTROL_UNIT_TYPE_ADDED, type, null);
      }
      
      if (parentType != null && parentID != null) {
        cuAdminCallback.hierarchyChanged(HierarchyListener.ATTACHED, type, cuID, 
          parentType, parentID);
      }
    }
    
    return true;
  }
  
  /* (non-Javadoc)
   * @see org.osgi.impl.service.cu.ManagedObjectsTracker#onManagedObjectModified(org.osgi.framework.ServiceReference, java.lang.String)
   */
  protected void onManagedObjectModified(ServiceReference reference, String type) {
    synchronized (providers) {
      Object curProvider = providers.get(type);
      
      if ( !(curProvider instanceof CUProvider) ) {
        return;
      }
      
      CUProvider cuProvider = (CUProvider)curProvider;
      
      String cuID = getID(reference);
      CUData cuData = cuProvider.getControlUnitData(cuID);
      if (cuData == null) {
        logError("Control Unit '" + cuID + "' of type '" + type + "' properties changed, but the unit was not found!");
        return;
      }
      
      String newParentType = getParentType(reference);
      String newParentID = getParentID(reference);
      
      if ( !cuData.hasParent(newParentType, newParentID) ) {
        String oldParentType = cuData.getParentType();
        String oldParentID = cuData.getParentID();
        
        cuProvider.changeParent(cuID, newParentType, newParentID);
        
        if (oldParentType != null && oldParentID != null) {
          cuAdminCallback.hierarchyChanged(HierarchyListener.DETACHED, type, cuID, 
            oldParentType, oldParentID);
        }
          
        if (newParentType != null && newParentID != null) {
          cuAdminCallback.hierarchyChanged(HierarchyListener.ATTACHED, type, cuID,
            newParentType, newParentID);
        }
      }
    }
  }
  
  /* (non-Javadoc)
   * @see org.osgi.impl.service.cu.ManagedObjectsTracker#onReleasingManagedObject(org.osgi.framework.ServiceReference, java.lang.String, boolean)
   */
  protected void onReleasingManagedObject(ServiceReference reference, String type, boolean isUnregistering) {
    String cuID = null;
    boolean isLastUnit = false;
    CUData cuData = null;
    synchronized (providers) {
      Object curProvider = providers.get(type);
      
      if ( !(curProvider instanceof CUProvider) ) {
        return;
      }
      
      CUProvider cuProvider = (CUProvider)curProvider;
      
      bc.ungetService(reference);
      
      cuID = getID(reference);
      cuData = cuProvider.removeControlUnit(cuID);
      
      isLastUnit = !cuProvider.hasUnits();
      if (isLastUnit) {
        providers.remove(type);
      }
    } // synchronized
      
    if (isUnregistering) {
      cuAdminCallback.controlUnitEvent( ControlUnitAdminListener.CONTROL_UNIT_REMOVED, type, cuID);  
        
      if (isLastUnit) {
        cuAdminCallback.controlUnitEvent(ControlUnitAdminListener.CONTROL_UNIT_TYPE_REMOVED, type, null);
      }
      
      if (cuData.hasParent()) {
        cuAdminCallback.hierarchyChanged(HierarchyListener.DETACHED, type, cuID, 
          cuData.getParentType(), cuData.getParentID());
      }
    }      
  }

  /******* End. ServiceTrackerCustomizer interface implementation *******/
  
  private String getID(ServiceReference reference) {
    return getProperty(reference, ControlUnitConstants.ID, true);
  }
  
  private String getParentType(ServiceReference reference) {
    return getProperty(reference, ControlUnitConstants.PARENT_TYPE, false);
  }
  
  private String getParentID(ServiceReference reference) {
    return getProperty(reference, ControlUnitConstants.PARENT_ID, false);
  }
  
}