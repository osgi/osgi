package org.osgi.test.cases.jakartaws.junit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.service.jakartaws.runtime.WebserviceServiceRuntime;
import org.osgi.service.jakartaws.runtime.dto.EnpointDTO;
import org.osgi.service.jakartaws.runtime.dto.RuntimeDTO;
import org.osgi.service.jakartaws.whiteboard.SoapWhiteboardConstants;
import org.osgi.test.cases.jakartaws.webservice.WSEcho;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@ExtendWith(ServiceExtension.class)
@ExtendWith(BundleContextExtension.class)
public class SoapWihteboardTestCase {

	private static final String	KEY_UUUD	= "UUUD";
	@InjectService(timeout = 10000)
	WebserviceServiceRuntime	runtime;

	@Test
	public void testHelloService(@InjectBundleContext
	BundleContext bundleContext) throws Exception {
		System.out.println("helo");
		Hashtable<String,Object> properties = new Hashtable<>();
		properties.put(SoapWhiteboardConstants.SOAP_ENDPOINT_IMPLEMENTOR, true);
		String id = UUID.randomUUID().toString();
		properties.put(KEY_UUUD, id);
		bundleContext.registerService(WSEcho.class, new WSEcho(), properties);
		waitForDTO(10, SECONDS, dto -> {
			assertThat(dto.endpoints)
					.as("Endpoints DTO")
					.isNotNull();
			for (EnpointDTO ep : dto.endpoints) {
				assertThat(ep.implementor).as("Endpoint Implementor DTO")
						.isNotNull();
				if (ep.implementor.bundle == bundleContext.getBundle()
						.getBundleId()) {
					if (id.equals(ep.implementor.properties.get(KEY_UUUD))) {
						return true;
					}
				}
			}
			return false;
		});
	}

	private void waitForDTO(long time, TimeUnit unit,
			Predicate<RuntimeDTO> tester) {
		long deadline = System.currentTimeMillis() + unit.toMillis(time);
		while (System.currentTimeMillis() < deadline) {
			RuntimeDTO runtimeDTO = runtime.getRuntimeDTO();
			assertThat(runtimeDTO).as("RuntimeDTO").isNotNull();
			if (tester.test(runtimeDTO)) {
				return;
			}
			Thread.yield();
		}
		Assertions.fail("Timeout waiting for DTO");
	}
}
