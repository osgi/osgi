package org.osgi.test.cases.cdi.secure.tbextension;

import java.time.Instant;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.util.AnnotationLiteral;

import org.osgi.annotation.bundle.Capability;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(property = {
		"osgi.cdi.extension=tb.extension"
}, scope = ServiceScope.PROTOTYPE)
@Capability(namespace = "osgi.cdi.extension", name = "tb.extension")
public class TbExtension implements Extension {

	@SuppressWarnings("serial")
	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
		final Instant whenExtensionCalled = Instant.now();

		abd.addBean()
				.addType(Instant.class)
				.addQualifier(new AnnotationLiteral<TBExtensionCalled>() {
				})
				.produceWith(i -> whenExtensionCalled);
	}
}
