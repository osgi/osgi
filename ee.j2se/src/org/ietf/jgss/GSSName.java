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

package org.ietf.jgss;
public interface GSSName {
	public final static org.ietf.jgss.Oid NT_ANONYMOUS = null;
	public final static org.ietf.jgss.Oid NT_EXPORT_NAME = null;
	public final static org.ietf.jgss.Oid NT_HOSTBASED_SERVICE = null;
	public final static org.ietf.jgss.Oid NT_MACHINE_UID_NAME = null;
	public final static org.ietf.jgss.Oid NT_STRING_UID_NAME = null;
	public final static org.ietf.jgss.Oid NT_USER_NAME = null;
	org.ietf.jgss.GSSName canonicalize(org.ietf.jgss.Oid var0) throws org.ietf.jgss.GSSException;
	boolean equals(java.lang.Object var0);
	boolean equals(org.ietf.jgss.GSSName var0) throws org.ietf.jgss.GSSException;
	byte[] export() throws org.ietf.jgss.GSSException;
	org.ietf.jgss.Oid getStringNameType() throws org.ietf.jgss.GSSException;
	int hashCode();
	boolean isAnonymous();
	boolean isMN();
	java.lang.String toString();
}

