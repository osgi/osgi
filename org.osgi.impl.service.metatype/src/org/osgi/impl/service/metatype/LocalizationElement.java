/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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
 */

package org.osgi.impl.service.metatype;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LocalizationElement {

	public static final char	KEY_SIGN		= '%';					//$NON-NLS-1$
	String						_localization	= null;
	ResourceBundle				_rb;

	/**
	 * Internal method
	 */
	void setResourceBundle(ResourceBundle rb) {
		this._rb = rb;
	}

	/**
	 * Method to get the localized text of inputed String.
	 */
	String getString(String key) {

		if (key == null) {
			// Shall it return null or empty String ?
			return null; //$NON-NLS-1$
		}

		if ((key.charAt(0) == KEY_SIGN) && (key.length() > 1)) {
			if (_rb != null) {
				try {
					String transfered = _rb.getString(key.substring(1));
					if (transfered != null) {
						return transfered;
					}
				}
				catch (MissingResourceException mre) {
					// Nothing found, just return the original key.
				}
			}
		}
		return key;
	}
}
