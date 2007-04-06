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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.BundleContext;

import org.osgi.service.cu.ControlUnit;
import org.osgi.service.cu.ControlUnitException;
import org.osgi.service.cu.admin.ControlUnitAdmin;
import org.osgi.service.cu.admin.ControlUnitAdminException;
import org.osgi.service.cu.admin.ControlUnitAdminListener;
import org.osgi.service.cu.admin.spi.CUAdminCallback;
import org.osgi.service.cu.admin.spi.ControlUnitFactory;

/**
 * This class implements the 
 * {@link org.osgi.service.cu.spi.ControlUnitAdmin ControlUnitAdmin} service.
 * 
 * @version $Revision$
 */
class ControlUnitAdminImpl implements ControlUnitAdmin, CUAdminCallback {
  
  private FactoryTracker cuFactoryTracker;
  private CUTracker managedCUTracker;
  
  private CUListenersTracker cuListenersTracker;
  private SVListenersTracker svListenersTracker;
  private HierarchyListenersTracker hierarchyListenersTracker;
  
  private Hashtable providers;
  
  private NotifyListenersThread notifyAsyncListenersThread;
  
  /**
   * Constructor.
   * 
   * @param bc {@link BundleContext BundleContext} of the ControlUnitAdmin bundle
   */
  ControlUnitAdminImpl(BundleContext bc) {
    providers = new Hashtable();
    cuFactoryTracker = new FactoryTracker(bc, providers);
    managedCUTracker = new CUTracker(bc, providers);
    
    cuListenersTracker = new CUListenersTracker(bc);
    svListenersTracker = new SVListenersTracker(bc, providers);
    hierarchyListenersTracker = new HierarchyListenersTracker(bc);
    
    notifyAsyncListenersThread = new NotifyListenersThread(
      cuListenersTracker, svListenersTracker, hierarchyListenersTracker);    
  }
  
  /**
   * Start the ControlUnitAdmin service. 
   * 
   * This method is invoked before the service is registered in the framework.
   */
  void initiate() {
    cuListenersTracker.initiate();    
    svListenersTracker.initiate();
    hierarchyListenersTracker.initiate();
    
    notifyAsyncListenersThread.initiate();
    
    cuFactoryTracker.initiate(this);
    managedCUTracker.initiate(this);
  }
  
  /**
   * Stop the ControlUnitAdmin service. 
   * 
   * This method is invoked after the service is unregistered.
   */
  void finish() {
    managedCUTracker.finish();
    cuFactoryTracker.finish();
    
    notifyAsyncListenersThread.finish();
    
    hierarchyListenersTracker.finish();
    svListenersTracker.finish();
    cuListenersTracker.finish();
  }
  
  //******* CUAdminCallback interface implementation *******//
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.ControlUnitListener#controlUnitEvent(int, java.lang.String, java.lang.String)
   */
  public void controlUnitEvent(int eventType, String cuType, String cuID) {
    chekIsValidCUEventType(eventType);
    checkArgument(cuType, "control unit type");
    
    if (cuID == null && eventType != CONTROL_UNIT_TYPE_ADDED
      && eventType != CONTROL_UNIT_TYPE_REMOVED) {
      throw new NullPointerException("No control unit ID specified!");
    }
    
    notifyAsyncListenersThread.addCUEvent(eventType, cuType, cuID);
    
    cuListenersTracker.notifySyncListeners(eventType, cuType, cuID);
    
    if (eventType == ControlUnitAdminListener.CONTROL_UNIT_ADDED) {
      svListenersTracker.controlUnitAdded(cuType, cuID);
    } 
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.StateVariableListener#stateVariableChanged(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
   */
  public void stateVariableChanged(String cuType, String cuID, String varID, Object value) {
    checkArgument(cuType, "control unit type");
    checkArgument(cuID, "control unit ID");
    checkArgument(varID, "state variable ID");
    
    notifyAsyncListenersThread.addSVEvent(cuType, cuID, varID, value);
    
    svListenersTracker.notifySyncListeners(cuType, cuID, varID, value);    
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.HierarchyListener#hierarchyChanged(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  public void hierarchyChanged(int eventType, String cuType, String cuID, String parentType, String parentID) {
    checkIsValidHierarchyEventType(eventType);
    checkArgument(cuType, "child control unit type");
    checkArgument(cuID, "child control unit id");
    checkArgument(parentType, "parent control unit type");
    checkArgument(parentID, "parent control unit id");
    
    notifyAsyncListenersThread.addHierachyEvent(eventType, cuType, cuID, parentType, parentID);
    
    hierarchyListenersTracker.notifySyncListeners(eventType, cuType, cuID, parentType, parentID);
  }
  
  //******* End.CUAdminCallback interface implementation *******//
  
  //******* ControlUnitAdmin interface implementation *******//
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.ControlUnitAdmin#getControlUnitTypes()
   */
  public String[] getControlUnitTypes() {
    synchronized (providers) {
      if (providers.size() == 0) {
        return null;
      }
      
      String[] result = new String [providers.size()];
      Enumeration types = providers.keys();
      int index = 0;
      while ( types.hasMoreElements() ) {
        result [index++] = (String)types.nextElement();
      }
      
      return result;
    }
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.ControlUnitAdmin#getControlUnitTypeVersion(java.lang.String)
   */
  public String getControlUnitTypeVersion(String cuType) throws ControlUnitAdminException {
    checkArgument(cuType, "control unit type");
    
    Provider provider = (Provider)providers.get(cuType);
    
    if (provider == null) {
      throw new ControlUnitAdminException(ControlUnitAdminException.NO_SUCH_CONTROL_UNIT_TYPE_ERROR);
    }
    
    return provider.getTypeVersion();
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.ControlUnitAdmin#findControlUnits(java.lang.String, java.lang.String, java.lang.Object)
   */
  public String[] findControlUnits(String cuType, String finderID, Object arguments) throws ControlUnitException {
    checkArgument(cuType, "control unit type");
    
    ControlUnitFactory provider = (ControlUnitFactory)providers.get(cuType);
    
    if (provider == null) {
      return null;
    }
    
    if (finderID == null && arguments == null) {
      return provider.listControlUnits();
    }
      
    return provider.findControlUnits(finderID, arguments);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.ControlUnitAdmin#getControlUnit(java.lang.String, java.lang.String)
   */
  public ControlUnit getControlUnit(String cuType, String cuID) {
    checkArgument(cuType, "control unit type");
    checkArgument(cuID, "control unit ID");
    
    ControlUnitFactory provider = (ControlUnitFactory)providers.get(cuType);
    
    return provider != null ? provider.getControlUnit(cuID) : null;
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.ControlUnitAdmin#createControlUnit(java.lang.String, java.lang.String, java.lang.Object)
   */
  public String createControlUnit(String cuType, String constructorID, Object arguments) throws ControlUnitException {
    checkArgument(cuType, "control unit type");
    
    ControlUnitFactory provider = (ControlUnitFactory)providers.get(cuType);
    
    if (provider == null) {
      throw new ControlUnitAdminException(ControlUnitAdminException.NO_SUCH_CONTROL_UNIT_TYPE_ERROR);
    }
     
    return provider.createControlUnit(constructorID, arguments);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.ControlUnitAdmin#destroyControlUnit(java.lang.String, java.lang.String)
   */
  public void destroyControlUnit(String cuType, String cuID) throws ControlUnitException {
    checkArgument(cuType, "control unit type");
    checkArgument(cuID, "control unit ID");
    
    ControlUnitFactory provider = (ControlUnitFactory)providers.get(cuType);
    
    if (provider == null) {
      throw new ControlUnitAdminException(ControlUnitAdminException.NO_SUCH_CONTROL_UNIT_TYPE_ERROR);
    }
    
    provider.destroyControlUnit(cuID);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.ControlUnitAdmin#getSubControlUnitTypes(java.lang.String)
   */
  public String[] getSubControlUnitTypes(String parentCUType) {
    checkArgument(parentCUType, "parent control unit type");
    
    Vector result = null;
    synchronized (providers) {
      result = new Vector( providers.size() );
      Enumeration types = providers.keys();
      while ( types.hasMoreElements() ) {
        String type = (String)types.nextElement();
        
        Provider provider = (Provider)providers.get(type);
        if ( provider.hasParentOfType(parentCUType) ) {
          result.addElement(type);
        }
      }
    } // synchronized
    
    if (result.size() > 0) {
      String[] typesArray = new String [result.size()];
      result.copyInto(typesArray);
      
      return typesArray;
    }
    
    return null;
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.ControlUnitAdmin#getSubControlUnits(java.lang.String, java.lang.String, java.lang.String)
   */
  public String[] getSubControlUnits(String parentCUType, String parentCUID, String subCUType) {
    checkArgument(subCUType, "childs' control units type");
    
    ControlUnitFactory provider = (ControlUnitFactory)providers.get(subCUType);
    
    return provider != null ? provider.getControlUnits(parentCUType, parentCUID) : null;
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.ControlUnitAdmin#getParentControlUnitTypes(java.lang.String)
   */
  public String[] getParentControlUnitTypes(String subCUType) {
    checkArgument(subCUType, "control unit type");
    
    Provider provider = (Provider)providers.get(subCUType);
    
    return provider != null ? provider.getParentTypes() : null;
  }

  /* (non-Javadoc)
   * @see org.osgi.service.cu.ControlUnitAdmin#getParentControlUnits(java.lang.String, java.lang.String, java.lang.String)
   */
  public String[] getParentControlUnits(String subCUType, String subCUID, String parentType) throws ControlUnitAdminException {
    checkArgument(subCUType, "child's control unit type");
    checkArgument(subCUID, "child's control unit ID");
    checkArgument(parentType, "parents' control units type");
    
    ControlUnitFactory provider = (ControlUnitFactory)providers.get(subCUType);
    
    return provider != null ? provider.getParents(subCUID, parentType) : null;
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.ControlUnitAdmin#queryStateVariable(java.lang.String, java.lang.String, java.lang.String)
   */
  public Object queryStateVariable(String cuType, String cuID, String varID) throws ControlUnitException {
    checkArgument(cuType, "control unit type");
    checkArgument(cuID, "control unit ID");
    checkArgument(varID, "state variable ID");
    
    ControlUnitFactory provider = (ControlUnitFactory)providers.get(cuType);
    
    if (provider == null) {
      throw new ControlUnitAdminException(ControlUnitAdminException.NO_SUCH_CONTROL_UNIT_TYPE_ERROR);
    }
    
    return provider.queryStateVariable(cuID, varID);
  }

  /* (non-Javadoc)
   * @see org.osgi.service.cu.ControlUnitAdmin#invokeAction(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
   */
  public Object invokeAction(String cuType, String cuID, String actionID, Object arguments) throws ControlUnitException {
    checkArgument(cuType, "control unit type");
    checkArgument(cuID, "control unit ID");
    checkArgument(actionID, "action ID");
    
    ControlUnitFactory provider = (ControlUnitFactory)providers.get(cuType);
    
    if (provider == null) {
      throw new ControlUnitAdminException(ControlUnitAdminException.NO_SUCH_CONTROL_UNIT_TYPE_ERROR);
    }
    
    return provider.invokeAction(cuID, actionID, arguments);
  }

  //******* End. ControlUnitAdmin interface implementation *******//
  
  private static final void checkIsValidHierarchyEventType(int eventType) {
    if (eventType != ATTACHED && eventType != DETACHED) {
      throw new IllegalArgumentException("Invalid hierarchy change event type : " + 
        eventType + "!");
    }
  }
  
  private static final void chekIsValidCUEventType(int eventType) {
    if (eventType != CONTROL_UNIT_ADDED && eventType != CONTROL_UNIT_REMOVED &&
      eventType != CONTROL_UNIT_TYPE_ADDED && eventType != CONTROL_UNIT_TYPE_REMOVED) {
      throw new IllegalArgumentException("Invalid control unit event type : " + eventType + "!");
    }
  }
  
  private static final void checkArgument(Object argument, String argumentName) {
    if (argument == null) {
      throw new NullPointerException("No " + argumentName + " provided!");
    }
  }
  
}