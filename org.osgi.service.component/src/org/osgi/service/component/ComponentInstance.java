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
 * A ComponentInstance encapsulates a component instance of an activated
 * component configuration. ComponentInstances are created whenever a component
 * configuration is activated.
 * 
 * <p>
 * ComponentInstances are never reused. A new ComponentInstance object will be
 * created when the component configuration is activated again.
 * 
 * @version $Revision$
 */
public interface ComponentInstance {
	/**
	 * Dispose of the component configuration for this component instance. The
	 * component configuration will be deactivated. If the component
	 * configuration has already been deactivated, this method does nothing.
	 */
	public void dispose();

	/**
	 * Returns the component instance of the activated component configuration.
	 * 
	 * @return The component instance or <code>null</code> if the component
	 *         configuration has been deactivated.
	 */
	public Object getInstance();
}