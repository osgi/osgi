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
package org.osgi.service.command.annotations;

import java.lang.annotation.*;

/**
 * An Option provides the information to treat a method parameter an option with
 * a value.
 * 
 * Options and flags always start with a minus sign ('-, \u002d) in the command
 * line. An option always is followed by a value.
 * 
 * If the same name alias is used by other Flags or Options then the first one
 * in the parameter declaration wins.
 * 
 * Flags and Options must be the first parameters in the method, it is not
 * possible to intersperse the Flags and Options with parameters that have none.
 * All remaining parameters without a Parameter description are unnamed.
 * 
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Option {

	/**
	 * Magic value to indicate that a value is not set. The shell must
	 * then use null,0, or false depending on the type.
	 */
	String	NOT_SET	= "326285af7359e197efbeac04ef9e4443a3ea1281";

	/**
	 * Magic value to indicate that a value is supposed to be null.
	 */
	String	NULL	= "aa0b23939aa1ddf22f5d8d7312968f602d8100b3";

	/**
	 * Parameter name and aliases. The shell will only be able to recognize
	 * names that start with a hyphen ('-', '\u002D'), however, other names are
	 * allowed. If the list of aliases is empty,
	 * 
	 * For example, to support the options -f/--files, return {"-f", "--files"}.
	 * 
	 * If the alias contains an empty array then the Parameter can be provided
	 * by the session because it is a built in value like the Command Session
	 * object or the Terminal.
	 * 
	 * @return parameter names.
	 **/
	String[] alias() default {};

	/**
	 * The value of the parameter if its name is not present on the command
	 * line. This value is effectively the default value for the parameter.
	 * 
	 * If this method returns {@link #NOT_SET} then this option must be
	 * specified.
	 * 
	 * @return default value of the parameter if its name is not present on the
	 *         command line.
	 **/
	String absent() default NOT_SET;

	/**
	 * Set if this parameter can be repeated multiple times. For an option, the
	 * collected parameters will be aggregated in a Collection.
	 */

	boolean repeat() default false;
	
	/**
	 * The descriptive text.
	 * 
	 * @return the descriptive text
	 */
	String description();	
	
}
