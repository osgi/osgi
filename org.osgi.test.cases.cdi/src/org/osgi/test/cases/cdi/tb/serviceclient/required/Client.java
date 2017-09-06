package org.osgi.test.cases.cdi.tb.serviceclient.required;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Component;
import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.ServiceScope;
import org.osgi.test.cases.tb.serviceapi.Provider;

@Component(scope = ServiceScope.BUNDLE)
public class Client implements Callable<String> {
	@Inject
	@Reference
	Provider provider;

	public String call() throws Exception {
		return provider.call();
	}
}
