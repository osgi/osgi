/*
 * $Header$
 *
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */
package org.osgi.impl.service.metatype;

import org.osgi.impl.service.metatype.msg.MessageFormat;

/**
 * This class retrieves strings from a resource bundle and returns them,
 * formatting them with MessageFormat when required.
 * <p>
 * It is used by the system classes to provide national language support, by
 * looking up messages in the <code>
 *    com.ibm.osg.service.metatypeimpl.ExternalMessages
 * </code>
 * resource bundle. Note that if this file is not available, or an invalid key
 * is looked up, or resource bundle support is not available, the key itself
 * will be returned as the associated message. This means that the <em>KEY</em>
 * should a reasonable human-readable (english) string.
 * 
 * @author
 * @version 1.0
 */

public class Msg {
	static public MessageFormat	formatter;

	// Attempt to load the message bundle.
	static {
		formatter = new MessageFormat(
				"com.ibm.osg.service.metatypeimpl.nls.ExternalMessages", java.util.Locale.getDefault(), new MsgObject().getClass()); //$NON-NLS-1$
	}
}

/* Need a simple class to pass the MassageFormat constructor */
class MsgObject {

}
