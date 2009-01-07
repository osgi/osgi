/*
 * Copyright (C) 2008 ProSyst Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.transaction;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class TestResource implements XAResource {
	
	private boolean committed = false;
	
	public boolean isCommitted() {
		return committed;
	}

	public void commit(Xid arg0, boolean arg1) throws XAException {
		committed = true;
	}

	public void end(Xid arg0, int arg1) throws XAException {
	}

	public void forget(Xid arg0) throws XAException {
	}

	public int getTransactionTimeout() throws XAException {
		return 0;
	}

	public boolean isSameRM(XAResource arg0) throws XAException {
		return false;
	}

	public int prepare(Xid arg0) throws XAException {
		return 0;
	}

	public Xid[] recover(int arg0) throws XAException {
		return null;
	}

	public void rollback(Xid arg0) throws XAException {
	}

	public boolean setTransactionTimeout(int arg0) throws XAException {
		return false;
	}

	public void start(Xid arg0, int arg1) throws XAException {
	}
	
}
