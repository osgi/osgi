/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2005, 2006). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
