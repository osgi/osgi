package org.osgi.service.onem2m.impl.protocol.coap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.osgi.service.onem2m.NotificationListener;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO;

public class NotificationResource extends CoapResource{

	private NotificationListener listener = null;

	public NotificationResource(String name) {
		super(name);
	}

	public void setListener(NotificationListener listener) {
		this.listener = listener;
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		RequestPrimitiveDTO primitive = new RequestPrimitiveDTO();

		listener.notified(primitive);
	}

}
