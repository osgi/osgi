package org.osgi.service.onem2m;

import org.osgi.service.onem2m.dto.RequestPrimitiveDTO;

/**
 * Interface to receive notification from other oneM2M entities.
 * <p>
 * Application which receives notification must implement this interface and 
 * register to OSGi service registry. No service property is required.
 */
public interface NotificationListener
{
	/**
	 * receive notification.
	 *
	 * @param request request primitive
	 *
	 */
	public void notified(RequestPrimitiveDTO request);

}
