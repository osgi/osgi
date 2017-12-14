package org.osgi.test.cases.cdi.tb12;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.spi.EventMetadata;
import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventProperties;
import org.osgi.test.cases.cdi.serviceapi.Foo;

@ApplicationScoped
public class EventAdminSender {

	@Inject
	@Reference
	private org.osgi.service.event.EventAdmin eventAdmin;

	void sendEvent(@Observes Foo foo, EventMetadata eventMetadata) {
		Map<String, Object> map = new HashMap<>();

		map.put("cdi.event", foo);

		// TODO merge in the qualifiers from eventMetadata

		eventAdmin.sendEvent(new Event("com/acme/foo", new EventProperties(map)));
	}

	void postEvent(@ObservesAsync Foo foo, EventMetadata eventMetadata) {
		Map<String, Object> map = new HashMap<>();

		map.put("cdi.event", foo);

		// TODO merge in the qualifiers from eventMetadata

		eventAdmin.postEvent(new Event("com/acme/foo", new EventProperties(map)));
	}

}
