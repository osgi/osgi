/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.service.resolver;

/**
 * A representation of one bundle import constraint as seen in a 
 * bundle manifest and managed by a state and resolver.
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.1
 */
public interface BundleSpecification extends VersionConstraint {

	/**
	 * Returns whether or not this bundle specificiation is exported from the 
	 * declaring bundle.
	 * 
	 * @return whether this specification is exported
	 */
	public boolean isExported();

	/**
	 * Returns whether or not this bundle specificiation is optional.
	 * 
	 * @return whether this specification is optional
	 */
	public boolean isOptional();
}
