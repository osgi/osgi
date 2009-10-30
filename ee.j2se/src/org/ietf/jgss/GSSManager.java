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
public abstract class GSSManager {
	public GSSManager() { } 
	public abstract void addProviderAtEnd(java.security.Provider var0, org.ietf.jgss.Oid var1) throws org.ietf.jgss.GSSException;
	public abstract void addProviderAtFront(java.security.Provider var0, org.ietf.jgss.Oid var1) throws org.ietf.jgss.GSSException;
	public abstract org.ietf.jgss.GSSContext createContext(org.ietf.jgss.GSSCredential var0) throws org.ietf.jgss.GSSException;
	public abstract org.ietf.jgss.GSSContext createContext(org.ietf.jgss.GSSName var0, org.ietf.jgss.Oid var1, org.ietf.jgss.GSSCredential var2, int var3) throws org.ietf.jgss.GSSException;
	public abstract org.ietf.jgss.GSSContext createContext(byte[] var0) throws org.ietf.jgss.GSSException;
	public abstract org.ietf.jgss.GSSCredential createCredential(int var0) throws org.ietf.jgss.GSSException;
	public abstract org.ietf.jgss.GSSCredential createCredential(org.ietf.jgss.GSSName var0, int var1, org.ietf.jgss.Oid var2, int var3) throws org.ietf.jgss.GSSException;
	public abstract org.ietf.jgss.GSSCredential createCredential(org.ietf.jgss.GSSName var0, int var1, org.ietf.jgss.Oid[] var2, int var3) throws org.ietf.jgss.GSSException;
	public abstract org.ietf.jgss.GSSName createName(java.lang.String var0, org.ietf.jgss.Oid var1) throws org.ietf.jgss.GSSException;
	public abstract org.ietf.jgss.GSSName createName(java.lang.String var0, org.ietf.jgss.Oid var1, org.ietf.jgss.Oid var2) throws org.ietf.jgss.GSSException;
	public abstract org.ietf.jgss.GSSName createName(byte[] var0, org.ietf.jgss.Oid var1) throws org.ietf.jgss.GSSException;
	public abstract org.ietf.jgss.GSSName createName(byte[] var0, org.ietf.jgss.Oid var1, org.ietf.jgss.Oid var2) throws org.ietf.jgss.GSSException;
	public static org.ietf.jgss.GSSManager getInstance() { return null; }
	public abstract org.ietf.jgss.Oid[] getMechs();
	public abstract org.ietf.jgss.Oid[] getMechsForName(org.ietf.jgss.Oid var0);
	public abstract org.ietf.jgss.Oid[] getNamesForMech(org.ietf.jgss.Oid var0) throws org.ietf.jgss.GSSException;
}

