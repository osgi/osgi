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
public final class SortResponseControl extends javax.naming.ldap.BasicControl {
	public final static java.lang.String OID = "1.2.840.113556.1.4.474";
	public SortResponseControl(java.lang.String var0, boolean var1, byte[] var2) throws java.io.IOException  { super((java.lang.String) null, false, (byte[]) null); } 
	public java.lang.String getAttributeID() { return null; }
	public javax.naming.NamingException getException() { return null; }
	public int getResultCode() { return 0; }
	public boolean isSorted() { return false; }
}

