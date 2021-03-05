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
package org.osgi.test.cases.dmt.tc4.ext.util;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;

public class TestDataPlugin implements DataPlugin {

	protected final Map<String,TestNode> nodeMap = new HashMap<>();

    @Override
	public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
        return new TestPluginReadableDataSession(nodeMap);
    }

    @Override
	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
        return new TestPluginReadWriteDataSession(nodeMap);
    }

    @Override
	public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
        return null;
    }

    public void setNode(String nodeUri, TestNode node) {
        nodeMap.put(nodeUri, node);
    }
}
