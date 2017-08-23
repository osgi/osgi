package org.osgi.test.cases.cdi.serviceclient;

import java.util.Iterator;
import java.util.concurrent.Callable;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Component;
import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.ServicePolicy;
import org.osgi.test.cases.tb.serviceapi.Provider;

@Component(servicePolicy = ServicePolicy.DERIVED)
public class Client implements Callable<String> {
	@Inject
	@Reference
	Instance<Provider> provider;

	public String call() throws Exception {
		Iterator<Provider> iterator = provider.iterator();
		return iterator.hasNext() ? iterator.next().call() : null;
	}
}
