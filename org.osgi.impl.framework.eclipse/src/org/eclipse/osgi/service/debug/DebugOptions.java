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
package org.eclipse.osgi.service.debug;

/**
 * Used to get debug options settings.
 */
public interface DebugOptions {

	/**
	 * Returns the identified option as a boolean value.  The specified
	 * defaultValue is returned if no such option is found.   Options are specified
	 * in the general form <i>&lt;Bundle-SymbolicName&gt;/&lt;option-path&gt;</i>.  
	 * For example, <code>org.eclipse.core.runtime/debug</code>
	 *
	 * @param option the name of the option to lookup
	 * @param defaultValue the value to return if no such option is found
	 * @return the value of the requested debug option or the
	 * defaultValue if no such option is found.
	 */
	public boolean getBooleanOption(String option, boolean defaultValue);

	/**
	 * Returns the identified option.  <code>null</code>
	 * is returned if no such option is found.   Options are specified
	 * in the general form <i>&lt;Bundle-SymbolicName&gt;/&lt;option-path&gt;</i>.  
	 * For example, <code>org.eclipse.core.runtime/debug</code>
	 *
	 * @param option the name of the option to lookup
	 * @return the value of the requested debug option or <code>null</code>
	 */
	public abstract String getOption(String option);

	/**
	 * Returns the identified option.  The specified defaultValue is 
	 * returned if no such option is found or if a NumberFormatException is thrown 
	 * while converting the option value to an integer.   Options are specified
	 * in the general form <i>&lt;Bundle-SymbolicName&gt;/&lt;option-path&gt;</i>.  
	 * For example, <code>org.eclipse.core.runtime/debug</code>
	 *
	 * @param option the name of the option to lookup
	 * @param defaultValue the value to return if no such option is found
	 * @return the value of the requested debug option or the
	 * defaultValue if no such option is found.
	 */
	public abstract String getOption(String option, String defaultValue);

	/**
	 * Returns the identified option as an int value.  The specified
	 * defaultValue is returned if no such option is found.   Options are specified
	 * in the general form <i>&lt;Bundle-SymbolicName&gt;/&lt;option-path&gt;</i>.  
	 * For example, <code>org.eclipse.core.runtime/debug</code>
	 *
	 * @param option the name of the option to lookup
	 * @param defaultValue the value to return if no such option is found
	 * @return the value of the requested debug option or the
	 * defaultValue if no such option is found.
	 */
	public abstract int getIntegerOption(String option, int defaultValue);

	/**
	 * Sets the identified option to the identified value.
	 * @param option the name of the option to set
	 * @param value the value of the option to set
	 */
	public abstract void setOption(String option, String value);
}
