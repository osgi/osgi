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

import java.util.Dictionary;

/**
 * When a component is declared with the <code>factory</code> attribute on its
 * <code>component</code> element, the Service Component Runtime will register
 * a ComponentFactory service to allow new component configurations to be
 * created and activated rather than automatically creating and activating
 * component configuration as necessary.
 * 
 * @version $Revision$
 */
public interface ComponentFactory {
	/**
	 * Create and activate a new component configuration. Additional properties
	 * may be provided for the component configuration.
	 * 
	 * @param properties Additional properties for the component configuration.
	 * @return A ComponentInstance object encapsulating the component
	 *         configuration. The returned component configuration has been
	 *         activated and, if the component specifies a <code>service</code>
	 *         element, the component configuration has been registered as a
	 *         service.
	 * @throws ComponentException If the Service Component Runtime is unable to
	 *         satisfy the component configuration.
	 */
	public ComponentInstance newInstance(Dictionary properties);
}