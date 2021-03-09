/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.resourcemonitoring;

/**
 * Resource Context Exception.
 * 
 * @version 1.0
 * @author $Id$
 */
public class ResourceContextException extends Exception {

	/** generated */
	private static final long	serialVersionUID	= -7657961201425163167L;

	/**
	 * Create a new ResourceContextException
	 * 
	 * @param msg message
	 */
	public ResourceContextException(String msg) {
		super(msg);
	}

	/**
	 * Create a new ResourceContextException
	 * 
	 * @param msg message
	 * @param t exception
	 */
	public ResourceContextException(String msg, Throwable t) {
		super(msg, t);
	}

}
