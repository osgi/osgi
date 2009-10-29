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

package javax.security.auth.kerberos;
public class KerberosTicket implements java.io.Serializable, javax.security.auth.Destroyable, javax.security.auth.Refreshable {
	public KerberosTicket(byte[] var0, javax.security.auth.kerberos.KerberosPrincipal var1, javax.security.auth.kerberos.KerberosPrincipal var2, byte[] var3, int var4, boolean[] var5, java.util.Date var6, java.util.Date var7, java.util.Date var8, java.util.Date var9, java.net.InetAddress[] var10) { }
	public void destroy() throws javax.security.auth.DestroyFailedException { }
	public final java.util.Date getAuthTime() { return null; }
	public final javax.security.auth.kerberos.KerberosPrincipal getClient() { return null; }
	public final java.net.InetAddress[] getClientAddresses() { return null; }
	public final byte[] getEncoded() { return null; }
	public final java.util.Date getEndTime() { return null; }
	public final boolean[] getFlags() { return null; }
	public final java.util.Date getRenewTill() { return null; }
	public final javax.security.auth.kerberos.KerberosPrincipal getServer() { return null; }
	public final javax.crypto.SecretKey getSessionKey() { return null; }
	public final int getSessionKeyType() { return 0; }
	public final java.util.Date getStartTime() { return null; }
	public boolean isCurrent() { return false; }
	public boolean isDestroyed() { return false; }
	public final boolean isForwardable() { return false; }
	public final boolean isForwarded() { return false; }
	public final boolean isInitial() { return false; }
	public final boolean isPostdated() { return false; }
	public final boolean isProxiable() { return false; }
	public final boolean isProxy() { return false; }
	public final boolean isRenewable() { return false; }
	public void refresh() throws javax.security.auth.RefreshFailedException { }
}

