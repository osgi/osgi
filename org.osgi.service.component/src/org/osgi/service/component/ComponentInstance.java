/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.component;

/**
 * A ComponentInstance encapsulates an instance of a component.
 * ComponentInstances are created whenever an instance of a component is
 * created.
 * 
 * @version $Revision$
 */
public interface ComponentInstance {
	/**
	 * Dispose of this component instance. The instance will be deactivated. If
	 * the instance has already been deactivated, this method does nothing.
	 */
	public void dispose();

	/**
	 * Returns the component instance. The instance has been activated.
	 * 
	 * @return The component instance or <code>null</code> if the instance has
	 *         been deactivated.
	 */
	public Object getInstance();
}