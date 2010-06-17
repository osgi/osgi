package org.osgi.service.obr.sandbox;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class ResolverFactoryServiceFactory implements ServiceFactory {

	public Object getService(Bundle bundle, ServiceRegistration registration) {
		return new ResolverFactoryImpl(bundle.getBundleContext());
	}

	public void ungetService(Bundle bundle, ServiceRegistration registration,
			Object service) {
		// TODO Auto-generated method stub
		
	}

}
