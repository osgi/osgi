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
package org.osgi.service.cu.admin.spi;

import org.osgi.service.cu.ControlUnit;

/**
 * This interface must be registered as an OSGi service in order to make a single
 * resource manageable through the control unit abstraction. <br>
 * The ManagedControlUnit services should not be used directly by the
 * applications. Instead applications access them as
 * {@link org.osgi.service.cu.ControlUnit} instances obtained via the
 * {@link org.osgi.service.cu.admin.ControlUnitAdmin} service. The
 * {@link org.osgi.service.cu.admin.ControlUnitAdmin} service is responsible for
 * tracking all <code>ManagedControlUnit</code> services registered in the
 * service registry of the framework and to notify registered
 * {@link org.osgi.service.cu.admin.ControlUnitAdminListener}s when a new
 * <code>ManagedControlUnit</code> appears or an existing one is unregistered.
 * 
 * To be properly handled by the Control Unit Admin service the
 * <code>ManagedControlUnit</code> service must be registered with the
 * following required properties:
 * <ul>
 * <li>Property {@link org.osgi.service.cu.ControlUnitConstants#TYPE} with
 * value of type <code>String</code> specifying the type of the control unit
 * instance.
 * <li>Property {@link org.osgi.service.cu.ControlUnitConstants#ID} with value
 * of type <code>String</code> specifying the identifier of the control unit
 * instance.
 * </ul>
 * Optionally the registration properties may contain properties
 * {@link org.osgi.service.cu.ControlUnitConstants#PARENT_ID} and
 * {@link org.osgi.service.cu.ControlUnitConstants#PARENT_TYPE}, both with
 * values of type <code>String</code> specifying the parent control unit in
 * the control unit hierarchy.
 * <p>
 * 
 * Note that the control units exported through <code>ManagedControlUnit</code>
 * may specify only one control unit as a parent, while control units registered
 * by a {@link org.osgi.service.cu.admin.spi.ControlUnitFactory} may have more
 * than one parent.
 * <p>
 * 
 * <code>ManagedControlUnits</code> may dynamically change their parent by
 * modifying <code>PARENT_TYPE</code> and/or <code>PARENT_ID</code> in their
 * service registration properties.
 * <p>
 *
 * Control Units that support versioning of their type should additionally 
 * register with the property
 * {@link org.osgi.service.cu.ControlUnitConstants#VERSION} with value of type
 * <code>String</code>.
 * 
 * @version $Revision$
 */
public interface ManagedControlUnit extends ControlUnit {

  /**
   * Supplies the Control Unit Admin callback interface to the implementation of
   * the <code>ManagedControlUnit</code> service. <br>
   * 
   * This method is invoked by the Control Unit Admin service with a non-
   * <code>null</code> argument after registration of the
   * <code>ManagedControlUnit</code> service or after startup of the Control
   * Unit Admin for already registered control units.
   * <p>
   * 
   * It is supposed that the Managed Control Unit will assign this reference to
   * an instance variable and use it later to notify the Control Unit Admin for
   * changes in the state variables of the control unit.
   * <p>
   * 
   * The method is invoked with a <code>null</code> argument during
   * unregistration of the <code>ManagedControlUnit</code> service or when the
   * Control Unit Admin is stopped.
   * 
   * @param adminCallback reference to the control unit callback interface or
   *          <code>null</code> if previously set reference is not longer
   *          valid.
   */
  public void setControlUnitCallback(CUAdminCallback adminCallback);

}
