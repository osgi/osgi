package org.osgi.test.cases.cdi.tb8;

import org.osgi.service.cdi.annotations.SingleComponent;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.test.cases.cdi.serviceapi.Foo;

@SingleComponent
@Service
public class FooImpl implements Foo {

	public String doFoo() {
		return "test";
	}

}
