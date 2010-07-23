/*
 * Copyright (c) OSGi Alliance (2008, 2010). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.service.formatter;

import java.util.*;

/**
 * A Formatter service can format an object to a CharSequence. The purpose of a
 * formatter is to create human readable output from general objects. The
 * primary purpose is for the command shell, but other uses do exist.
 * 
 * A key concept in the formatter is the level of detail that is displayed. In
 * practice, the detail can vary depending on the use case. For this reason,
 * there are three levels:
 * <ol>
 * <li><{@link #INSPECT} - Provides the greatest level of detail. The output can
 * consist of multiple lines. If columns are used, it is recommended to use 40
 * characters for the first column. However, the format of the output is more or
 * less free.</li>
 * <li><{@link #LINE} - This level is used to print collections. The output may
 * consist of multiple lines but columns should have fixed width so they align.</li>
 * <li><{@link #PART} - A cell in a table.</li>
 * </ol>
 * 
 * Multiple lines must be separated with LF character ( ), not a CR or CR/LF.
 * The end of the output must never be ended with a LF. The receiver must
 * properly separate the output.
 * 
 * Ranking. If two formatters have the same preference for conversion than the 
 * service with the highest ranking must be used. 
 * 
 * A formatter should follow the Locale objects given in the call. This list is
 * in preferential order. If no locales are given the preference is the default
 * locale and finally English.
 * 
 * @ThreadSafe
 * @ConsumerInterface
 * @version $Id$
 */
public interface Formatter {
	/**
	 * This property is <code>String+</code>. A string, or array/collection of
	 * strings, and specifies the names of the classes and/or interfaces that
	 * this Formatter service recognizes.
	 * 
	 * Not setting this property indicates that this is an
	 * <em>aggregate formatter</em>. An aggregate formatter promises to use the
	 * existing Formatter services to convert and is thus not type specific.
	 */
	String	OSGI_FORMATTER_TYPE	= "osgi.formatter.type";

	/**
	 * Print the object in detail using as many lines and columns as needed. For
	 * example, a Bundle object would be formatted with all its details like id,
	 * location, all its headers, registered services, etc. For this level,
	 * completeness is more important than space. The output can contain
	 * multiple lines but should not end in a LF (\n, ).
	 */
	int		INSPECT				= 0;

	/**
	 * Print the object as a row in a table. The columns should align for
	 * multiple objects printed beneath each other. For example, a bundle would
	 * print the primary information like the id, the state, the name, the
	 * version, but it would ignore the headers, registered services, etc. The
	 * print may run over multiple lines but must not end in a LF (\n, ).
	 */
	int		LINE				= 1;

	/**
	 * Print the value in a small format so that it is identifiable. For
	 * example, for a bundle it would print the id or bundle-symbolic name
	 * combination. If applicable, the constructor of the given class (assuming
	 * it is not an interface) should take the result of this formatting to
	 * reconstruct this object.
	 */
	int		PART				= 2;

	/**
	 * Convert an object to a CharSequence object in the requested format. The
	 * format can be {@link #INSPECT}, {@link #LINE}, or {@link #PART}. Other
	 * values must throw IllegalArgumentException.
	 * 
	 * @param target The object to be converted to a <code>CharSequence</code>
	 *        object
	 * @param level One of {@link #INSPECT}, {@link #LINE}, or {@link #PART}.
	 * @param locales A list of locales in order of preference. If no locales
	 *        are specified, use the default locale and then english.
	 * @return A printed object of potentially multiple lines, must never be
	 *         <code>null</code>. The return is a CharSequence because this is
	 *         usually more efficient.
	 * @throws Exception If something fails during the formatting
	 */

	CharSequence format(Object target, int level, Locale... locales)
			throws Exception;

}
