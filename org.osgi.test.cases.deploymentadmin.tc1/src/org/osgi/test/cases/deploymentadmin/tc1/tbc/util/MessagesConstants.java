/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 24/03/2005   Andre Assad
 * 26           Implement MEG TCK for the deployment RFC-88
 * ===========  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.tc1.tbc.util;

import java.text.MessageFormat;

public class MessagesConstants {
	public static final String EXCEPTION_THROWN = "Expected the exception {0} and {1} was thrown.";
	public static final String EXCEPTION_CORRECTLY_THROWN = "{0} correctly thrown.";
	public static final String UNEXPECTED_EXCEPTION = "Unexpected {0} exception thrown.";
	
	public static final String ASSERT_EQUALS = "Asserting value of {0}.";
	public static final String ASSERT_TRUE = "{0} is {1}";
	public static final String ASSERT_NOT_NULL = "{0} is not null.";
	public static final String ASSERT_NULL = "{0} is null.";
	
	public static final String FAIL_EXCEPTION = "No {0} thrown.";
	
	public static String getMessage(String msg, Object[] params) {
		return MessageFormat.format(msg,params);
	}
}
