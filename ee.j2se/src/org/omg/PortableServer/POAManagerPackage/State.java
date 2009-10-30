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

package org.omg.PortableServer.POAManagerPackage;
public class State implements org.omg.CORBA.portable.IDLEntity {
	public final static org.omg.PortableServer.POAManagerPackage.State ACTIVE; static { ACTIVE = null; }
	public final static org.omg.PortableServer.POAManagerPackage.State DISCARDING; static { DISCARDING = null; }
	public final static org.omg.PortableServer.POAManagerPackage.State HOLDING; static { HOLDING = null; }
	public final static org.omg.PortableServer.POAManagerPackage.State INACTIVE; static { INACTIVE = null; }
	public final static int _ACTIVE = 1;
	public final static int _DISCARDING = 2;
	public final static int _HOLDING = 0;
	public final static int _INACTIVE = 3;
	protected State(int var0) { } 
	public static org.omg.PortableServer.POAManagerPackage.State from_int(int var0) { return null; }
	public int value() { return 0; }
}

