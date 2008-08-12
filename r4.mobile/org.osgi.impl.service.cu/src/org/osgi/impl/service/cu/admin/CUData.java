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

import org.osgi.service.cu.ControlUnit;

/**
 * This class is used by 
 * {@link org.osgi.impl.service.cu.admin.CUProvider} and 
 * {@link org.osgi.impl.service.cu.admin.CUTracker} to hold
 * additional information about a control unit (namely what it's parent
 * control unit is, if any).
 * 
 * @version $Revision$
 */
class CUData {
  
  private final ControlUnit cu;
  private String parentType;
  private String parentID;
  
  /**
   * Constructor.
   * 
   * @param cu control unit
   * @param parentType the control unit parent's type or null, if the 
   * control unit has no parent
   * @param parentID the control unit parent's ID or null, if the 
   * control unit has no parent
   */
  CUData(ControlUnit cu, String parentType, String parentID) {
    this.cu = cu;
    setParent(parentType, parentID);
  }
  
  
  /**
   * Get the {@link ControlUnit} for which this data is.
   * 
   * @return control unit
   */
  final ControlUnit getControlUnit() {
    return cu;
  }
  
  /**
   * Get the type of the parent control unit.
   * 
   * @return parent's control unit type
   */
  final String getParentType() {
    return parentType;
  }
  
  /**
   * Get the ID of the parent control unit.
   * 
   * @return parent's control unit ID
   */
  final String getParentID() {
    return parentID;
  }
  
  /**
   * Updates the info about the parent of the corresponding 
   * {@link ControlUnit}.
   *  
   * @param parentType
   * @param parentID
   */
  final void setParent(String parentType, String parentID) {
    this.parentType = parentType;
    this.parentID = parentID;
  }
  
  /**
   * Checks if the corresponding {@link ControlUnit}'s
   * parent is the one given.
   * 
   * @param parentType parent control unit type
   * @param parentID parent control unit ID
   * @return true, if control unit with the given type and ID is parent of this
   * data's <code>ControlUnit</code>, otherwise - false 
   */
  final boolean hasParent(String parentType, String parentID) {
    return areEqual(parentType, this.parentType) && areEqual(parentID, this.parentID);
  }
  
  /**
   * Checks if the corresponding {@link ControlUnit} has a parent.
   * 
   * @return true if the control unit has parent, false - otherwise
   */
  final boolean hasParent() {
    return parentType != null && parentID != null;
  }
  
  /**
   * Checks if the two given <code>Strings</code> are both <code>null</null> 
   * or equal.
   * 
   * @param source source string
   * @param target target string
   * @return are the two strings equal
   */
  final boolean areEqual(String source, String target) {
    if (source == null) {
      return target == null;
    }
    
    return source.equals(target);
  }
  
}