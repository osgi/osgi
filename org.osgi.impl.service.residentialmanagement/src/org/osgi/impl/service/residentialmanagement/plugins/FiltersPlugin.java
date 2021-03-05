/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.residentialmanagement.plugins;

import org.osgi.framework.BundleContext;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;
/**
 * 
 * @author Shigekuni KONDO NTT Corporation
 */
public class FiltersPlugin implements DataPlugin {
	private FiltersReadOnlySession readonly;
	private FiltersReadWriteSession readwrite;
	private DmtSession session;

	FiltersPlugin(BundleContext context) {
		readonly = new FiltersReadOnlySession(this, context);
		readwrite = new FiltersReadWriteSession(this, context, readonly);
	}
	
	public DmtSession getSession(){
		return session;
	}

	@Override
	public TransactionalDataSession openAtomicSession(String[] sessionRoot,
			@SuppressWarnings("hiding") DmtSession session)
			throws DmtException {
		this.session = session;
		return readwrite;
	}

	@Override
	public ReadableDataSession openReadOnlySession(String[] sessionRoot,
			@SuppressWarnings("hiding") DmtSession session)
			throws DmtException {
		return readonly;
	}

	@Override
	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
			@SuppressWarnings("hiding") DmtSession session)
			throws DmtException {
		this.session = session;
		return readwrite;
	}

}
