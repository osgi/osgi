package org.osgi.test.cases.subsystem.app.service.dep.h;

public class H1impl implements H1 {
	public void init() {
		System.out.println("***** Blueprint bean H1 for bundle app.service.dep.h was activated *****");
	}
}
