/*
 * $Revision$
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

package java.rmi.server;
public final class ObjID implements java.io.Serializable {
	public ObjID() { }
	public ObjID(int var0) { }
	public int hashCode() { return 0; }
	public static java.rmi.server.ObjID read(java.io.ObjectInput var0) throws java.io.IOException { return null; }
	public void write(java.io.ObjectOutput var0) throws java.io.IOException { }
	public final static int ACTIVATOR_ID = 1;
	public final static int DGC_ID = 2;
	public final static int REGISTRY_ID = 0;
}

