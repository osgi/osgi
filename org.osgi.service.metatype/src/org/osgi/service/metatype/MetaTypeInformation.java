/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.metatype;

import org.osgi.framework.Bundle;

/**
 * A MetaType Information object is created by the MetaTypeService to return
 * meta type information for a specific bundle.
 * 
 * @version $Revision$
 * @since 1.1
 */
public interface MetaTypeInformation extends MetaTypeProvider {
	/**
	 * Return the PIDs (for ManagedServices) for which ObjectClassDefinition
	 * information is available.
	 * 
	 * @return Array of PIDs.
	 */
	public String[] getPids();

	/**
	 * Return the Factory PIDs (for ManagedServiceFactories) for which
	 * ObjectClassDefinition information is available.
	 * 
	 * @return Array of Factory PIDs.
	 */
	public String[] getFactoryPids();

	/**
	 * Return the bundle for which this object provides meta type information.
	 * 
	 * @return Bundle for which this object provides meta type information.
	 */
	public Bundle getBundle();
}