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

package javax.naming;
public class NamingException extends java.lang.Exception {
	public NamingException() { }
	public NamingException(java.lang.String var0) { }
	public void appendRemainingComponent(java.lang.String var0) { }
	public void appendRemainingName(javax.naming.Name var0) { }
	public java.lang.String getExplanation() { return null; }
	public javax.naming.Name getRemainingName() { return null; }
	public javax.naming.Name getResolvedName() { return null; }
	public java.lang.Object getResolvedObj() { return null; }
	public java.lang.Throwable getRootCause() { return null; }
	public java.lang.Throwable initCause(java.lang.Throwable var0) { return null; }
	public void setRemainingName(javax.naming.Name var0) { }
	public void setResolvedName(javax.naming.Name var0) { }
	public void setResolvedObj(java.lang.Object var0) { }
	public void setRootCause(java.lang.Throwable var0) { }
	public java.lang.String toString(boolean var0) { return null; }
	protected javax.naming.Name remainingName;
	protected javax.naming.Name resolvedName;
	protected java.lang.Object resolvedObj;
	protected java.lang.Throwable rootException;
}

