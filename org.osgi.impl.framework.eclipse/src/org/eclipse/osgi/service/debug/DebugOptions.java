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

public interface DebugOptions {
	public boolean getBooleanOption(String option, boolean defaultValue);

	public abstract String getOption(String option);

	public abstract String getOption(String option, String defaultValue);

	public abstract int getIntegerOption(String option, int defaultValue);

	public abstract void setOption(String option, String value);
}
