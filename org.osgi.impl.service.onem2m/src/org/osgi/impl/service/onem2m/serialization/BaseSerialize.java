package org.osgi.impl.service.onem2m.serialization;

import org.osgi.service.onem2m.dto.NotificationDTO;
import org.osgi.service.onem2m.dto.ResourceDTO;

public interface BaseSerialize {
	public Object resourceToRequest(ResourceDTO dto) throws Exception;
	public ResourceDTO responseToResource(Object response) throws Exception;
	public Object notificationToRequest(NotificationDTO dto) throws Exception;
}
