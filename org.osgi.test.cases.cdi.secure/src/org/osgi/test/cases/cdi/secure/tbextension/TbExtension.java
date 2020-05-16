package org.osgi.test.cases.cdi.secure.tbextension;

import java.time.Instant;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.util.AnnotationLiteral;

import org.osgi.annotation.bundle.Capability;
import org.osgi.annotation.bundle.Header;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.support.dictionary.Dictionaries;
import org.osgi.test.support.map.Maps;

@Capability(namespace = "osgi.cdi.extension", name = "tb.extension")
@Header(name = Constants.BUNDLE_ACTIVATOR, value = "${@class}")
public class TbExtension implements BundleActivator, Extension,
		PrototypeServiceFactory<Extension> {

	private ServiceRegistration<Extension> registration;

	@SuppressWarnings("serial")
	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
		final Instant whenExtensionCalled = Instant.now();

		abd.addBean()
				.addType(Instant.class)
				.addQualifier(new AnnotationLiteral<TBExtensionCalled>() {
				})
				.produceWith(i -> whenExtensionCalled);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		registration = context.registerService(Extension.class,
				(PrototypeServiceFactory<Extension>) this, Dictionaries
						.asDictionary(
						Maps.mapOf("osgi.cdi.extension", "tb.extension")));
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (registration != null) {
			registration.unregister();
		}

	}

	public Extension getService(Bundle bundle,
			ServiceRegistration<Extension> registration) {
		return this;
	}

	public void ungetService(Bundle bundle,
			ServiceRegistration<Extension> registration, Extension service) {
	}

}
