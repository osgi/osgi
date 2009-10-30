/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
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

package javax.transaction.xa;
public interface XAResource {
	public final static int TMENDRSCAN = 8388608;
	public final static int TMFAIL = 536870912;
	public final static int TMJOIN = 2097152;
	public final static int TMNOFLAGS = 0;
	public final static int TMONEPHASE = 1073741824;
	public final static int TMRESUME = 134217728;
	public final static int TMSTARTRSCAN = 16777216;
	public final static int TMSUCCESS = 67108864;
	public final static int TMSUSPEND = 33554432;
	public final static int XA_OK = 0;
	public final static int XA_RDONLY = 3;
	void commit(javax.transaction.xa.Xid var0, boolean var1) throws javax.transaction.xa.XAException;
	void end(javax.transaction.xa.Xid var0, int var1) throws javax.transaction.xa.XAException;
	void forget(javax.transaction.xa.Xid var0) throws javax.transaction.xa.XAException;
	int getTransactionTimeout() throws javax.transaction.xa.XAException;
	boolean isSameRM(javax.transaction.xa.XAResource var0) throws javax.transaction.xa.XAException;
	int prepare(javax.transaction.xa.Xid var0) throws javax.transaction.xa.XAException;
	javax.transaction.xa.Xid[] recover(int var0) throws javax.transaction.xa.XAException;
	void rollback(javax.transaction.xa.Xid var0) throws javax.transaction.xa.XAException;
	boolean setTransactionTimeout(int var0) throws javax.transaction.xa.XAException;
	void start(javax.transaction.xa.Xid var0, int var1) throws javax.transaction.xa.XAException;
}

