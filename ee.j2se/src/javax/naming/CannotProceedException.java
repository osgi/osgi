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

package javax.naming;
public class CannotProceedException extends javax.naming.NamingException {
	public CannotProceedException() { }
	public CannotProceedException(java.lang.String var0) { }
	public javax.naming.Name getAltName() { return null; }
	public javax.naming.Context getAltNameCtx() { return null; }
	public java.util.Hashtable getEnvironment() { return null; }
	public javax.naming.Name getRemainingNewName() { return null; }
	public void setAltName(javax.naming.Name var0) { }
	public void setAltNameCtx(javax.naming.Context var0) { }
	public void setEnvironment(java.util.Hashtable var0) { }
	public void setRemainingNewName(javax.naming.Name var0) { }
	protected javax.naming.Name altName;
	protected javax.naming.Context altNameCtx;
	protected java.util.Hashtable environment;
	protected javax.naming.Name remainingNewName;
}

