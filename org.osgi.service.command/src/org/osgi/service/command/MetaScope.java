/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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
package org.osgi.service.command;

import java.util.*;

import org.osgi.service.converter.*;

/**
 * A scope defines the meta information of many commands. The information about
 * the scopes can be obtained from {@link CommandSession#getMetaScopes()}. If
 * annotations are present, the information is augmented with special parameter
 * and descriptive information.
 * 
 * Commands are stored in the session variables. Commands are added from the
 * service registry or commands are added programmatically, however, all
 * commands are part of the session variables and changing the session variables
 * will change the available commands in a scope.
 * 
 * Each command is stored in the session variables under the name: @{code
 * <scope>:<function>}.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface MetaScope {

	/**
	 * Return the name of this scope.
	 * 
	 * @return Name of the scope, always {@code non-null}.
	 */
	String getName();

	/**
	 * Return a description of this scope, if present.
	 * 
	 * @return A description or {@code null}
	 */
	String getDescription();

	/**
	 * Return an unmodifiable list of Meta Function objects. This list is a
	 * snapshot of the current state and will not follow the state.
	 * 
	 * @return an unmodifiable list of Meta Function objects.
	 */
	Collection<MetaFunction> getMetaFunctions();

	/**
	 * A Meta Function describes a scoped function.
	 */
	public interface MetaFunction {
		/**
		 * Return the name of this function.
		 * 
		 * @return Name of the function, always {@code non-null}.
		 */
		String getName();

		/**
		 * Return a description of this scope, if present.
		 * 
		 * @return A description or {@code null}
		 */
		String getDescription();

		/**
		 * Return the Meta Arguments of this Meta Function.
		 * 
		 * @return The meta arguments
		 */
		Collection<MetaParameter> getMetaParameters();

		/**
		 * Answer if this is mapped to a {@code vararg} method. A vararg method
		 * can be used to fill out the last argument of a function.
		 * 
		 * @return {@code true} if this is for a vararg method, otherwise
		 *         {@code false}
		 */
		boolean isVarArgs();

		/**
		 * 
		 */
		public interface MetaParameter {
			/**
			 * A parameter type without any meta information, a so called
			 * UNNAMED parameter. Unnamed parameters have no Flag or Option
			 * annotation. The UNNAMED parameters must be at the end of the
			 * parameter list for a method.
			 */
			int	PARAMETER_UNNAMED				= 0;

			/**
			 * The parameter type that is marked as option. An option can be
			 * omitted from the parameter list, in that case the
			 * {@link #isAbsent()} must be used.
			 * 
			 * Options are marked in the command line with names that start with
			 * '-'. Options must be followed by a value, for example
			 * 
			 * <pre>
			 * foo -f (bar file) hello
			 * </pre>
			 */
			int	PARAMETER_OPTION				= 1;

			/**
			 * The parameter type is marked as a Flag. A Flag does not require a
			 * value in the command line. If the flag is used in the command
			 * line then the value is given by {@link #ifPresent()}, otherwise
			 * the value is given by {@link #isAbsent()}.
			 */
			int	PARAMETER_FLAG					= 2;

			/**
			 * The parameter can be provided by the session, for example the
			 * Terminal object or the Command Session object.
			 */
			int	PARAMETER_PROVIDED_BY_SESSION	= 3;

			/**
			 * Return the Parameter Type, either: {@value #PARAMETER_UNNAMED},
			 * {@link #PARAMETER_FLAG}, {@link #PARAMETER_OPTION},
			 * {@link #PARAMETER_PROVIDED_BY_SESSION}.
			 * 
			 * @return the parameter type
			 */
			int getParameterType();

			/**
			 * Provide a description of the parameter.
			 * 
			 * @return A description of the parameter or null of none available.
			 */
			String getDescription();

			/**
			 * List the name of aliases for this parameter. An alias must start
			 * with a minus sign ('-' \u002D) and must not be '--'. For example:
			 * {"-f", "--file"}
			 * 
			 * If the list of aliases can be empty, in that case this must be an
			 * unnamed parameter.
			 * 
			 * @return Array of aliases.
			 */
			String[] getAliases();

			/**
			 * Return the Java type of the parameter. This is a Reified Type
			 * that can be used in the conversion model.
			 * 
			 * @return the refied type that describes the parameter's type.
			 */
			ReifiedType< ? > getType();

			/**
			 * The value to use when none of the parameter aliases are used in
			 * the command line for an option or a flag.
			 * 
			 * @return The value to use when the parameter is not used in the
			 *         command line, can be {@code null}.
			 */
			String isAbsent();

			/**
			 * The value to use for a flag when it is used in the command line.
			 * This method is undefined for an option.
			 * 
			 * @return the parameter value for a flag.
			 */
			String ifPresent();
		}
	}
}
