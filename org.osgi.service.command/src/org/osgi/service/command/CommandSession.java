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
package org.osgi.service.command;

import java.net.*;
import java.util.*;

/**
 * A Command Session holds the executable state of a script engine as well as a
 * {@link Terminal} that is connected to that session.
 * 
 * @NotThreadSafe
 * @version $Id$
 * 
 * @ProviderType
 */
public interface CommandSession {
	/**
	 * Execute a program in this session.
	 * 
	 * @param commandline A Command line according to the script syntax.
	 * @return the result of the execution
	 * @throws ParsingException If the text contained a syntax error
	 * @throws Exception if something fails
	 */
	Object execute(CharSequence commandline) throws Exception, ParsingException;

	/**
	 * Close this command session. After the session is closed, it will throw
	 * IllegalStateException when it is used.
	 */
	void close();

	/**
	 * Return the Terminal associated with this session. The terminal is
	 * sometimes necessary to communicate directly to the end user. For example,
	 * a "less" or "more" command needs direct input from the keyboard to
	 * control the paging.
	 * 
	 * @return Terminal used closest to the user, never {@code null}.
	 */
	Terminal getTerminal();

	/**
	 * Return the modifiable Map used to store the local session variables.
	 * 
	 * The returned Map can be modified to add/remove new variables, these
	 * changes are visible to the scripts running in this session.
	 * 
	 * @return the map with all the variables
	 */
	Map<String, Object> getSessionVariables();

	/**
	 * Return the Map used to maintain the global variables.
	 * 
	 * The returned map can be modified to add/remove new variables, it is
	 * thread safe.
	 * 
	 * @return the Map with all the global variables
	 * 
	 * @ThreadSafe
	 */
	Map<String, Object> getGlobalVariables();

	/**
	 * Return the current list of Meta Scope objects. A scope represents a set
	 * of commands under a common name. The purpose of this information is to
	 * simplify command completion and providing extensive help about commands.
	 * If an implementation supports annotations it can use the annotations to
	 * provide extra information.
	 * 
	 * @return A unmodifiable collection of {@link Meta.Scope} objects.
	 */
	Collection<Meta.Scope> getMetaScopes();

	/**
	 * Resolve a local name to a URI to the current working directory. If the
	 * name is absolute, an absolute URI is returned.
	 * 
	 * @param path A relative or absolute URI
	 * @return a URI that is the original when it was absolute and resolved
	 *         against the $CWD variable if relative.
	 */
	URI resolve(String path);

}
