package org.osgi.test.cases.discovery.internal;

import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServiceListener;

public class DiscoveryServiceListener implements ServiceListener {
	ServiceEndpointDescription availableCalled = null;
	ServiceEndpointDescription modifiedCalled = null;
	ServiceEndpointDescription unavailableCalled = null;

	public void setAvailableCalled(ServiceEndpointDescription availableCalled) {
		this.availableCalled = availableCalled;
	}

	public void setModifiedCalled(ServiceEndpointDescription modifiedCalled) {
		this.modifiedCalled = modifiedCalled;
	}

	public void setUnavailableCalled(ServiceEndpointDescription unavailableCalled) {
		this.unavailableCalled = unavailableCalled;
	}

	public ServiceEndpointDescription getAvailableCalled() {
		return availableCalled;
	}

	public ServiceEndpointDescription getModifiedCalled() {
		return modifiedCalled;
	}

	public ServiceEndpointDescription getUnavailableCalled() {
		return unavailableCalled;
	}

	public void serviceAvailable(ServiceEndpointDescription arg0) {
		availableCalled = arg0;
	}

	public void serviceModified(ServiceEndpointDescription arg0,
			ServiceEndpointDescription arg1) {
		modifiedCalled = arg1;
	}

	public void serviceUnavailable(ServiceEndpointDescription arg0) {
		unavailableCalled = arg0;
	}

}
