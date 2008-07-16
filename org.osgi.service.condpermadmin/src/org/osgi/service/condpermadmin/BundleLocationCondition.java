/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2005, 2007). All Rights Reserved.
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

package org.osgi.service.condpermadmin;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

import org.osgi.framework.*;

/**
 * Condition to test if the location of a bundle matches a pattern. Pattern
 * matching is done according to the filter string matching rules.
 * <p>
 * A second String argument is optional
 * and if present and equal to "!" indicates that the Condition must
 * return the logical NOT of the bundle location result.  So for example if
 * the location string matches "bundles/1" then all bundles with a location
 * that is NOT "bundles/1" would match the condition, while a bundle with
 * location "bundles/1" would not match the condition.  Note that if the
 * second argument is not equal to "!" then the argument should be ignored
 * and should not affect the processing of the condition.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public class BundleLocationCondition {
	private static final String	CONDITION_TYPE	= "org.osgi.service.condpermadmin.BundleLocationCondition";

	/**
	 * Constructs a condition that tries to match the passed Bundle's location
	 * to the location pattern.
	 * 
	 * @param bundle The Bundle being evaluated.
	 * @param info The ConditionInfo to construct the condition for. The first
	 *        args of the ConditionInfo must be a String which specifies the
	 *        location pattern to match against the Bundle location. Matching is
	 *        done according to the filter string matching rules. Any '*'
	 *        characters in the location argument are used as wildcards when
	 *        matching bundle locations unless they are escaped with a '\'
	 *        character.  The optional second args of the ConditionInfo must
	 *        be a String.  If this second argument is equal to "!"
	 *        then the Condition shall return the logical NOT
	 *        of the match of the first argument.  If the second argument
	 *        String is present but does not equal "!" then the second
	 *        argument String should be ignored.
	 * @return Condition object for the requested condition.
	 */
	static public Condition getCondition(final Bundle bundle,
			final ConditionInfo info) {
		if (!CONDITION_TYPE.equals(info.getType()))
			throw new IllegalArgumentException(
					"ConditionInfo must be of type \"" + CONDITION_TYPE + "\"");
		String[] args = info.getArgs();
		if (args.length != 1 && args.length != 2)
			throw new IllegalArgumentException("Illegal number of args: " + args.length);
		String bundleLocation = (String) AccessController.doPrivileged(new PrivilegedAction() {
					public Object run() {
						return bundle.getLocation();
					}
				});
		Filter filter = null;
		try {
			filter = FrameworkUtil.createFilter("(location="
					+ escapeLocation(args[0]) + ")");
		}
		catch (InvalidSyntaxException e) {
			// this should never happen, but just in case
			throw new RuntimeException("Invalid filter: " + e.getFilter());
		}
		Hashtable matchProps = new Hashtable(2);
		matchProps.put("location", bundleLocation);
		boolean negative = args.length == 2 && args[1].length() > 0 ? args[1].charAt(0) == '!' : false;
		return negative ^ filter.match(matchProps) ? Condition.TRUE : Condition.FALSE;
	}

	private BundleLocationCondition() {
		// private constructor to prevent objects of this type
	}

	/**
	 * Escape the value string such that '(', ')' and '\' are escaped. The '\'
	 * char is only escaped if it is not followed by a '*'.
	 * 
	 * @param value unescaped value string.
	 * @return escaped value string.
	 */
	private static String escapeLocation(String value) {
		boolean escaped = false;
		int inlen = value.length();
		int outlen = inlen << 1; /* inlen * 2 */

		char[] output = new char[outlen];
		value.getChars(0, inlen, output, inlen);

		int cursor = 0;
		for (int i = inlen; i < outlen; i++) {
			char c = output[i];
			switch (c) {
				case '\\' :
					if (i + 1 < outlen && output[i + 1] == '*')
						break;
				case '(' :
				case ')' :
					output[cursor] = '\\';
					cursor++;
					escaped = true;
					break;
			}

			output[cursor] = c;
			cursor++;
		}

		return escaped ? new String(output, 0, cursor) : value;
	}
}
