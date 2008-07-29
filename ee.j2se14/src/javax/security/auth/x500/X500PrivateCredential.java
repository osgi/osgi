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

package javax.security.auth.x500;
public final class X500PrivateCredential implements javax.security.auth.Destroyable {
	public X500PrivateCredential(java.security.cert.X509Certificate var0, java.security.PrivateKey var1) { }
	public X500PrivateCredential(java.security.cert.X509Certificate var0, java.security.PrivateKey var1, java.lang.String var2) { }
	public void destroy() { }
	public java.lang.String getAlias() { return null; }
	public java.security.cert.X509Certificate getCertificate() { return null; }
	public java.security.PrivateKey getPrivateKey() { return null; }
	public boolean isDestroyed() { return false; }
}

