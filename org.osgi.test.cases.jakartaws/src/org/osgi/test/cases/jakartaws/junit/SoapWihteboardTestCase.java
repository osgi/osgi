package org.osgi.test.cases.jakartaws.junit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.service.jakartaws.runtime.WebserviceServiceRuntime;
import org.osgi.service.jakartaws.runtime.dto.EndpointDTO;
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

	private static final String	DEFAULT_PUBLISH_ADDRESS	= System.getProperty(
			"org.osgi.test.cases.jakartaws.defaultaddress",
			"http://localhost:8579");
	private static final String	KEY_UUUID		= "UUUID";
	@InjectService(timeout = 10000)
	WebserviceServiceRuntime	runtime;

	@Test
	public void testHelloService(@InjectBundleContext
	BundleContext bundleContext) throws Exception {
		String id = UUID.randomUUID().toString();
		Hashtable<String,Object> properties = new Hashtable<>();
		properties.put(SoapWhiteboardConstants.SOAP_ENDPOINT_IMPLEMENTOR, true);
		properties.put(SoapWhiteboardConstants.SOAP_ENDPOINT_ADDRESS,
				DEFAULT_PUBLISH_ADDRESS);
		properties.put(KEY_UUUID, id);
		bundleContext.registerService(WSEcho.class, new WSEcho(), properties);
		EndpointDTO endpoint = waitForDTO(10, SECONDS, dto -> {
			assertThat(dto.endpoints).as("Endpoints DTO").isNotNull();
			for (EndpointDTO ep : dto.endpoints) {
				assertThat(ep.implementor).as("Endpoint Implementor DTO")
						.isNotNull();
				if (ep.implementor.bundle == bundleContext.getBundle()
						.getBundleId()) {
					System.out.println("Waiting for " + id + " eq "
							+ ep.implementor.properties.get(KEY_UUUID));
					if (id.equals(ep.implementor.properties.get(KEY_UUUID))) {
						assertThat(ep.address).as("Publish Address")
								.isNotNull();
						return ep;
					}
				}
			}
			return null;
		});
		assertThat(endpoint.address).as("Endpoint Address")
				.isEqualTo(DEFAULT_PUBLISH_ADDRESS);
	}

	private <T> T waitForDTO(long time, TimeUnit unit,
			Function<RuntimeDTO,T> tester) {
		long deadline = System.currentTimeMillis() + unit.toMillis(time);
		while (System.currentTimeMillis() < deadline) {
			RuntimeDTO runtimeDTO = runtime.getRuntimeDTO();
			assertThat(runtimeDTO).as("RuntimeDTO").isNotNull();
			T result = tester.apply(runtimeDTO);
			if (result != null) {
				return result;
			}
			Thread.yield();
		}
		Assertions.fail("Timeout waiting for DTO");
		return null;
	}
}
