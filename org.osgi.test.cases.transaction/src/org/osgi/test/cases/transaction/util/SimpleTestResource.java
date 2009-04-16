/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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

package org.osgi.test.cases.transaction.util;

import java.io.Serializable;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * @version $Rev$ $Date$
 */
public class SimpleTestResource implements XAResource, Serializable {
	
	private static final long serialVersionUID = 6246488102848183247L;
	
	private boolean committed = false;
	private boolean rolledback = false;
	private boolean prepared = false;
	private String resName = "testResource";
	
	public SimpleTestResource(String resName) {
	    this.resName = resName;
	}
	public boolean isCommitted() {
		return this.committed;
	}
	
	public boolean isRolledback() {
	    return this.rolledback;
	}
	
	public boolean isPrepared() {
	    return this.prepared;
	}

	public void commit(Xid arg0, boolean onePhase) throws XAException {
		this.committed = true;
	}

	public void end(Xid arg0, int arg1) throws XAException {
	}

	public void forget(Xid arg0) throws XAException {
	}

	public int getTransactionTimeout() throws XAException {
		return 0;
	}

	public boolean isSameRM(XAResource arg0) throws XAException {
	    if (arg0 instanceof SimpleTestResource)
	        {
	            if (resName.equals(((SimpleTestResource)arg0).resName))
	            {
	                return true;
	            }
	        }
	        
	 
		return false;
	}

	public int prepare(Xid arg0) throws XAException {
		this.prepared = true;
		return XA_OK;
	}

	public Xid[] recover(int arg0) throws XAException {
		return null;
	}

	public void rollback(Xid arg0) throws XAException {
	    this.rolledback = true;
	}

	public boolean setTransactionTimeout(int arg0) throws XAException {
		return false;
	}

	public void start(Xid arg0, int arg1) throws XAException {
	}
	
	public String getName() {
	    return this.resName;
	}
}
