/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import org.eclipse.osgi.framework.msg.MessageFormat;
import org.eclipse.osgi.framework.util.FrameworkMessageFormat;

/**
 * This class retrieves strings from a resource bundle
 * and returns them, formatting them with MessageFormat
 * when required.
 * <p>
 * It is used by the system classes to provide national
 * language support, by looking up messages in the
 * <code>
 *    org.eclipse.osgi.framework.internal.core.ConsoleMessages
 * </code>
 * resource bundle. Note that if this file is not available,
 * or an invalid key is looked up, or resource bundle support
 * is not available, the key itself will be returned as the
 * associated message. This means that the <em>KEY</em> should
 * a reasonable human-readable (english) string.
 */

public class ConsoleMsg {

	static public MessageFormat formatter;

	// Attempt to load the message bundle.
	static {
		formatter = FrameworkMessageFormat.getMessageFormat("org.eclipse.osgi.framework.internal.core.ConsoleMessages"); //$NON-NLS-1$
	}
}