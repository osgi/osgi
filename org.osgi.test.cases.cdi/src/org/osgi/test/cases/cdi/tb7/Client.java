package org.osgi.test.cases.cdi.tb7;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cdi.reference.ReferenceEvent;
import org.osgi.service.cdi.runtime.CDIComponentRuntime;
import org.osgi.service.cdi.runtime.dto.ContainerDTO;
import org.osgi.test.cases.cdi.serviceapi.ContainerId;

@ApplicationScoped
public class Client {

	private final Bundle _bundle;
	private final Event<ContainerDTO> _event;

	private volatile long _lastChange = 0;

	// @Inject
	// @Reference
	// Foo<org.osgi.test.cases.tb.serviceapi.Foo> pr

	@Inject
	public Client(BundleContext bundleContext, Event<ContainerDTO> event) {
		_bundle = bundleContext.getBundle();
		_event = event;
	}

	void updates(@Observes ReferenceEvent<CDIComponentRuntime> event) {
		event.onUpdate(this::checkMe);
	}

	private void checkMe(CDIComponentRuntime r) {
		long changeCount = r.getContainerChangeCount(_bundle);
		if (_lastChange < changeCount) {
			_lastChange = changeCount;

			ContainerDTO containerDTO = r.getContainerDTO(_bundle);

			_event.select(ContainerId.Literal.of(containerDTO.template.id)).fire(containerDTO);
		}
	}

}
