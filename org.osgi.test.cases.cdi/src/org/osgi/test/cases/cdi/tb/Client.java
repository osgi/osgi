package org.osgi.test.cases.cdi.tb;

import java.util.Iterator;
import java.util.concurrent.Callable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.test.cases.cdi.serviceapi.Foo;

@ApplicationScoped
@Service
public class Client implements Callable<String> {
	@Inject
	@Reference
	Instance<Foo> foo;

	public String call() throws Exception {
		Iterator<Foo> iterator = foo.iterator();
		return iterator.hasNext() ? iterator.next().doFoo() : null;
	}
}
