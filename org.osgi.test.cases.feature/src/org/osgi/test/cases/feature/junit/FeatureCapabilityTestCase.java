package org.osgi.test.cases.feature.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.osgi.namespace.service.ServiceNamespace.*;
import static org.osgi.resource.Namespace.CAPABILITY_USES_DIRECTIVE;

import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class FeatureCapabilityTestCase {

	@InjectBundleContext
	BundleContext ctx;

	@ParameterizedTest
	@ValueSource(classes = {
			org.osgi.service.feature.FeatureService.class
	})
	public void test_service_capability(Class< ? > clz) {

		String className = clz.getName();

		ServiceReference< ? > ref = ctx.getServiceReference(clz);

		assertThat(ref).isNotNull();

		BundleWiring wiring = ref.getBundle().adapt(BundleWiring.class);

		@SuppressWarnings("unchecked")
		BundleCapability cap = wiring
				.getCapabilities(SERVICE_NAMESPACE)
				.stream()
				.filter(c -> ((List<String>) c.getAttributes()
						.get(CAPABILITY_OBJECTCLASS_ATTRIBUTE))
								.contains(className))
				.findFirst()
				.orElse(null);

		assertThat(cap).isNotNull();

		assertThat(cap.getDirectives().get(CAPABILITY_USES_DIRECTIVE))
				.isEqualTo(clz.getPackage().getName());
	}
}
