package org.osgi.test.cases.subsystem.app.service.dep.d;

import org.osgi.test.cases.subsystem.app.service.dep.e.E1;
import org.osgi.test.cases.subsystem.app.service.dep.e.E2;

public class Bean {
	E1 e1 = null;
	E2 e2 = null;

	public void init() {
		System.out.println("***** Blueprint bean for bundle app.service.dep.e was activated *****");
	}
}
