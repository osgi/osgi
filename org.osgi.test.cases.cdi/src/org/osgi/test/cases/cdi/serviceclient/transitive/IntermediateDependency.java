package org.osgi.test.cases.cdi.serviceclient.transitive;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.service.cdi.annotations.ServiceProperty;
import org.osgi.test.cases.tb.serviceapi.Provider;

@Service(properties = { @ServiceProperty(key = "name", value = "intermediate") })
public class IntermediateDependency implements Callable<String> {

	@Inject
	@Reference
	Provider provider;

	public String call() throws Exception {
		return provider.call();
	}
}
