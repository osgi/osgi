/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.adaptor;

import org.osgi.framework.Bundle;

/**
 * Contains information about activated bundles and acts as the main 
 * entry point for logging plugin activity.
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.1
 */
public interface BundleWatcher {
	/**
	 * Called when a bundle is being activated.
	 * @param bundle the bundle being activated.
	 */
	public void startActivation(Bundle bundle);

	/**
	 * Called after a bundle has been activated.
	 * @param bundle the bundle being activated.
	 */
	public void endActivation(Bundle bundle);
}
