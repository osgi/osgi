/*
 * $Header$
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

import org.osgi.service.cu.StateVariableListener;
import org.osgi.service.cu.admin.ControlUnitAdminListener;
import org.osgi.service.cu.admin.HierarchyListener;

/**
 * Represents the interface of the 
 * {@link org.osgi.service.cu.admin.ControlUnitAdmin} service provided to the
 * implementations of the {@link org.osgi.service.cu.admin.spi.ManagedControlUnit} and
 * {@link org.osgi.service.cu.admin.spi.ControlUnitFactory}.
 * ManagedControlUnit and ControlUnitFactory instances use the methods of 
 * this interface to notify the <code>ControlUnitAdmin</code> service for changes of 
 * the state variables. Control unit factories also use this interface 
 * to notify the <code>ControlUnitAdmin</code> service
 * for appearance and disappearance of control unit instances maintained by the factory.
 *
 * @see org.osgi.service.cu.admin.spi.ManagedControlUnit#setControlUnitCallback(CUAdminCallback)
 * @see org.osgi.service.cu.admin.spi.ControlUnitFactory#setControlUnitCallback(CUAdminCallback)
 * @version $Revision$
 */
public interface CUAdminCallback extends ControlUnitAdminListener, StateVariableListener, HierarchyListener {
}
