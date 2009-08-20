package org.osgi.tools.command;

/**
	A command interpreter is a shell that can interpret command
	lines. This object is passed as parameter when a CommandProvider
	is invoked.
*/
public interface CommandInterpreter {
	public final static String NAME = "org.osgi.tools.command.CommandInterpreter";
	
	/**
		Get the next argument in the input.
		
		E.g. if the commandline is hello world, the _hello method
		will get "world" as the first argument.
	*/
	public String nextArgument();
	
	/**
		Get the current user if logged in.
		
		Can also be null
	*/
	public String getUser();
	
	/**
		Get a global variable.
		
		The command interpreter should keep an environment of
		variables relative to the user. The purpose of this
		is to allow a command to keep state between invocations.
		
		@param name			Name of the variable
	*/
	public Object getVariable(String name);
	
	/**
		Set a global variable.
		
		@param name			name of the variable
		@param value		value of the variable
	*/
	public void setVariable(String name, Object value);
	
	/**
		Execute a command line as if it came from the end user
		and return the result.
	*/
	public Object execute( String cmd );
	
	/**
		When an error is discovered in the execution, this
		method should be called to make an error object.
	*/
	public Object error( String message );
}

