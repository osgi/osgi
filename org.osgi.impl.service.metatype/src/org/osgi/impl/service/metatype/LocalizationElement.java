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
	String getLocalized(String key) {

		if (key == null) {
			return null;
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
					// Nothing found for this key.
				}
			}
			// If no localization file available or no localized value found
			// for the key, then return the raw data without the key-sign.
			return key.substring(1);
		}
		return key;
	}
}
