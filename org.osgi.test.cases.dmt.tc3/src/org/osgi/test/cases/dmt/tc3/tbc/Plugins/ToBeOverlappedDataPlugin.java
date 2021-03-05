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

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Feb 25, 2005  Andre Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 * Mar 04, 2005  Luiz Felipe Guimaraes
 * 11            Implement TCK Use Cases
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.Plugins;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;

/**
 * @author Andre Assad
 * 
 * This plugin must be overlapped by the DmtExecPlugin, so these methods must not be called. 
 * Instead of that, org.osgi.test.cases.dmt.tbc.ExecPlugin.TestExecPlugin will be called.)
 */
public class ToBeOverlappedDataPlugin implements DataPlugin {
    
    public static final String MESSAGE = "ToBeOverlappedDataPlugin";
    
    @Override
	public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
        return new DefaultOverlappingPluginTransactionalDataSession(MESSAGE);
    }
    
    @Override
	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
        return new DefaultOverlappingPluginTransactionalDataSession(MESSAGE);
    }

    @Override
	public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
        return new DefaultOverlappingPluginTransactionalDataSession(MESSAGE);
        
    }
}
