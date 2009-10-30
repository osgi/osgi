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

package org.omg.PortableServer;
public class ServantRetentionPolicyValue implements org.omg.CORBA.portable.IDLEntity {
	public final static org.omg.PortableServer.ServantRetentionPolicyValue NON_RETAIN; static { NON_RETAIN = null; }
	public final static org.omg.PortableServer.ServantRetentionPolicyValue RETAIN; static { RETAIN = null; }
	public final static int _NON_RETAIN = 1;
	public final static int _RETAIN = 0;
	protected ServantRetentionPolicyValue(int var0) { } 
	public static org.omg.PortableServer.ServantRetentionPolicyValue from_int(int var0) { return null; }
	public int value() { return 0; }
}

