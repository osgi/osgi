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

import org.osgi.service.cu.StateVariableListener;
import org.osgi.service.cu.admin.ControlUnitListener;
import org.osgi.service.cu.admin.HierarchyListener;

/**
 * Represents the interface of the Control Unit Admin bundle provided to the
 * implementations of the {@link org.osgi.service.cu.admin.spi.ManagedControlUnit} and
 * {@link org.osgi.service.cu.admin.spi.ControlUnitFactory}.
 * Managed Control Units and Control Unit Factories use methods of this interface to 
 * notify the Control Unit Admin bundle for changes of the state variables. Control 
 * Unit Factories also use this interface to notify the Control Unit Admin bundle 
 * for appearance and disappearance of Control Unit instances maintained by the factory.
 *
 * @see org.osgi.service.cu.admin.spi.ManagedControlUnit#setControlUnitCallback(CUAdminCallback)
 * @see org.osgi.service.cu.admin.spi.ControlUnitFactory#setControlUnitCallback(CUAdminCallback)
 * @version $Revision$
 */
public interface CUAdminCallback extends ControlUnitListener, StateVariableListener, HierarchyListener {
}
