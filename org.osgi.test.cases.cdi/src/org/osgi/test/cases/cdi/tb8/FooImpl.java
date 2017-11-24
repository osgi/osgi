package org.osgi.test.cases.cdi.tb8;

import org.osgi.service.cdi.annotations.Component;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.test.cases.cdi.serviceapi.Foo;

@Component
@Service
public class FooImpl implements Foo {

	public String doFoo() {
		return "test";
	}

}
