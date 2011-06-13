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

import java.lang.reflect.*;
import java.util.*;

/**
 * A Meta Scope defines the meta information of a set of commands grouped in a
 * <i>scope</i>. The information about the scopes can be obtained from
 * {@link CommandSession#getMetaScopes()}. If annotations are present, the
 * information is augmented with special @Parameter and @Description
 * information.
 * 
 * @version $Id$
 * 
 * @ThreadSafe
 * @ProviderType
 */
public interface Meta {
	/**
	 * 
	 */
	public interface Scope {

		/**
		 * Return the name of this scope.
		 * 
		 * @return Name of the scope, always {@code non-null}.
		 */
		String name();

		/**
		 * Return a description of this scope, if present. The description is
		 * derived from the @Description annotation or from localized resources.
		 * 
		 * @return A description or {@code null}
		 */
		String description();

		/**
		 * Return the summary. The summary is derived from the @Description
		 * annotation or from localized resources.
		 * 
		 * @return A summary or {@code null}
		 */
		String summary();

		/**
		 * Return an unmodifiable list of Meta Function objects. This list is a
		 * snapshot of the current state. Each member of the returned list
		 * describes an existing function that can be called in this scope.
		 * 
		 * @return an unmodifiable list of Meta Function objects.
		 */
		Collection< ? extends Command> commands();
	}

	/**
	 * A Meta Function describes a scoped function. The information is derived
	 * from the @Description annotation on the method
	 */
	public interface Command {
		/**
		 * Return the name of this function. This is normally the corresponding
		 * method name. From the command line, a simpler form of the name can be
		 * used.
		 * 
		 * @return Name of the function, always {@code non-null}.
		 */
		String name();

		/**
		 * Return a description of this function, if present. This information
		 * comes from the @Description annotation or resources.
		 * 
		 * @return A description or {@code null}
		 */
		String description();

		/**
		 * Return a summary of this function, if present. This information comes
		 * from the @Description annotation or resources.
		 * 
		 * @return A summary or {@code null}
		 */
		String summary();

		/**
		 * Return the Meta Parameter objects of this Meta Function.
		 * 
		 * Each parameter in the associated method has a MetaParameter object
		 * associated with it. This object derives its information from the @Description
		 * and @Paramater annotation as well as the method and its parameter
		 * types information.
		 * 
		 * Every parameter has a MetaParameter object, regardless if it has a @Description
		 * or @Parameter annotation.
		 * 
		 * @return The Meta Parameter objects, never {@code null}
		 */
		Collection< ? extends Flag> flags();

		/**
		 * Return the Meta Parameter objects of this Meta Function.
		 * 
		 * Each parameter in the associated method has a MetaParameter object
		 * associated with it. This object derives its information from the @Description
		 * and @Paramater annotation as well as the method and its parameter
		 * types information.
		 * 
		 * Every parameter has a MetaParameter object, regardless if it has a @Description
		 * or @Parameter annotation.
		 * 
		 * @return The Meta Parameter objects, never {@code null}
		 */
		Collection< ? extends Option> options();

		/**
		 * Answer if this is mapped to a {@code vararg} method. A vararg method
		 * can be used to fill out the last parameters of a function with
		 * remaining arguments.
		 * 
		 * @return {@code true} if this is for a vararg method, otherwise
		 *         {@code false}
		 */
		Method method();
		
	}

	/**
	 * A Flag describes parameter in a method.
	 */
	public interface Flag {
		/**
		 * List the name of aliases for this named parameter. An alias must
		 * start with a minus sign ('-' \u002D) and must not be '--'. For
		 * example: {"-f", "--file"}
		 * 
		 * If the list of aliases can be empty, in that case this must be an
		 * unnamed parameter. Unnamed parameters can provide a default as well
		 * as a description for a parameter.
		 * 
		 * @return Array of aliases, never {@code null}.
		 */
		String[] aliases();
		
		/**
		 * Return a description of this parameter, if present. This information
		 * comes from the @Description annotation or resources.
		 * 
		 * @return A description or {@code null}
		 */
		String description();

		/**
		 * Return a summary of this parameter, if present. This information
		 * comes from the @Description annotation or resources.
		 * 
		 * @return A summary or {@code null}
		 */
		String summary();


		/**
		 * The value to use for a flag when it is used in the command line. This
		 * method is {@code Parameter.NOT_SET} for an option.
		 * 
		 * @return the parameter value for a flag.
		 */
		String value();

		/**
		 * The value to use when a named parameter has not been used or when an
		 * unnamed parameter can accept a default. If this method returns
		 * {@code Parameter.NOT_SET} then there is no default set. In that case,
		 * an unnamed parameter or an option is mandatory.
		 * 
		 * @return The value to use when the parameter is not used in the
		 *         command line, can be {@code null} or
		 *         {@code Parameter.NOT_SET}.
		 */
		String absent();

	}

	/**
	 * Information about options
	 */
	public interface Option {
		/**
		 * List the name of aliases for this named parameter. An alias must
		 * start with a minus sign ('-' \u002D) and must not be '--'. For
		 * example: {"-f", "--file"}
		 * 
		 * If the list of aliases can be empty, in that case this must be an
		 * unnamed parameter. Unnamed parameters can provide a default as well
		 * as a description for a parameter.
		 * 
		 * @return Array of aliases, never {@code null}.
		 */
		String[] aliases();
		
		/**
		 * Return a description of this parameter, if present. This information
		 * comes from the @Description annotation or resources.
		 * 
		 * @return A description or {@code null}
		 */
		String description();

		/**
		 * Return a summary of this parameter, if present. This information
		 * comes from the @Description annotation or resources.
		 * 
		 * @return A summary or {@code null}
		 */
		String summary();

		/**
		 * The value to use when a named parameter has not been used or when an
		 * unnamed parameter can accept a default. If this method returns
		 * {@code Parameter.NOT_SET} then there is no default set. In that case,
		 * an unnamed parameter or an option is mandatory.
		 * 
		 * @return The value to use when the parameter is not used in the
		 *         command line, can be {@code null} or
		 *         {@code Parameter.NOT_SET}.
		 */
		String absent();

		/**
		 * Answer if this option is can be used multiple times. If so, multiple
		 * arguments for the option are collection in a collection and converted
		 * to the parameter type, which must then be an array or collection.
		 * 
		 * This parameter must be an option for isRepeat to be true.
		 * 
		 * For an unnamed parameter or a parameter without a Parameter
		 * annotation this method always returns {@code false}.
		 * 
		 * @return {@code true} if this is an option and the repeat flag is set
		 *         in the Parameter annotation. Otherwise {@code false}
		 */
		boolean repeat();
	}
}
