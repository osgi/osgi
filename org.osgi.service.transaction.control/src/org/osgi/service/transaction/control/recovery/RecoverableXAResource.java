/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.service.transaction.control.recovery;

import javax.transaction.xa.XAResource;

import org.osgi.service.transaction.control.ResourceProvider;
import org.osgi.service.transaction.control.TransactionContext;

/**
 * A {@link RecoverableXAResource} service may be provided by a
 * {@link ResourceProvider} if they are able to support XA recovery
 * operations.
 * 
 * There are two main sorts of recovery:
 * 
 * <ul>
 *   <li>Recovery after a remote failure, where the local transaction
 *       manager runs throughout</li>
 *   <li>Recovery after a local failure, where the transaction manager
 *       replays in-doubt transactions from its log</li>
 * </ul>
 * 
 * This service is used in both of these cases. 
 * 
 * The identifier returned by {@link #getId()} provides a persistent name 
 * that can be used to correlate usage of the resource both before and after
 * failure. This identifier must also be passed to 
 * {@link TransactionContext#registerXAResource(XAResource, String)} each time
 * the recoverable resource is used.
 * 
 */
public interface RecoverableXAResource {

	/**
	 * Get the id of this resource. This should be unique, and persist between restarts
	 * @return an identifier, never <code>null</code>
	 */
	String getId();
	
	/**
	 * Get a new, valid XAResource that can be used in recovery
	 * 
	 * This XAResource will be returned later using the 
	 * {@link #releaseXAResource(XAResource)} method
	 * 
	 * @return a valid, connected, XAResource 
	 * 
	 * @throws Exception If it is not possible to acquire a valid
	 * XAResource at the current time, for example if the database
	 * is temporarily unavailable.
	 */
	XAResource getXAResource() throws Exception;
	
	/**
	 * Release the XAResource that has been used for recovery
	 * 
	 * @param xaRes An {@link XAResource} previously returned
	 * by {@link #getXAResource()}
	 */
	void releaseXAResource(XAResource xaRes);
}
