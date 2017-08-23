package org.osgi.test.cases.cdi.tb.serviceprovider;

import org.osgi.service.cdi.annotations.Component;
import org.osgi.test.cases.tb.serviceapi.Provider;

@Component
public class ProviderImpl implements Provider {

	public String call() {
		return "test";
	}

}
