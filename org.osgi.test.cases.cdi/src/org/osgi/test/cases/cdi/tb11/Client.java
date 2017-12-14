package org.osgi.test.cases.cdi.tb11;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.osgi.service.cdi.annotations.SingleComponent;

@SingleComponent
public class Client implements Callable<String> {
	@Inject
	IntermediateDependency callable;

	public String call() throws Exception {
		return callable.call();
	}
}
