package org.osgi.test.cases.subsystem.app.service.dep.g;

import org.osgi.service.component.ComponentContext;

public class Component {
	@SuppressWarnings("unused")
	private void activate(ComponentContext ctxt) {
		System.out.println("***** DS component for bundle app.service.dep.g was activated *****: " + ctxt);
	}
	
	@SuppressWarnings("unused")
	private void deactivate() {
		System.out.println("***** DS component for bundle app.service.dep.g was deactivated *****");
	}
}
