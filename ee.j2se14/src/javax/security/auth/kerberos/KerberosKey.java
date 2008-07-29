/*
 * $Date$
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

package javax.security.auth.kerberos;
public class KerberosKey implements javax.crypto.SecretKey, javax.security.auth.Destroyable {
	public KerberosKey(javax.security.auth.kerberos.KerberosPrincipal var0, byte[] var1, int var2, int var3) { }
	public KerberosKey(javax.security.auth.kerberos.KerberosPrincipal var0, char[] var1, java.lang.String var2) { }
	public void destroy() throws javax.security.auth.DestroyFailedException { }
	public final java.lang.String getAlgorithm() { return null; }
	public final byte[] getEncoded() { return null; }
	public final java.lang.String getFormat() { return null; }
	public final int getKeyType() { return 0; }
	public final javax.security.auth.kerberos.KerberosPrincipal getPrincipal() { return null; }
	public final int getVersionNumber() { return 0; }
	public boolean isDestroyed() { return false; }
}

