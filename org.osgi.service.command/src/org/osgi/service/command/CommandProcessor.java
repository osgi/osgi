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

import java.io.InputStream;
import java.io.PrintStream;

/**
 * A Command Processor is a service that is registered by a TSH script engine
 * that can execute commands.
 * 
 * A Command Processor is a factory for Command Session objects. The Command
 * Session maintains execution state and holds the console and keyboard streams.
 * A Command Processor must track any services that are registered with the
 * {@link #COMMAND_SCOPE} and {@link #COMMAND_FUNCTION} properties. The
 * functions listed in the {@link #COMMAND_FUNCTION} property must be made
 * available as functions in the TSH script language.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface CommandProcessor {
	/**
	 * The scope of commands provided by this service. This name can be used to
	 * distinguish between different command providers with the same function
	 * names. Commands can be executed as &lt;scope&gt;:&lt;function&gt;.
	 */
	String	COMMAND_SCOPE		= "osgi.command.scope";

	/**
	 * A {@code String+} of function names that may be called for this command
	 * provider. A name may end with a *, this will then be calculated from all
	 * declared public methods in this service.
	 * 
	 * TODO verify the * is true?
	 * 
	 */
	String	COMMAND_FUNCTION	= "osgi.command.function";

	/**
	 * A description of the command scope.
	 */
	String	COMMAND_DESCRIPTION	= "osgi.command.description";

	/**
	 * Create a new command session associated with IO streams.
	 * 
	 * The session is bound to the life cycle of the bundle getting this
	 * service. The session will be automatically closed when this bundle is
	 * stopped or the service is returned.
	 * 
	 * The shell will provide any available commands to this session and can set
	 * additional variables that will be local to this session.
	 * 
	 * @param in The value used for System.in. If {@code null} is passed, the
	 *        implementation must create a valid Input Stream that always
	 *        returns end of file.
	 * @param out The stream used for System.out, must not be {@code null}
	 * @param err The stream used for System.err, must not be {@code null}
	 * @return A new session.
	 */
	CommandSession createSession(InputStream in, PrintStream out,
			PrintStream err);

	/**
	 * Create a new Command Session that is associated with a {@link Terminal}.
	 * 
	 * A Terminal provides the common streams but adds extra capabilities for
	 * commands to control the screen. A session maintains this Terminal under
	 * the variable .terminal and can automatically inject a Terminal if needed
	 * in a method call.
	 * 
	 * @param terminal The terminal to use in this session
	 * @return A new sessions
	 */
	CommandSession createSession(Terminal terminal);

}
