package org.osgi.test.cases.upnp.tbc.export;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;

/**
 * 
 * 
 */
public abstract class TestService implements UPnPService {
	private final UPnPAction[]			actions;
	private final UPnPStateVariable[]	variables;
	private final Map					acts;
	private final Map					vars;

	public TestService(UPnPAction[] actions, UPnPStateVariable[] variables) {
		if (variables == null || variables.length < 1) {
			throw new IllegalArgumentException(
					"UPnPService must have at least one state variable");
		}
		acts = new HashMap();
		vars = new HashMap();
		if (actions != null) {
			for (int i = 0; i < actions.length; i++) {
				acts.put(actions[i].getName(), actions[i]);
			}
		}
		for (int j = 0; j < variables.length; j++) {
			vars.put(variables[j].getName(), variables[j]);
		}
		this.actions = ((actions == null) ? null : (UPnPAction[]) actions
				.clone());
		this.variables = (UPnPStateVariable[]) variables.clone();
	}

	public abstract String getId();

	public abstract String getType();

	public abstract String getVersion();

	public final UPnPAction getAction(String name) {
		if (acts != null) {
			return (UPnPAction) acts.get(name);
		}
		else {
			if (actions != null) {
				for (int i = 0; i < actions.length; i++) {
					if (actions[i].getName().equals(name)) {
						return actions[i];
					}
				}
			}
			return null;
		}
	}

	public final UPnPAction[] getActions() {
		if (actions == null) {
			return null;
		}
		return (UPnPAction[]) actions.clone();
	}

	public final UPnPStateVariable[] getStateVariables() {
		return (UPnPStateVariable[]) variables.clone();
	}

	public final UPnPStateVariable getStateVariable(String name) {
		if (vars != null) {
			return (UPnPStateVariable) vars.get(name);
		}
		else {
			for (int i = 0; i < variables.length; i++) {
				if (variables[i].getName().equals(name)) {
					return variables[i];
				}
			}
			return null;
		}
	}

}