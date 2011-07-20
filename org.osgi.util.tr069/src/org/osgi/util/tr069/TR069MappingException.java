/*
 * Copyright (c) OSGi Alliance (2010, 2011). All Rights Reserved.
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

package org.osgi.util.tr069;

/**
 * Exception which will be thrown if some trouble occurs for mapping.
 * 
 * @version $Id$
 */
public class TR069MappingException extends Exception {
	private static final long	serialVersionUID	= 3712320696439886391L;

	/**
	 * Construct an exception.
	 * 
	 * @param string error message.
	 */
	public TR069MappingException(String string) {
		super(string);
	}

	/**
	 * Construct an exception with a cause.
	 * 
	 * @param string error message.
	 * @param cause cause of the exception
	 */
	public TR069MappingException(String string, Throwable cause) {
		super(string, cause);
	}
}
