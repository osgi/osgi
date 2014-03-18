package org.osgi.impl.service.async;

import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.async.Async;

public class Activator implements BundleActivator {
	
	private final ExecutorService executor = Executors.newFixedThreadPool(10, new ThreadFactory() {
		
		private final AtomicInteger count = new AtomicInteger();
		
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, "Asynchronous Execution Service Thread " + count.incrementAndGet());
			return t;
		}
	});
	
	public void start(BundleContext context) throws Exception {
		context.registerService(Async.class.getName(), new AsyncServiceFactory(executor), new Hashtable<String, Object>());
	}

	public void stop(BundleContext context) throws Exception {
		executor.shutdownNow();
	}
}
