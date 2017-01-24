package org.osgi.test.cases.cdi.serviceclient.transitive;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.Service;

@Service
public class Client implements Callable<String> {
	@Inject
	@Reference(target = "(name=intermediate)")
	Callable<String> callable;

	public String call() throws Exception {
		return callable.call();
	}
}
