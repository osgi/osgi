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
public interface GSSContext {
	public final static int DEFAULT_LIFETIME = 0;
	public final static int INDEFINITE_LIFETIME = 2147483647;
	void acceptSecContext(java.io.InputStream var0, java.io.OutputStream var1) throws org.ietf.jgss.GSSException;
	byte[] acceptSecContext(byte[] var0, int var1, int var2) throws org.ietf.jgss.GSSException;
	void dispose() throws org.ietf.jgss.GSSException;
	byte[] export() throws org.ietf.jgss.GSSException;
	boolean getAnonymityState();
	boolean getConfState();
	boolean getCredDelegState();
	org.ietf.jgss.GSSCredential getDelegCred() throws org.ietf.jgss.GSSException;
	boolean getIntegState();
	int getLifetime();
	void getMIC(java.io.InputStream var0, java.io.OutputStream var1, org.ietf.jgss.MessageProp var2) throws org.ietf.jgss.GSSException;
	byte[] getMIC(byte[] var0, int var1, int var2, org.ietf.jgss.MessageProp var3) throws org.ietf.jgss.GSSException;
	org.ietf.jgss.Oid getMech() throws org.ietf.jgss.GSSException;
	boolean getMutualAuthState();
	boolean getReplayDetState();
	boolean getSequenceDetState();
	org.ietf.jgss.GSSName getSrcName() throws org.ietf.jgss.GSSException;
	org.ietf.jgss.GSSName getTargName() throws org.ietf.jgss.GSSException;
	int getWrapSizeLimit(int var0, boolean var1, int var2) throws org.ietf.jgss.GSSException;
	int initSecContext(java.io.InputStream var0, java.io.OutputStream var1) throws org.ietf.jgss.GSSException;
	byte[] initSecContext(byte[] var0, int var1, int var2) throws org.ietf.jgss.GSSException;
	boolean isEstablished();
	boolean isInitiator() throws org.ietf.jgss.GSSException;
	boolean isProtReady();
	boolean isTransferable() throws org.ietf.jgss.GSSException;
	void requestAnonymity(boolean var0) throws org.ietf.jgss.GSSException;
	void requestConf(boolean var0) throws org.ietf.jgss.GSSException;
	void requestCredDeleg(boolean var0) throws org.ietf.jgss.GSSException;
	void requestInteg(boolean var0) throws org.ietf.jgss.GSSException;
	void requestLifetime(int var0) throws org.ietf.jgss.GSSException;
	void requestMutualAuth(boolean var0) throws org.ietf.jgss.GSSException;
	void requestReplayDet(boolean var0) throws org.ietf.jgss.GSSException;
	void requestSequenceDet(boolean var0) throws org.ietf.jgss.GSSException;
	void setChannelBinding(org.ietf.jgss.ChannelBinding var0) throws org.ietf.jgss.GSSException;
	void unwrap(java.io.InputStream var0, java.io.OutputStream var1, org.ietf.jgss.MessageProp var2) throws org.ietf.jgss.GSSException;
	byte[] unwrap(byte[] var0, int var1, int var2, org.ietf.jgss.MessageProp var3) throws org.ietf.jgss.GSSException;
	void verifyMIC(java.io.InputStream var0, java.io.InputStream var1, org.ietf.jgss.MessageProp var2) throws org.ietf.jgss.GSSException;
	void verifyMIC(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, org.ietf.jgss.MessageProp var6) throws org.ietf.jgss.GSSException;
	void wrap(java.io.InputStream var0, java.io.OutputStream var1, org.ietf.jgss.MessageProp var2) throws org.ietf.jgss.GSSException;
	byte[] wrap(byte[] var0, int var1, int var2, org.ietf.jgss.MessageProp var3) throws org.ietf.jgss.GSSException;
}

