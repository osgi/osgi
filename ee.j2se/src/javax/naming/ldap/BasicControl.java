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

package javax.naming.ldap;
public class BasicControl implements javax.naming.ldap.Control {
	protected boolean criticality;
	protected java.lang.String id;
	protected byte[] value;
	public BasicControl(java.lang.String var0) { } 
	public BasicControl(java.lang.String var0, boolean var1, byte[] var2) { } 
	public byte[] getEncodedValue() { return null; }
	public java.lang.String getID() { return null; }
	public boolean isCritical() { return false; }
}

