package org.osgi.test.cases.cdi.tb;

import java.util.concurrent.Callable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.test.cases.cdi.serviceapi.Foo;

@ApplicationScoped
@Service
public class Client implements Callable<String> {
	@Inject
	@Reference
	Foo foo;

	public String call() throws Exception {
		return foo.doFoo();
	}
}
