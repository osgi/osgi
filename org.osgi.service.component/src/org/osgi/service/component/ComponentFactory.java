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
 * <code>component</code> element, the Service Component Runtime will register a
 * ComponentFactory service to allow instances of the component to be created
 * rather than automatically create component instances as necessary.
 * 
 * @version $Revision$
 */
public interface ComponentFactory {
	/**
	 * Create a new instance of the component. Additional properties may be
	 * provided for the component instance.
	 * 
	 * @param properties Additional properties for the component instance.
	 * @return A ComponentInstance object encapsulating the component instance.
	 *         The returned component instance has been activated.
	 */
	public ComponentInstance newInstance(Dictionary properties);
}