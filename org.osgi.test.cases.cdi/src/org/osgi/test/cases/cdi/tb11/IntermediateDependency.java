package org.osgi.test.cases.cdi.tb11;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.osgi.service.cdi.annotations.ComponentScoped;
import org.osgi.service.cdi.annotations.Reference;
import org.osgi.test.cases.cdi.serviceapi.Foo;

@ComponentScoped
public class IntermediateDependency implements Callable<String> {

	@Inject
	@Reference
	Foo foo;

	public String call() throws Exception {
		return foo.doFoo();
	}
}
