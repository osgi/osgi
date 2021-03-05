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
package org.osgi.test.cases.dmt.tc4.tb1;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.dmt.tc4.tb1.intf.Node;
import org.osgi.test.cases.dmt.tc4.tb1.nodes.Framework;


public class FrameworkDP implements DataPlugin {
	private Node frameworkNode;

	
	public FrameworkDP(BundleContext context) {
		frameworkNode = new Framework(context);
	}
	

	@Override
	public TransactionalDataSession openAtomicSession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		System.out.println("[Framework Data Plugin] Atomic Sessions are not supported");
		return null;
	}

	@Override
	public ReadableDataSession openReadOnlySession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		System.out.println("[Framework Data Plugin] Creating a Read-Only Session");
		return new ReadOnlyDS(frameworkNode);
	}

	@Override
	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		System.out.println("[Framework Data Plugin] Creating a Read-Write Session");
		return new ReadWriteDS(frameworkNode);
	}
}
