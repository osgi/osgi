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

/**
 * A Parameter provides the information to treat a method parameter as a flag or
 * option.
 * 
 * Options and flags always start with a minus sign ('-, \u002d) in the command
 * line. An option always is followed by a value while a flag goes without a
 * value. The distinction is made with the {@link #ifPresent()} method. If this method
 * returns {@link #NOT_SET} then this Parameter describes an option, otherwise
 * it is a flag because a value is provided when the flag is used.
 * 
 * If the same name aliases are used in other flags or options than the first
 * one in the parameter declaration wins.
 * 
 * Flags and options must be the first parameters in the method, it is not
 * possible to intersperse the flags and options with parameters that have none. All
 * remaining parameters without a Parameter description are unnamed.
 * 
 * @version $Id$
 */
public @interface Parameter {
	/**
	 * Magic value to provide a default for {@link #ifAbsent()} and
	 * {@link #ifPresent()} to indicate it is not set.
	 */
	String	NOT_SET	= "be3831f6ddfaa48efe1c0aba9e81c6251bf0f0ca";

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
	String[] alias();

	/**
	 * The value if the flag or option is used in the command line. If 
	 * this returns {@link #NOT_SET} then this is an option
	 * and the value after the alias must be used. This value
	 * mist always be set for a flag.
	 * 
	 * @return The value to use when one of the flag names is present in the
	 *         command line.
	 */
	String ifPresent() default NOT_SET;

	/**
	 * The value of the parameter if its name is not present on the command
	 * line. This value is effectively the default value for the parameter.
	 * 
	 * If this method returns {@link #NOT_SET} then an appropriate default
	 * must be chosen that is negative. That is, 0, false, null.
	 * 
	 * @return default value of the parameter if its name is not present on the
	 *         command line.
	 **/
	String ifAbsent();
	
	
}
