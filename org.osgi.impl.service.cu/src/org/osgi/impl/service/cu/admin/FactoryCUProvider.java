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

import org.osgi.service.cu.admin.ControlUnitAdminException;
import org.osgi.service.cu.admin.spi.CUAdminCallback;
import org.osgi.service.cu.admin.spi.ControlUnitFactory;
import org.osgi.service.cu.ControlUnit;
import org.osgi.service.cu.ControlUnitException;

/**
 * A {@link org.osgi.impl.service.cu.admin.Provider} for control units
 * provided by factories in the framework.
 * 
 * @version $Revision$
 */
class FactoryCUProvider implements Provider {
  
  private final ControlUnitFactory factory;
  private final String[] parentTypes;
  private final String typeVersion;

  /**
   * Constructor.
   * 
   * @param factory control unit factory
   * @param parentTypes types of the control units which can be parent of control
   * units from the given factory
   * @param typeVersion version of the factory's control unit type
   */
  FactoryCUProvider(ControlUnitFactory factory, String[] parentTypes, String typeVersion) {
    this.factory = factory;
    this.parentTypes = parentTypes;
    
    this.typeVersion = typeVersion;
  }
  
  //******* CUProvider methods implementations *******//
  
  /* (non-Javadoc)
   * @see org.osgi.impl.service.cu.CUProvider#getParentTypes()
   */
  public String[] getParentTypes() {
     return parentTypes;
  }
  
  /* (non-Javadoc)
   * @see org.osgi.impl.service.cu.CUProvider#hasParentOfType(java.lang.String)
   */
  public boolean hasParentOfType(String parentType) {
    if (parentTypes != null) {
      for (int i = 0; i < parentTypes.length; i++) {
        if ( parentType.equals(parentTypes [i]) ) {
          return true;
        }
      }
    }
    
    return false;
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
    factory.setControlUnitCallback(adminCallback);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#getControlUnit(java.lang.String)
   */
  public ControlUnit getControlUnit(String cuID) {
    return factory.getControlUnit(cuID);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#getControlUnits(java.lang.String, java.lang.String)
   */
  public String[] getControlUnits(String parentType, String parentID) {
    return factory.getControlUnits(parentType, parentID);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#listControlUnits()
   */
  public String[] listControlUnits() {
    return factory.listControlUnits(); 
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#findControlUnits(java.lang.String, java.lang.Object)
   */
  public String[] findControlUnits(String finderID, Object arguments) throws ControlUnitException {
    return factory.findControlUnits(finderID, arguments);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#getParents(java.lang.String, java.lang.String)
   */
  public String[] getParents(String childID, String parentType) throws ControlUnitAdminException {
    return factory.getParents(childID, parentType);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#queryStateVariable(java.lang.String, java.lang.String)
   */
  public Object queryStateVariable(String cuID, String varID) throws ControlUnitException {
    return factory.queryStateVariable(cuID, varID);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#invokeAction(java.lang.String, java.lang.String, java.lang.Object)
   */
  public Object invokeAction(String cuID, String actionID, Object arguments) throws ControlUnitException {
    return factory.invokeAction(cuID, actionID, arguments);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#createControlUnit(java.lang.String, java.lang.Object)
   */
  public String createControlUnit(String constructorID, Object arguments) throws ControlUnitException {
    return factory.createControlUnit(constructorID, arguments);
  }
  
  /* (non-Javadoc)
   * @see org.osgi.service.cu.spi.ControlUnitFactory#destroyControlUnit(java.lang.String)
   */
  public void destroyControlUnit(String cuID) throws ControlUnitException {
    factory.destroyControlUnit(cuID);
  }
  
  //******* End. CUProvider methods implementations *******//

}