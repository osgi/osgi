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

package javax.net.ssl;
public abstract class SSLEngine {
	protected SSLEngine() { } 
	protected SSLEngine(java.lang.String var0, int var1) { } 
	public abstract void beginHandshake() throws javax.net.ssl.SSLException;
	public abstract void closeInbound() throws javax.net.ssl.SSLException;
	public abstract void closeOutbound();
	public abstract java.lang.Runnable getDelegatedTask();
	public abstract boolean getEnableSessionCreation();
	public abstract java.lang.String[] getEnabledCipherSuites();
	public abstract java.lang.String[] getEnabledProtocols();
	public abstract javax.net.ssl.SSLEngineResult.HandshakeStatus getHandshakeStatus();
	public abstract boolean getNeedClientAuth();
	public java.lang.String getPeerHost() { return null; }
	public int getPeerPort() { return 0; }
	public javax.net.ssl.SSLParameters getSSLParameters() { return null; }
	public abstract javax.net.ssl.SSLSession getSession();
	public abstract java.lang.String[] getSupportedCipherSuites();
	public abstract java.lang.String[] getSupportedProtocols();
	public abstract boolean getUseClientMode();
	public abstract boolean getWantClientAuth();
	public abstract boolean isInboundDone();
	public abstract boolean isOutboundDone();
	public abstract void setEnableSessionCreation(boolean var0);
	public abstract void setEnabledCipherSuites(java.lang.String[] var0);
	public abstract void setEnabledProtocols(java.lang.String[] var0);
	public abstract void setNeedClientAuth(boolean var0);
	public void setSSLParameters(javax.net.ssl.SSLParameters var0) { }
	public abstract void setUseClientMode(boolean var0);
	public abstract void setWantClientAuth(boolean var0);
	public javax.net.ssl.SSLEngineResult unwrap(java.nio.ByteBuffer var0, java.nio.ByteBuffer var1) throws javax.net.ssl.SSLException { return null; }
	public javax.net.ssl.SSLEngineResult unwrap(java.nio.ByteBuffer var0, java.nio.ByteBuffer[] var1) throws javax.net.ssl.SSLException { return null; }
	public abstract javax.net.ssl.SSLEngineResult unwrap(java.nio.ByteBuffer var0, java.nio.ByteBuffer[] var1, int var2, int var3) throws javax.net.ssl.SSLException;
	public javax.net.ssl.SSLEngineResult wrap(java.nio.ByteBuffer var0, java.nio.ByteBuffer var1) throws javax.net.ssl.SSLException { return null; }
	public abstract javax.net.ssl.SSLEngineResult wrap(java.nio.ByteBuffer[] var0, int var1, int var2, java.nio.ByteBuffer var3) throws javax.net.ssl.SSLException;
	public javax.net.ssl.SSLEngineResult wrap(java.nio.ByteBuffer[] var0, java.nio.ByteBuffer var1) throws javax.net.ssl.SSLException { return null; }
}

