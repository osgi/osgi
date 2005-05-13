/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
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
