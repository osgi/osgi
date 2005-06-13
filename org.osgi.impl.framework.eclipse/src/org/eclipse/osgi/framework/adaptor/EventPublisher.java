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

package org.eclipse.osgi.framework.adaptor;

import org.osgi.framework.Bundle;

/**
 * The EventPublisher is used by FrameworkAdaptors to publish events to the
 * Framework.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 3.1
 */
public interface EventPublisher {

	/**
	 * Publish a FrameworkEvent.
	 *
	 * @param type FrameworkEvent type.
	 * @param bundle Bundle related to FrameworkEven or <tt>null</tt> to for the
	 * system bundle.
	 * @param throwable Related exception or <tt>null</tt>.
	 * @see org.osgi.framework.FrameworkEvent
	 */
	public abstract void publishFrameworkEvent(int type, Bundle bundle, Throwable throwable);

}
