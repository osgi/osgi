/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.adaptor;

/**
 * Exception class to denote that the resource is in an imported package
 * but the resource was not found! This is thrown by ImportClassLoader
 * and caught by BundleClassLoader in the getResource* methods after
 * calling parent.getResource*().
 *
 */
public class ImportResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 3977584697024721712L;

	/**
	 * Constructor with no detail message
	 */
	public ImportResourceNotFoundException() {
		super();
	}

	/**
	 * Constructor with detail message
	 *
	 * @param   s   the detail message.
	 */
	public ImportResourceNotFoundException(String s) {
		super(s);
	}
}
