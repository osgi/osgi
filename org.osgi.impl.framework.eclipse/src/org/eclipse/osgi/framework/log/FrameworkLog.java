/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.log;

import java.io.*;
import org.osgi.framework.FrameworkEvent;

/**
 * The FramworkLog interface.  A FrameworkLog implementation is provided by the
 * FrameworkAdaptor and used by the Framework to log any error messages and
 * FrameworkEvents of type ERROR.  The FrameworkLog may persist the log messages 
 * to the filesystem or allow other ways of accessing the log information.
 * @since 3.1
 */
public interface FrameworkLog {
	/**
	 * A service lookup constant (value "performance") indicating an 
	 * implementation of the logging service that logs performance events. 
	 * Create a filter with this property set to <code>"true"</code> in order to 
	 * obtain a performance log.
	 * 
	 * @since 3.1
	 */
	public static final String SERVICE_PERFORMANCE = "performance"; //$NON-NLS-1$

	/**
	 * Logs the information from a FrameworkEvent to the FrameworkLog.
	 * @param frameworkEvent The FrameworkEvent to log.
	 */
	public void log(FrameworkEvent frameworkEvent);

	/**
	 * Logs the FrameworkLogEntry to the FrameworkLog
	 * @param logEntry The entry to log.
	 */
	public void log(FrameworkLogEntry logEntry);

	/**
	 * Sets the current Writer used to log messages to the specified
	 * newWriter.  If append is set to true then the content
	 * of the current Writer will be appended to the new Writer 
	 * if possible.
	 * @param newWriter The Writer to use for logging messages. 
	 * @param append Indicates whether the content of the current Writer
	 * used for logging messages should be appended to the end of the new 
	 * Writer.
	 */
	public void setWriter(Writer newWriter, boolean append);

	/** 
	 * Sets the current File used to log messages to a FileWriter
	 * using the specified File.  If append is set to true then the 
	 * content of the current Writer will be appended to the 
	 * new File if possible.
	 * @param newFile The File to create a new FileWriter which will be
	 * used for logging messages.
	 * @param append Indicates whether the content of the current Writer
	 * used for logging messages should be appended to the end of the new 
	 * File.
	 * @throws IOException if any problem occurs while constructing a
	 * FileWriter from the newFile.  If this exception is thrown the 
	 * FrameworkLog will not be effected and will continue to use the 
	 * current Writer to log messages.
	 */
	public void setFile(File newFile, boolean append) throws IOException;

	/**
	 * Returns the log File if it is set, otherwise null is returned.
	 * @return the log File if it is set, otherwise null is returned.
	 */
	public File getFile();

	/**
	 * Sets the console log option.  If this is set then all logs will be
	 * logged to System.out as well as the current Writer.
	 * @param consoleLog indicates whether to log to System.out
	 */
	public void setConsoleLog(boolean consoleLog);

	/**
	 * Closes the FrameworkLog.  After the FrameworkLog is closed messages may
	 * no longer be logged to it.
	 */
	public void close();
}
