/*
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

package javax.security.auth;
public final class Subject implements java.io.Serializable {
	public Subject() { }
	public Subject(boolean var0, java.util.Set var1, java.util.Set var2, java.util.Set var3) { }
	public static java.lang.Object doAs(javax.security.auth.Subject var0, java.security.PrivilegedAction var1) { return null; }
	public static java.lang.Object doAs(javax.security.auth.Subject var0, java.security.PrivilegedExceptionAction var1) throws java.security.PrivilegedActionException { return null; }
	public static java.lang.Object doAsPrivileged(javax.security.auth.Subject var0, java.security.PrivilegedAction var1, java.security.AccessControlContext var2) { return null; }
	public static java.lang.Object doAsPrivileged(javax.security.auth.Subject var0, java.security.PrivilegedExceptionAction var1, java.security.AccessControlContext var2) throws java.security.PrivilegedActionException { return null; }
	public java.util.Set getPrincipals() { return null; }
	public java.util.Set getPrincipals(java.lang.Class var0) { return null; }
	public java.util.Set getPrivateCredentials() { return null; }
	public java.util.Set getPrivateCredentials(java.lang.Class var0) { return null; }
	public java.util.Set getPublicCredentials() { return null; }
	public java.util.Set getPublicCredentials(java.lang.Class var0) { return null; }
	public static javax.security.auth.Subject getSubject(java.security.AccessControlContext var0) { return null; }
	public int hashCode() { return 0; }
	public boolean isReadOnly() { return false; }
	public void setReadOnly() { }
}

