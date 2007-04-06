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

import org.osgi.service.cu.admin.spi.ControlUnitFactory;


/**
 * This class is used by the {@link org.osgi.service.cu.admin.ControlUnitAdmin ControlUnitAdmin} 
 * implementation to access the control units, provided in the framework by 
 * {@link org.osgi.service.cu.admin.spi.ControlUnitFactory} and as
 * {@link org.osgi.service.cu.ControlUnit} or
 * {@link org.osgi.service.cu.admin.spi.ManagedControlUnit}s, in a
 * common way.<BR><BR>
 * 
 * The <code>ControlUnitAdmin</code> implementation holds a table in which 
 * for every type of control units there is a single <code>Provider</code>, 
 * which provides methods for obtaining control units by their ID's, invoking
 * or quering state variables on given control unit, getting of parent or child
 * control units for given unit, etc. 
 * 
 * @version $Revision$
 */
interface Provider extends ControlUnitFactory {
  
  /**
   * Checks if any control unit, of the type for which this provider is, has
   * a parent of the given type.
   * 
   * @param parentType parent control unit type to check for
   * @return true, if there is a control unit with such parent's type, otherwise - false
   */
  public boolean hasParentOfType(String parentType);
  
  /**
   * Get all distinct parent types which control units of the type for which 
   * this provider is have.
   * 
   * @return parent types
   */
  public String[] getParentTypes();
  
  /**
   * Get the version of the control unit type for which this provider is.
   * 
   * @return type version
   */
  public String getTypeVersion();
  
}