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

import java.util.*;

/**
 * A Command Session holds the executable state of a script engine as well as a
 * {@link Terminal} that is connected to that session.
 * 
 * A Command Session is not thread safe and should not be used from different
 * threads at the same time.
 * 
 * @NotThreadSafe
 * @version $Id$
 */
public interface CommandSession {
	/**
	 * Execute a program in this session.
	 * 
	 * @param commandline A Command line according to the TSH syntax.
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
	 * Return the input stream that is the first of the pipeline. This stream is
	 * sometimes necessary to communicate directly to the end user. For example,
	 * a "less" or "more" command needs direct input from the keyboard to
	 * control the paging.
	 * 
	 * @return InputStream used closest to the user or (@code null} if input is
	 *         from a file.
	 */
	Terminal getTerminal();

	/**
	 * Return the map used to store the session variables.
	 * 
	 * The returned map can be modified to add/remove new variables.
	 * 
	 * @return the map with all the variables
	 */
	Map<String, Object> getSessionVariables();

	/**
	 * Return the map used to store the global variables.
	 * 
	 * The returned map can be modified to add/remove new variables.
	 * 
	 * @return the map with all the variables
	 */
	Map<String, Object> getGlobalVariables();

	/**
	 * Return the current list of scopes. A scope represents a command and its
	 * sub-commands. The purpose of this information is to simplify command
	 * completion and providing extensive help about commands. If an implementation
	 * supports annotations it can use the annotations to provide extra information.
	 * 
	 * @return A unmodifiable collection of {@link MetaScope} objects. 
	 */

	Collection<MetaScope> getMetaScopes();

}
