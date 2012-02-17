package org.osgi.test.cases.jmx.framework.tb2.impl;

import org.osgi.test.cases.jmx.framework.tb2.api.HelloSayer;

public class HelloSayerImpl implements HelloSayer {
	public String sayHello() {
		return "Hello " + SomeInternalClass.getSomeName();
	}
}
