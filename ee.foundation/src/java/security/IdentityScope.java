/*
 * $Date$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2007). All Rights Reserved.
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

package java.security;
/** @deprecated */ public abstract class IdentityScope extends java.security.Identity {
	protected IdentityScope() { }
	public IdentityScope(java.lang.String var0) { }
	public IdentityScope(java.lang.String var0, java.security.IdentityScope var1) throws java.security.KeyManagementException { }
	public abstract void addIdentity(java.security.Identity var0) throws java.security.KeyManagementException;
	public abstract java.security.Identity getIdentity(java.lang.String var0);
	public java.security.Identity getIdentity(java.security.Principal var0) { return null; }
	public abstract java.security.Identity getIdentity(java.security.PublicKey var0);
	public static java.security.IdentityScope getSystemScope() { return null; }
	public abstract java.util.Enumeration identities();
	public abstract void removeIdentity(java.security.Identity var0) throws java.security.KeyManagementException;
	protected static void setSystemScope(java.security.IdentityScope var0) { }
	public abstract int size();
}

