/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.adaptor.core;

import org.eclipse.osgi.framework.msg.MessageFormat;
import org.eclipse.osgi.framework.util.FrameworkMessageFormat;

/**
 * This class retrieves strings from a resource bundle
 * and returns them, formatting them with MessageFormat
 * when required.
 */

public class AdaptorMsg {

	static public MessageFormat formatter;

	// Attempt to load the message bundle.
	static {
		formatter = FrameworkMessageFormat.getMessageFormat("org.eclipse.osgi.framework.adaptor.core.ExternalMessages"); //$NON-NLS-1$
	}
}