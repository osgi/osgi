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

package org.eclipse.osgi.framework.console;

/**
 When an object wants to provide a number of commands
 to the console, it should register an object with this
 interface. Some console can then pick this up and execute
 command lines.
 The SERVICE_RANKING registration property can be used to influence the
 order that a CommandProvider gets called.  Specify a value less than
 Integer.MAXVALUE, where higher is more significant.  The default value
 if SERVICE_RANKING is not set is 0.
 <p>
 The interface contains only methods for the help.
 The console should use inspection
 to find the commands. All public commands, starting with
 a '_' and taking a CommandInterpreter as parameter
 will be found. E.g.
 <pre>
 public Object _hello( CommandInterpreter intp ) {
 return "hello " + intp.nextArgument();
 }
 </pre>
 */
public interface CommandProvider {
	/**
	 Answer a string (may be as many lines as you like) with help
	 texts that explain the command.
	 */
	public String getHelp();

}
