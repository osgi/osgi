package org.osgi.test.cases.cdi.tb9;

import static org.osgi.service.cdi.ServiceInstanceType.BUNDLE;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.service.cdi.annotations.ServiceInstance;
import org.osgi.service.cdi.annotations.SingleComponent;
import org.osgi.test.cases.cdi.serviceapi.Foo;

@SingleComponent
@Service
@ServiceInstance(BUNDLE)
public class Client implements Callable<String> {
	@Inject
	@Reference
	Foo foo;

	public String call() throws Exception {
		return foo.doFoo();
	}
}
