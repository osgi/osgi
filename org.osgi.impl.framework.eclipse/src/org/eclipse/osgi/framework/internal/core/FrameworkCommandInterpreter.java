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

package org.eclipse.osgi.framework.internal.core;

import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.eclipse.osgi.util.NLS;

/**
 * This class knows how to parse and execute the command line arguments to the FrameworkConsole.
 * It attempts to pass the command to each registered CommandProvider until it finds one
 * that knows what to do with it.
 *
 * FrameworkCommandInterpreter provides support for the "More" command which allows the operator to configure
 * the number of lines to display before being prompted to continue.
 *
 * FrameworkCommandInterpreter provides several print methods which handle the "More" command.
 */
public class FrameworkCommandInterpreter implements CommandInterpreter {

	/** The command line in StringTokenizer form */
	private StringTokenizer tok;
	/** The active CommandProviders */
	private Object[] commandProviders;
	/** The FrameworkConsole */
	private FrameworkConsole con;
	/** The stream to send output to */
	private PrintWriter out;

	/** Strings used to format other strings */
	private String tab = "\t"; //$NON-NLS-1$
	private String newline = "\r\n"; //$NON-NLS-1$

	/**
	 * The maximum number of lines to print without user prompt.
	 * 0 means no user prompt is required, the window is scrollable.
	 */
	protected static int maxLineCount;

	/** The number of lines printed without user prompt.*/
	protected int currentLineCount;

	/**
	 *  The constructor.  It turns the cmdline string into a StringTokenizer and remembers
	 *  the input parms.
	 */
	public FrameworkCommandInterpreter(String cmdline, Object[] commandProviders, FrameworkConsole con) {
		tok = new StringTokenizer(cmdline);
		this.commandProviders = commandProviders;
		this.con = con;
		this.out = con.getWriter();
	}

	/**
		Get the next argument in the input.
	
		E.g. if the commandline is hello world, the _hello method
		will get "world" as the first argument.
	
	    @return A string containing the next argument on the command line
	*/
	public String nextArgument() {
		if (tok == null || !tok.hasMoreElements()) {
			return null;
		}
		String token = tok.nextToken();
		//check for quotes
		int index = token.indexOf('"');
		if (index != -1) {
			//if we only have one quote, find the second quote
			if (index == token.lastIndexOf('"')) {
				token += tok.nextToken("\""); //$NON-NLS-1$
			}
			StringBuffer buf = new StringBuffer(token);

			//strip quotes
			while (index != -1) {
				buf.deleteCharAt(index);
				token = buf.toString();
				index = token.indexOf('"');
			}
			return buf.toString();
		}
		return (token);
	}

	/**
		Execute a command line as if it came from the end user.
	
	    Searches the list of command providers using introspection until
	    it finds one that contains a matching method.  It searches for a method
	    with the name "_cmd" where cmd is the command to execute.  For example,
	    for a command of "launch" execute searches for a method called "_launch".
	
	    @param cmd The name of the command to execute.
	    @return The object returned by the method executed.
	*/
	public Object execute(String cmd) {
		resetLineCount();
		Object retval = null;
		// handle "more" command here
		if (cmd.equalsIgnoreCase("more")) { //$NON-NLS-1$
			try {
				_more();
			} catch (Exception e) {
				printStackTrace(e);
			}
			return retval;
		}
		// handle "disconnect" command here
		if (cmd.equalsIgnoreCase("disconnect") && con.getUseSocketStream()) { //$NON-NLS-1$
			try {
				_disconnect();
			} catch (Exception e) {
				printStackTrace(e);
			}
			return retval;
		}
		Class[] parameterTypes = new Class[] { CommandInterpreter.class };
		Object[] parameters = new Object[] { this };
		boolean executed = false;
		int size = commandProviders.length;
		for (int i = 0; !executed && (i < size); i++) {
			try {
				Object target = commandProviders[i];
				Method method = target.getClass().getMethod("_" + cmd, parameterTypes); //$NON-NLS-1$
				retval = method.invoke(target, parameters);
				executed = true; // stop after the command has been found
			} catch (NoSuchMethodException ite) {
				// keep going - maybe another command provider will be able to execute this command
			} catch (InvocationTargetException ite) {
				executed = true; // don't want to keep trying - we found the method but got an error
				printStackTrace(ite.getTargetException());
			} catch (Exception ee) {
				executed = true; // don't want to keep trying - we got an error we don't understand
				printStackTrace(ee);
			}
		}
		// if no command was found to execute, display help for all registered command providers
		if (!executed) {
			for (int i = 0; i < size; i++) {
				try {
					CommandProvider commandProvider = (CommandProvider) commandProviders[i];
					out.print(commandProvider.getHelp());
					out.flush();
				} catch (Exception ee) {
					printStackTrace(ee);
				}
			}
			// call help for the more command provided by this class
			out.print(getHelp());
			out.flush();
		}
		return retval;
	}

	/**
	 * Answers the number of lines output to the console
	 * window should scroll without user interaction.
	 *
	 * @return	The number of lines to scroll.
	 */
	private int getMaximumLinesToScroll() {
		return maxLineCount;
	}

	/**
	 * Sets the number of lines output to the console
	 * window will scroll without user interaction.
	 * <p>
	 * Note that this number does not include the line
	 * for the 'more' prompt itself.
	 * <p>
	 * If the number of lines is 0 then no 'more' prompt
	 * is disabled.
	 *
	 * @param	lines	the number of lines to scroll
	 */
	private void setMaximumLinesToScroll(int lines) {
		if (lines < 0) {
			throw new IllegalArgumentException(ConsoleMsg.CONSOLE_LINES_TO_SCROLL_NEGATIVE_ERROR); //$NON-NLS-1$
		}

		maxLineCount = lines;
	}

	/**
	 * Resets the line counter for the 'more' prompt.
	 */
	private void resetLineCount() {
		currentLineCount = 0;
	}

	/**
	 * Prints a string to the output medium (appended with newline character).
	 * <p>
	 * This method does not increment the line counter for the 'more' prompt.
	 *
	 * @param o the string to be printed
	 */
	private void printline(Object o) {
		print(o + newline);
	}

	/**
	 * Prints an object to the outputstream
	 *
	 * @param o	the object to be printed
	 */
	public void print(Object o) {
		synchronized (out) {
			check4More();
			out.print(o);
			out.flush();
		}
	}

	/**
	 * Prints a empty line to the outputstream
	 */
	public void println() {
		println(""); //$NON-NLS-1$
	}

	/**
	 * Print a stack trace including nested exceptions.
	 * @param t The offending exception
	 */
	public void printStackTrace(Throwable t) {
		t.printStackTrace(out);

		Method[] methods = t.getClass().getMethods();

		int size = methods.length;
		Class throwable = Throwable.class;

		for (int i = 0; i < size; i++) {
			Method method = methods[i];

			if (Modifier.isPublic(method.getModifiers()) && method.getName().startsWith("get") && throwable.isAssignableFrom(method.getReturnType()) && (method.getParameterTypes().length == 0)) { //$NON-NLS-1$
				try {
					Throwable nested = (Throwable) method.invoke(t, null);

					if ((nested != null) && (nested != t)) {
						out.println(ConsoleMsg.CONSOLE_NESTED_EXCEPTION); 
						printStackTrace(nested);
					}
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				}
			}
		}
	}

	/**
	 * Prints an object to the output medium (appended with newline character).
	 * <p>
	 * If running on the target environment, the user is prompted with '--more'
	 * if more than the configured number of lines have been printed without user prompt.
	 * This enables the user of the program to have control over scrolling.
	 * <p>
	 * For this to work properly you should not embed "\n" etc. into the string.
	 *
	 * @param	o	the object to be printed
	 */
	public void println(Object o) {
		if (o == null) {
			return;
		}
		synchronized (out) {
			check4More();
			printline(o);
			currentLineCount++;
			currentLineCount += o.toString().length() / 80;
		}
	}

	/**
	 * Prints the given dictionary sorted by keys.
	 *
	 * @param dic	the dictionary to print
	 * @param title	the header to print above the key/value pairs
	 */
	public void printDictionary(Dictionary dic, String title) {
		if (dic == null)
			return;

		int count = dic.size();
		String[] keys = new String[count];
		Enumeration keysEnum = dic.keys();
		int i = 0;
		while (keysEnum.hasMoreElements()) {
			keys[i++] = (String) keysEnum.nextElement();
		}
		Util.sort(keys);

		if (title != null) {
			println(title);
		}
		for (i = 0; i < count; i++) {
			println(" " + keys[i] + " = " + dic.get(keys[i]));  //$NON-NLS-1$//$NON-NLS-2$
		}
		println();
	}

	/**
	 * Prints the given bundle resource if it exists
	 *
	 * @param bundle	the bundle containing the resource
	 * @param resource	the resource to print
	 */
	public void printBundleResource(AbstractBundle bundle, String resource) {
		URL entry = null;
		entry = bundle.getEntry(resource);
		if (entry != null) {
			try {
				println(resource);
				InputStream in = entry.openStream();
				byte[] buffer = new byte[1024];
				int read = 0;
				try {
					while ((read = in.read(buffer)) != -1)
						print(new String(buffer, 0, read));
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
						}
					}
				}
			} catch (Exception e) {
				System.err.println(NLS.bind(ConsoleMsg.CONSOLE_ERROR_READING_RESOURCE, resource));
			}
		} else {
			println(NLS.bind(ConsoleMsg.CONSOLE_RESOURCE_NOT_IN_BUNDLE, resource, bundle.toString())); //$NON-NLS-1$
		}
	}

	/**
	 *  Displays the more... prompt if the max line count has been reached 
	 *  and waits for the operator to hit enter.
	 *
	 */
	private void check4More() {
		int max = getMaximumLinesToScroll();
		if (max > 0) {
			if (currentLineCount >= max) {
				out.print(ConsoleMsg.CONSOLE_MORE); 
				out.flush();
				con.getInput(); // wait for user entry
				resetLineCount(); //Reset the line counter for the 'more' prompt
			}
		}
	}

	/**
		Answer a string (may be as many lines as you like) with help
		texts that explain the command.
	*/
	public String getHelp() {
		StringBuffer help = new StringBuffer(256);
		help.append(newline);
		help.append(ConsoleMsg.CONSOLE_HELP_CONTROLLING_CONSOLE_HEADING); 
		help.append(newline);
		help.append(tab);
		help.append("more - "); //$NON-NLS-1$
		help.append(ConsoleMsg.CONSOLE_HELP_MORE); 
		if (con.getUseSocketStream()) {
			help.append(newline);
			help.append(tab);
			help.append("disconnect - "); //$NON-NLS-1$
			help.append(ConsoleMsg.CONSOLE_HELP_DISCONNECT); 
		}
		return help.toString();
	}

	/**
	 * Toggles the use of the more prompt for displayed output.
	 *
	 */
	public void _more() throws Exception {
		if (confirm(ConsoleMsg.CONSOLE_CONFIRM_MORE, true)) { 
			int lines = prompt(newline + ConsoleMsg.CONSOLE_MORE_ENTER_LINES, 24);
			setMaximumLinesToScroll(lines);
		} else {
			setMaximumLinesToScroll(0);
		}
	}

	private void _disconnect() throws Exception {
		if (confirm(ConsoleMsg.CONSOLE_CONFIRM_DISCONNECT, true)) {
			con.disconnect();
		}
	}

	/**
	 * Prompts the user for confirmation.
	 *
	 * @param	string			the message to present to the user to confirm
	 * @param	defaultAnswer	the default result
	 *
	 * @return	<code>true</code> if the user confirms; <code>false</code> otherwise.
	 */
	protected boolean confirm(String string, boolean defaultAnswer) {
		synchronized (out) {
			if (string.length() > 0) {
				print(string);
			} else {
				print(ConsoleMsg.CONSOLE_CONFIRM);
			}
			print(" (" + ConsoleMsg.CONSOLE_CONFIRM_VALUES); //$NON-NLS-1$
			if (defaultAnswer) {
				print(ConsoleMsg.CONSOLE_Y + ") ");  //$NON-NLS-1$
			} else {
				print(ConsoleMsg.CONSOLE_N + ") "); //$NON-NLS-1$
			}
		}
		String input = con.getInput();
		resetLineCount();
		if (input.length() == 0) {
			return defaultAnswer;
		}
		return input.toLowerCase().charAt(0) == ConsoleMsg.CONSOLE_Y.charAt(0);
	}

	/**
	 * Prompts the user for input from the input medium providing a default value.
	 *
	 * @param	string			the message to present to the user
	 * @param	defaultAnswer	the string to use as a default return value
	 *
	 * @return	The user provided string or the defaultAnswer,
	 *			if user provided string was empty.
	 */
	protected String prompt(String string, String defaultAnswer) {
		if (string.length() > 0) {
			if (defaultAnswer.length() > 0) {
				StringBuffer buf = new StringBuffer(256);
				buf.append(string);
				buf.append(" "); //$NON-NLS-1$
				buf.append(ConsoleMsg.CONSOLE_PROMPT_DEFAULT);
				buf.append("="); //$NON-NLS-1$
				buf.append(defaultAnswer);
				buf.append(") "); //$NON-NLS-1$
				print(buf.toString());
			} else {
				print(string);
			}
		}
		String input = con.getInput();
		resetLineCount();
		if (input.length() > 0) {
			return input;
		}
		return defaultAnswer;
	}

	/**
	 * Prompts the user for input of a positive integer.
	 *
	 * @param	string			the message to present to the user
	 * @param	defaultAnswer	the integer to use as a default return value
	 *
	 * @return	The user provided integer or the defaultAnswer,
	 *			if user provided an empty input.
	 */
	protected int prompt(String string, int defaultAnswer) {
		Integer i = new Integer(defaultAnswer);
		int answer;
		for (int j = 0; j < 3; j++) {
			String s = prompt(string, i.toString());
			try {
				answer = Integer.parseInt(s);
				if (answer >= 0) {
					return answer;
				}
			} catch (NumberFormatException e) {
			}
			println(ConsoleMsg.CONSOLE_INVALID_INPUT); 
		}
		println(ConsoleMsg.CONSOLE_TOO_MUCH_INVALID_INPUT);
		return defaultAnswer;
	}
}
