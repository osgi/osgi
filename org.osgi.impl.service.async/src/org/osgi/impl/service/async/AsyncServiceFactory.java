package org.osgi.impl.service.async;

import java.util.concurrent.ExecutorService;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.async.Async;

public class AsyncServiceFactory implements ServiceFactory<Async> {

	private final ExecutorService executor;
	
	public AsyncServiceFactory(ExecutorService executor) {
		super();
		this.executor = executor;
	}

	@Override
	public Async getService(Bundle bundle,
			ServiceRegistration<Async> registration) {
		
		return new AsyncService(bundle, executor);
	}

	@Override
	public void ungetService(Bundle bundle,
			ServiceRegistration<Async> registration, Async service) {}

}
