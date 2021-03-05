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

package org.osgi.impl.service.resourcemonitoring.util;

/**
 * Reports an exception when trying to persist or restore ResourceMonitors.
 * 
 * @author mpcy8647
 * 
 */
public class PersistenceException extends Exception {

	/** generated */
	private static final long	serialVersionUID	= 7079982829353135283L;

	/**
	 * @param message
	 */
	public PersistenceException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param t
	 */
	public PersistenceException(String message, Throwable t) {
		super(message + " : " + t.getMessage());
	}
}
