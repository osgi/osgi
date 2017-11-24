package org.osgi.test.cases.cdi.tb9;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Bundle;
import org.osgi.service.cdi.annotations.Component;
import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.test.cases.cdi.serviceapi.Foo;

@Component
@Service
@Bundle
public class Client implements Callable<String> {
	@Inject
	@Reference
	Foo foo;

	public String call() throws Exception {
		return foo.doFoo();
	}
}
