/*
 * $Date$
 *
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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
public class XAException extends java.lang.Exception {
	public XAException() { }
	public XAException(int var0) { }
	public XAException(java.lang.String var0) { }
	public final static int XAER_ASYNC = -2;
	public final static int XAER_DUPID = -8;
	public final static int XAER_INVAL = -5;
	public final static int XAER_NOTA = -4;
	public final static int XAER_OUTSIDE = -9;
	public final static int XAER_PROTO = -6;
	public final static int XAER_RMERR = -3;
	public final static int XAER_RMFAIL = -7;
	public final static int XA_HEURCOM = 7;
	public final static int XA_HEURHAZ = 8;
	public final static int XA_HEURMIX = 5;
	public final static int XA_HEURRB = 6;
	public final static int XA_NOMIGRATE = 9;
	public final static int XA_RBBASE = 100;
	public final static int XA_RBCOMMFAIL = 101;
	public final static int XA_RBDEADLOCK = 102;
	public final static int XA_RBEND = 107;
	public final static int XA_RBINTEGRITY = 103;
	public final static int XA_RBOTHER = 104;
	public final static int XA_RBPROTO = 105;
	public final static int XA_RBROLLBACK = 100;
	public final static int XA_RBTIMEOUT = 106;
	public final static int XA_RBTRANSIENT = 107;
	public final static int XA_RDONLY = 3;
	public final static int XA_RETRY = 4;
	public int errorCode;
}

