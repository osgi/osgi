/*
 * $Date: 2007-10-11 17:26:56 +0200 (Thu, 11 Oct 2007) $
 * 
 * Copyright (c) OSGi Alliance (2002, 2007). All Rights Reserved.
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
 * A Function is a a block of code that can be executed with a set of arguments,
 * it returns the result object of executing the script.
 * 
 * @version $Revision: 5021 $
 * @ThreadSafe
 */
public interface Function {
	/**
	 * Execute this function and return the result.
	 * 
	 * @return the result from the execution.
	 * 
	 * @throws Exception
	 *             if anything goes terribly wrong
	 */
	Object execute(CommandSession session, List arguments) throws Exception;
}
