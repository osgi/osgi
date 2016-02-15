package org.osgi.service.transaction.control.recovery;

import javax.transaction.xa.XAResource;

/**
 * This service interface is published by Transaction control services that are
 * able to support recovery. Any recoverable resources should register
 * themselves with all available recovery services as they are created.
 */
public interface TransactionRecovery {

	/**
	 * Allow the {@link TransactionRecovery} service to attempt to recover any
	 * incomplete XA transactions. Any recovery failures that occur must be
	 * logged and not thrown to the caller of this service.
	 * 
	 * @param resource
	 */
	public void recover(XAResource resource);

}
