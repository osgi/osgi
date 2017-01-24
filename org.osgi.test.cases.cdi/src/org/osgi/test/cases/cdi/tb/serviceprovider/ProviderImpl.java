package org.osgi.test.cases.cdi.tb.serviceprovider;

import org.osgi.service.cdi.annotations.Service;
import org.osgi.test.cases.tb.serviceapi.Provider;

@Service
public class ProviderImpl implements Provider {

	public String call() {
		return "test";
	}

}
