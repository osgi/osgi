/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.defaultadaptor;

import java.io.*;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.eclipse.osgi.framework.log.FrameworkLogEntry;
import org.osgi.framework.FrameworkEvent;

/*
 * This is an empty implementation that does no actually logging.
 */
public class DefaultLog implements FrameworkLog {

	public DefaultLog() {
	}

	public void log(FrameworkEvent frameworkEvent) {
	}

	public void log(FrameworkLogEntry logEntry) {
	}

	public void setWriter(Writer newWriter, boolean append) {
	}

	public void setFile(File newFile, boolean append) throws IOException {
	}

	public File getFile() {
		return null;
	}

	public void setConsoleLog(boolean consoleLog) {
	}

	public void close() {
	}

}
