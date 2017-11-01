package org.osgi.test.cases.cdi.serviceclient.transitive;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Callable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Qualifier;

import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.test.cases.tb.serviceapi.Provider;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@interface Name {
	String value();
}

@ApplicationScoped
@Name("intermediate")
@Service
public class IntermediateDependency implements Callable<String> {

	@Inject
	@Reference
	Provider provider;

	public String call() throws Exception {
		return provider.call();
	}
}
