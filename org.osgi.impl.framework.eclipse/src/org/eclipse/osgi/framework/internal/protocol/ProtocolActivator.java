/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.protocol;

import org.eclipse.osgi.framework.adaptor.FrameworkAdaptor;
import org.osgi.framework.BundleContext;

//TODO this is non-API, and has no internal clients - do we really need it? at least explain why
public interface ProtocolActivator {

	public void start(BundleContext context, FrameworkAdaptor adaptor);

}
