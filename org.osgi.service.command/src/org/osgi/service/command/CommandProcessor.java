/*
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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
 * A Command Processor is a service that is registered by a script engine that
 * can execute commands.
 * 
 * A Command Processor is a factory for Command Session objects. The Command
 * Session maintains execution state and holds the console and keyboard streams.
 * A Command Processor must track any services that are registered with the
 * COMMAND_SCOPE and COMMAND_FUNCTION properties. The functions listed in the
 * COMMAND_FUNCTION property must be made available as functions in the script
 * language.
 * 
 * TODO The javadoc in this class need a good scrub before release.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface CommandProcessor {
	/**
	 * The scope of commands provided by this service. This name can be used to distinguish
	 * between different command providers with the same function names.
	 */
	String	COMMAND_SCOPE		= "osgi.command.scope";

	/**
	 * A String, array, or list of method names that may be called for this command provider. A
	 * name may end with a *, this will then be calculated from all declared public
	 * methods in this service.
	 * 
	 * Help information for the command may be supplied with a space as
	 * separation.
	 */
	String	COMMAND_FUNCTION	= "osgi.command.function";

	/**
	 * Create a new command session associated with IO streams.
	 * 
	 * The session is bound to the life cycle of the bundle getting this
	 * service. The session will be automatically closed when this bundle is
	 * stopped or the service is returned.
	 * 
	 * The shell will provide any available commands to this session and
	 * can set additional variables.
	 * 
	 * @param in
	 *            The value used for System.in
	 * @param out
	 *            The stream used for System.out
	 * @param err
	 *            The stream used for System.err
	 * @return A new session.
	 */
	CommandSession createSession(InputStream in, PrintStream out,
			PrintStream err);
}
