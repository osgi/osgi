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
public interface GSSCredential extends java.lang.Cloneable {
	public final static int ACCEPT_ONLY = 2;
	public final static int DEFAULT_LIFETIME = 0;
	public final static int INDEFINITE_LIFETIME = 2147483647;
	public final static int INITIATE_AND_ACCEPT = 0;
	public final static int INITIATE_ONLY = 1;
	void add(org.ietf.jgss.GSSName var0, int var1, int var2, org.ietf.jgss.Oid var3, int var4) throws org.ietf.jgss.GSSException;
	void dispose() throws org.ietf.jgss.GSSException;
	boolean equals(java.lang.Object var0);
	org.ietf.jgss.Oid[] getMechs() throws org.ietf.jgss.GSSException;
	org.ietf.jgss.GSSName getName() throws org.ietf.jgss.GSSException;
	org.ietf.jgss.GSSName getName(org.ietf.jgss.Oid var0) throws org.ietf.jgss.GSSException;
	int getRemainingAcceptLifetime(org.ietf.jgss.Oid var0) throws org.ietf.jgss.GSSException;
	int getRemainingInitLifetime(org.ietf.jgss.Oid var0) throws org.ietf.jgss.GSSException;
	int getRemainingLifetime() throws org.ietf.jgss.GSSException;
	int getUsage() throws org.ietf.jgss.GSSException;
	int getUsage(org.ietf.jgss.Oid var0) throws org.ietf.jgss.GSSException;
	int hashCode();
}

