package org.osgi.impl.service.async;

import java.util.concurrent.ExecutorService;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.async.Async;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class AsyncServiceFactory implements ServiceFactory<Async> {

	private final ExecutorService executor;
	
	private final ServiceTracker<LogService, LogService> logServiceTracker;
	
	public AsyncServiceFactory(ExecutorService executor, ServiceTracker<LogService, LogService> logServiceTracker) {
		this.logServiceTracker = logServiceTracker;
		this.executor = executor;
	}

	@Override
	public Async getService(Bundle bundle,
			ServiceRegistration<Async> registration) {
		
		return new AsyncService(bundle, executor, logServiceTracker);
	}

	@Override
	public void ungetService(Bundle bundle,
			ServiceRegistration<Async> registration, Async service) {
		// empty
	}

}
