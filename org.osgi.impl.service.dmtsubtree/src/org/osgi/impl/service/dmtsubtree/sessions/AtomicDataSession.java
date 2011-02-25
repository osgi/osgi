/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.impl.service.dmtsubtree.sessions;

import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.spi.TransactionalDataSession;

import java.util.LinkedHashMap;

import org.osgi.impl.service.dmtsubtree.Activator;
import org.osgi.impl.service.dmtsubtree.mapping.VendorPluginInfo;

public class AtomicDataSession extends WriteableDataSession implements TransactionalDataSession {

	// also Atomic sessions are stored in its own (overwritten) map
	LinkedHashMap vendorPluginSessions;

	public AtomicDataSession(Activator activator, String[] sessionRoot) throws DmtException {
		super( activator, sessionRoot );
	}

	public void commit() throws DmtException {
		// we have to close all involved vendorPluginSessions
		SessionInfo[] sessionInfos = (SessionInfo[]) getVendorPluginSessions().values().toArray(
				new SessionInfo[getVendorPluginSessions().size()] );
		DmtSession[] sessions = getOrderedSessions( sessionInfos );
		for (int i = 0; i < sessions.length; i++) {
			try {
				DmtSession session = (DmtSession) sessions[i];
				session.commit();
			} catch (DmtException e) {
				activator.logError( "error while committing an involved vendor plugin session", e );
			}
		}
	}

	public void rollback() throws DmtException {
		// we have to rollback all involved vendorPluginSessions
		SessionInfo[] sessionInfos = (SessionInfo[]) getVendorPluginSessions().values().toArray(
				new SessionInfo[getVendorPluginSessions().size()] );
		DmtSession[] sessions = getOrderedSessions( sessionInfos );
		for (int i = 0; i < sessions.length; i++) {
			try {
				DmtSession session = (DmtSession) sessions[i];
				session.rollback();

			} catch (DmtException e) {
				activator.logError( "error while rolling back an involved vendor plugin session", e );
			}
		}
	}

	/**
	 * local facade for the retrieval (including potential creation) of a
	 * session with lock type ATOMIC
	 * 
	 * @param pluginInfo
	 * @return
	 * @throws DmtException
	 */
	DmtSession getSession(VendorPluginInfo pluginInfo) throws DmtException {
		return getSession( pluginInfo, DmtSession.LOCK_TYPE_ATOMIC );
	}

}
