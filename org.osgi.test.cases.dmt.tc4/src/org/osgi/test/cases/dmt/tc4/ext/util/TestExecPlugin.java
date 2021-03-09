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

import static junit.framework.TestCase.fail;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.ExecPlugin;

public class TestExecPlugin implements ExecPlugin {

    private boolean wasExecuteCalled;

    private DmtSession session;

    private String[] nodePath;

    private String correlator;

    private String data;

    private boolean failed = false;

    @Override
	public void execute(DmtSession session, String[] nodePath, String correlator, String data) throws DmtException {
        synchronized (this) {
            if (wasExecuteCalled) {
                failed = true;
            }
        }
        this.wasExecuteCalled = true;
        this.session = session;
        this.nodePath = nodePath;
        this.correlator = correlator;
        this.data = data;
    }

    public boolean wasExecuteCalled() {
        synchronized (this) {
            if (failed) {
				fail();
            }
        }

        try {
            return wasExecuteCalled;
        } finally {
            wasExecuteCalled = false;
        }
    }

    public DmtSession getSession() {
        synchronized (this) {
            if (failed) {
				fail();
            }
        }
        return session;
    }

    public String[] getNodePath() {
        synchronized (this) {
            if (failed) {
				fail();
            }
        }
        return nodePath;
    }

    public String getCorrelator() {
        synchronized (this) {
            if (failed) {
				fail();
            }
        }
        return correlator;
    }

    public String getData() {
        synchronized (this) {
            if (failed) {
				fail();
            }
        }
        return data;
    }
}
