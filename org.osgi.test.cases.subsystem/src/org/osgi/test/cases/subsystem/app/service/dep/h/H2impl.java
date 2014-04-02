package org.osgi.test.cases.subsystem.app.service.dep.h;

public class H2impl implements H2 {
	public void init() {
		System.out.println("***** Blueprint bean H2 for bundle app.service.dep.h was activated *****");
	}
}
