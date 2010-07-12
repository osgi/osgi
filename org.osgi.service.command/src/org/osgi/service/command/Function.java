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

import java.util.List;

/**
 * A Function is a a block of code that can be executed with a set of arguments,
 * it returns the result object of executing the script.
 * 
 * The purpose of the Function is to be injected in commands. Many commands
 * require the possibility to execute closures or other commands. For example,
 * a {@code foreach} command requires a block for execution:
 * <pre>
 *   void foreach( Iterable<?> collection, Function block ) {
 *       for ( Object o : collection ) block.execute( Arrays.asList(o));
 * </pre>
 * 
 * Though the majority of application is when functions are closures, functions
 * can also be used as a target type for conversion.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface Function {
	/**
	 * Execute this function and return the result.
	 * 
	 * @param session The session in which to execute this function
	 * @param arguments The list of arguments. This list will not be modified.
	 * 
	 * @return the result from the execution.
	 * 
	 * @throws Exception if anything goes wrong
	 */
	Object execute(CommandSession session, List<?> arguments) throws Exception;
}
