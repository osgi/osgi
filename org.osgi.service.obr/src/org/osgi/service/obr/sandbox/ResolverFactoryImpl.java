package org.osgi.service.obr.sandbox;

import java.util.LinkedList;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.service.obr.CapabilityProvider;
import org.osgi.service.obr.Part;
import org.osgi.service.obr.Resolver;
import org.osgi.service.obr.ResolverFactory;
import org.osgi.service.obr.admin.Repository;

public class ResolverFactoryImpl implements ResolverFactory {

	private final BundleContext bundleContext;
	
	private ServiceTracker tracker;

	public ResolverFactoryImpl(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		tracker = new ServiceTracker(bundleContext, CapabilityProvider.class.getName(), null);
	}

	void start() {
		tracker.open();
	}

	void stop() {
		tracker.close();
	}
	
	public Resolver newResolver(Map properties) {
		return new ResolverImpl(properties, getProviders(bundleContext));
	}

	public Resolver newResolver(Map properties, CapabilityProvider... providers) {
		return new ResolverImpl(properties, providers);
	}

	private CapabilityProvider[] getProviders(BundleContext bundleContext) {
		LinkedList<CapabilityProvider> providers = new LinkedList<CapabilityProvider>();
		providers.add(new LocalProvider(bundleContext));
		for (Object service : tracker.getServices()) {
			providers.add((CapabilityProvider) service);
		}
		return providers.toArray(new CapabilityProvider[providers.size()]);
	}


}
