package org.osgi.impl.service.upnp.cp.description;

public class Action {
	private String			name;
	private ArgumentList	args[];

	// This method returns the name of the action.
	public String getName() {
		return name;
	}

	// This method returns the arguments of the action.
	public ArgumentList[] getArguments() {
		if (args == null) {
			return new ArgumentList[0];
		}
		return args;
	}

	// This method sets the name for the action.
	public void setName(String nam) {
		name = nam;
	}

	// This method sets the argument lists for the action.
	public void setArgumentList(ArgumentList al[]) {
		args = al;
	}
}