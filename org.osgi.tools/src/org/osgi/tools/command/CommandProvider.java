package org.osgi.tools.command;

/**
	When an object wants to provide a number of commands
	to the console, it should register an object with this
	interface. Some console can then pick this up and execute
	command lines.
	<p>
	The interface contains only methods for the help and
	string translation. The console should use inspection
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
	public final static String NAME = "org.osgi.nursery.command.CommandProvider";
	
	/**
		Answer a string (maybe as many lines as you like) with help
		texts that explain the command.
	*/
	public String getHelp();
	
	/**
		Optional method for translation of objects. If null is returned,
		the value is ignored and another CommandProvider is used. Default
		translations are already provided for ServiceReference, ServiceRegistration,
		Bundle, Thread, Vector and arrays.
	*/
	public Object toString( Object o );
}

