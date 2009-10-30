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

package javax.security.sasl;
public interface SaslClientFactory {
	javax.security.sasl.SaslClient createSaslClient(java.lang.String[] var0, java.lang.String var1, java.lang.String var2, java.lang.String var3, java.util.Map<java.lang.String,?> var4, javax.security.auth.callback.CallbackHandler var5) throws javax.security.sasl.SaslException;
	java.lang.String[] getMechanismNames(java.util.Map<java.lang.String,?> var0);
}

