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

package javax.security.auth;
public final class Subject implements java.io.Serializable {
	public Subject() { } 
	public Subject(boolean var0, java.util.Set<? extends java.security.Principal> var1, java.util.Set<?> var2, java.util.Set<?> var3) { } 
	public static <T> T doAs(javax.security.auth.Subject var0, java.security.PrivilegedAction<T> var1) { return null; }
	public static <T> T doAs(javax.security.auth.Subject var0, java.security.PrivilegedExceptionAction<T> var1) throws java.security.PrivilegedActionException { return null; }
	public static <T> T doAsPrivileged(javax.security.auth.Subject var0, java.security.PrivilegedAction<T> var1, java.security.AccessControlContext var2) { return null; }
	public static <T> T doAsPrivileged(javax.security.auth.Subject var0, java.security.PrivilegedExceptionAction<T> var1, java.security.AccessControlContext var2) throws java.security.PrivilegedActionException { return null; }
	public java.util.Set<java.security.Principal> getPrincipals() { return null; }
	public <T extends java.security.Principal> java.util.Set<T> getPrincipals(java.lang.Class<T> var0) { return null; }
	public java.util.Set<java.lang.Object> getPrivateCredentials() { return null; }
	public <T> java.util.Set<T> getPrivateCredentials(java.lang.Class<T> var0) { return null; }
	public java.util.Set<java.lang.Object> getPublicCredentials() { return null; }
	public <T> java.util.Set<T> getPublicCredentials(java.lang.Class<T> var0) { return null; }
	public static javax.security.auth.Subject getSubject(java.security.AccessControlContext var0) { return null; }
	public boolean isReadOnly() { return false; }
	public void setReadOnly() { }
}

