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
public abstract class X509ExtendedKeyManager implements javax.net.ssl.X509KeyManager {
	protected X509ExtendedKeyManager() { } 
	public java.lang.String chooseEngineClientAlias(java.lang.String[] var0, java.security.Principal[] var1, javax.net.ssl.SSLEngine var2) { return null; }
	public java.lang.String chooseEngineServerAlias(java.lang.String var0, java.security.Principal[] var1, javax.net.ssl.SSLEngine var2) { return null; }
}

