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

import java.util.*;

/**
 * @author Julian Chen
 * @version 1.0
 */
public class ValueTokenizer {

	private static final char	SEPARATE				= ',';			//$NON-NLS-1$
	private static final char	CONTROL					= '\\';			//$NON-NLS-1$

	Vector _value_vector = new Vector(7);

	/*
	 * Constructor of class ValueTokenizer
	 */
	public ValueTokenizer(String values_str) {

		if (values_str != null) {

			StringBuffer buffer = new StringBuffer("");	//$NON-NLS-1$
			for (int i=0; i<values_str.length(); i++) {
				if (values_str.charAt(i) == CONTROL) {
					if (i+1 < values_str.length()) {
						buffer.append(values_str.charAt(++i));
						continue;
					}
					else {
						// CONTROL char should not occur in last char.
						Logging.log(Logging.ERROR, this,
								"ValueTokenizer(String)",
								Msg.formatter.getString(
										"TOKENIZER_GOT_INVALID_DATA"));
						// It's an invalid char, but since it's the last one,
						// just ignore it.
						continue;
					}
				}
				if (values_str.charAt(i) == SEPARATE) {
					_value_vector.addElement(buffer.toString().trim());
					buffer = new StringBuffer(""); //$NON-NLS-1$
					continue;
				}
				buffer.append(values_str.charAt(i));
			}
			// Don't forget the final one.
			_value_vector.addElement(buffer.toString().trim());
		}
	}

	/*
	 * Method to return values as Vector.
	 */
	public Vector getValuesAsVector() {
		return _value_vector;
	}

	/*
	 * Method to return values as String[] or null.
	 */
	public String[] getValuesAsArray() {

		String[] value_array = null;
		if ((_value_vector != null) && (_value_vector.size() != 0)) {
			
			value_array = new String[_value_vector.size()];
			_value_vector.toArray(value_array);
		}

		return value_array;
	}
}
