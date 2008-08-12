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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.service.cu.ControlUnit;
import org.osgi.service.cu.ControlUnitException;
import org.osgi.service.cu.admin.ControlUnitAdminException;
import org.osgi.service.cu.admin.spi.CUAdminCallback;
import org.osgi.service.cu.admin.spi.ManagedControlUnit;

/**
 * A {@link org.osgi.impl.service.cu.admin.Provider CUProvider} for control units
 * provided as either
 * {@link org.osgi.service.cu.ControlUnit ControlUnit}s or
 * {@link org.osgi.service.cu.admin.spi.ManagedControlUnit ManagedControlUnit}s
 * in the framework.
 * 
 * @version $Revision$
 */
class CUProvider implements Provider {
  private final Hashtable unitsTable;
  
  private CUAdminCallback adminCallback;
  private final String typeVersion;
  
  /**
   * Constructor.
   * 
   * @param typeVersion version of the control unit's type for which the provider is
   */
  CUProvider(CUAdminCallback adminCallback, String typeVersion) {
    this.adminCallback = adminCallback;
    this.typeVersion = typeVersion;
    
    unitsTable = new Hashtable(5);
  }
  
  /**
   * Check if this provider still provides any control units.
   * 
   * @return true if there are still units provided, false - otherwise
   */
  boolean hasUnits() {
    return !unitsTable.isEmpty();
  }
  
  /**
   * Adds a newly found in the framework control unit to this provider.
   * 
   * @param cuID control unit ID
   * @param cu control unit 
   * @param parentType the type of the parent control unit for the given control 
   * unit, or null if it has no parent
   * @param parentID the ID of the parent control unit for the given control 
   * unit, or null if it has no parent
   * @param typeVersion version of the given control unit's type
   */
  void addControlUnit(String cuID, ControlUnit cu, 
                             String parentType, String parentID, String typeVersion) {
              
    unitsTable.put(cuID, new CUData(cu, parentType, parentID));    
    setControlUnitCallback(cu, adminCallback);
    
    if ( !areVersionsEqual(this.typeVersion, typeVersion) ) {
      System.err.println("Different versions type : '" + this.typeVersion + 
        "' != '" + typeVersion + "'. Offending unit='" + cuID + "'.");      
    }
  }
  
  /**
   * Remove from this provider a control unit which has unregistered from the
   * framework.
   * 
   * @param cuID id of the control unit to be removed
   * @return data of the removed control unit
   */
  CUData removeControlUnit(String cuID) {
    CUData cuData = (CUData)unitsTable.remove(cuID);
    
    ControlUnit cu = cuData.getControlUnit();
    
    setControlUnitCallback(cu, null);
    
    return cuData;
  }
  
  /**
   * Get the data stored for given control unit.
   * 
   * @param cuID id of the control unit
   * @return control unit data
   */
  protected CUData getControlUnitData(String cuID) {
    return (CUData)unitsTable.get(cuID);
  }
  
  /**
   * Should be called when a given control unit has changed its parent.
   * 
   * @param cuID ID of the control unit which has changed its parent
   * @param newParentType new parent's control unit type
   * @param newParentID new parent's control unit ID
   */
  protected void changeParent(String cuID, String newParentType, String newParentID) {
    CUData data = getControlUnitData(cuID);
    
    data.setParent(newParentType, newParentID);
  }
  
  //******* CUProvider methods implementation *******//
  
  /* (non-Javadoc)
   * @see org.osgi.impl.service.cu.CUProvider#hasParentOfType(java.lang.String)
   */
  public boolean hasParentOfType(String parentType) {
    synchronized (unitsTable) {
      Enumeration unitsData = unitsTable.elements();
      
      while ( unitsData.hasMoreElements() ) {
        CUData cuData = (CUData)unitsData.nextElement();
        
        if ( parentType.equals(cuData.getParentType()) ) {
          return true;
        }
      }
    } // synchronized
    
    return false;
  }
  
  /* (non-Javadoc)
   * @see org.osgi.impl.service.cu.CUProvider#getParentTypes()
   */
  public String[] getParentTypes() {
    Vector result = null;
    
    synchronized (unitsTable) {
      Enumeration unitsData = unitsTable.elements();
      
      result = new Vector( unitsTable.size() );
      while ( unitsData.hasMoreElements() ) {
        CUData cuData = (CUData)unitsData.nextElement();
        
        String parentType = cuData.getParentType();
        
        if (parentType != null && !result.contains(parentType)) {
          result.addElement(parentType);
        }
      }
    } // synchronized
    
    return toStringArray(result);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.impl.service.cu.CUProvider#getTypeVersion()
   */
  public String getTypeVersion() {
    return typeVersion;
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#setControlUnitCallback(org.osgi.service.cu.spi.CUAdminCallback)
   */
  public void setControlUnitCallback(CUAdminCallback adminCallback) {
    synchronized (unitsTable) {
      Enumeration unitsData = unitsTable.elements();
      
      while ( unitsData.hasMoreElements() ) {
        ControlUnit cu = retrieveControlUnit( unitsData.nextElement() );
        
        setControlUnitCallback(cu, adminCallback);
      }
    }
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#getControlUnit(java.lang.String)
   */
  public ControlUnit getControlUnit(String cuID) {
    return retrieveControlUnit( unitsTable.get(cuID) );
  }

  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#getControlUnits(java.lang.String, java.lang.String)
   */
  public String[] getControlUnits(String parentType, String parentID) {
    Vector result = null;
    synchronized (unitsTable) {
      Enumeration unitsData = unitsTable.elements();
      
      result = new Vector( unitsTable.size() );
      while ( unitsData.hasMoreElements() ) {
        CUData cuData = (CUData)unitsData.nextElement();
        
        if ( cuData.hasParent(parentType, parentID) ) {
          try {
            result.addElement( cuData.getControlUnit().getId() );
          } catch (Throwable ex) {
            ex.printStackTrace(System.err);
          }
        }
      }
    } // synchronized
    
    return toStringArray(result);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#getParents(java.lang.String, java.lang.String)
   */
  public String[] getParents(String childID, String parentType) throws ControlUnitAdminException {
    CUData cuData = (CUData)unitsTable.get(childID);
    
    if (cuData == null) { 
      throw new ControlUnitAdminException(ControlUnitAdminException.NO_SUCH_CONTROL_UNIT_ERROR);
    }
    
    return cuData.getParentID() != null ? new String[] { cuData.getParentID() } : null;
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#findControlUnits(java.lang.String, java.lang.Object)
   */
  public String[] findControlUnits(String finderID, Object arguments) throws ControlUnitException {
    throw new ControlUnitAdminException(ControlUnitAdminException.SEARCHING_NOT_SUPPORTED_ERROR);
  }
    
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#listControlUnits()
   */
  public String[] listControlUnits() {
    Vector result = null;
    
    synchronized (unitsTable) {
      
      Enumeration unitsData = unitsTable.elements();
      result = new Vector( unitsTable.size() );
      while ( unitsData.hasMoreElements() ) {
        ControlUnit cu = retrieveControlUnit( unitsData.nextElement() ); 
        
        try {
          result.addElement( cu.getId() );
        } catch (Throwable ex) {
          ex.printStackTrace(System.err);
        }
      }
      
    } // synchronized
    
    return toStringArray(result);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#queryStateVariable(java.lang.String, java.lang.String)
   */
  public Object queryStateVariable(String cuID, String varID) throws ControlUnitException {
    ControlUnit cu = getControlUnit(cuID);
    
    if (cu == null) { 
      throw new ControlUnitAdminException(ControlUnitAdminException.NO_SUCH_CONTROL_UNIT_ERROR);
    }
    
    return cu.queryStateVariable(varID);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#invokeAction(java.lang.String, java.lang.String, java.lang.Object)
   */
  public Object invokeAction(String cuID, String actionID, Object arguments) throws ControlUnitException {
    ControlUnit cu = getControlUnit(cuID);
    
    if (cu == null) { 
      throw new ControlUnitAdminException(ControlUnitAdminException.NO_SUCH_CONTROL_UNIT_ERROR);
    }
    
    return cu.invokeAction(actionID, arguments);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#createControlUnit(java.lang.String, java.lang.Object)
   */
  public String createControlUnit(String constructorID, Object arguments) throws ControlUnitException {
    throw new ControlUnitAdminException(ControlUnitAdminException.CREATION_NOT_SUPPORTED_ERROR);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#destroyControlUnit(java.lang.String)
   */
  public void destroyControlUnit(String controlUnitID) throws ControlUnitException {
    throw new ControlUnitAdminException(ControlUnitAdminException.DESTRUCTION_NOT_SUPPORTED_ERROR);
  }
  
  //******* End. CUProvider methods implementation *******//

  private String[] toStringArray(Vector strings) {
    if (strings != null && strings.size() > 0) {
      String[] result = new String [strings.size()];
      strings.copyInto(result);
      
      return result;
    }
    
    return null;
  }
  
  private void setControlUnitCallback(ControlUnit cu, CUAdminCallback adminCallback) {
    if (cu instanceof ManagedControlUnit) {
      try {
        ( (ManagedControlUnit)cu ).setControlUnitCallback(adminCallback);
      } catch (Throwable ex) {
        ex.printStackTrace(System.err);
      }
    }
  }
  
  private ControlUnit retrieveControlUnit(Object element) {
    return element != null ? ( (CUData)element ).getControlUnit() : null;
  }
  
  private boolean areVersionsEqual(String sourseVersion, String targetVersion) {
    return sourseVersion != null ? sourseVersion.equals(targetVersion) : targetVersion == null;
  }
}