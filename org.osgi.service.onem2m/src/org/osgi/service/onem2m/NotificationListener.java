package org.osgi.service.onem2m;

import org.osgi.service.onem2m.dto.RequestPrimitiveDTO;

/**
 * Primary Interface for an oneM2M entity to send request and get response to/from other
 * oneM2M entity.
 *
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
