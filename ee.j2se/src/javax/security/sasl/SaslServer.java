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
public interface SaslServer {
	void dispose() throws javax.security.sasl.SaslException;
	byte[] evaluateResponse(byte[] var0) throws javax.security.sasl.SaslException;
	java.lang.String getAuthorizationID();
	java.lang.String getMechanismName();
	java.lang.Object getNegotiatedProperty(java.lang.String var0);
	boolean isComplete();
	byte[] unwrap(byte[] var0, int var1, int var2) throws javax.security.sasl.SaslException;
	byte[] wrap(byte[] var0, int var1, int var2) throws javax.security.sasl.SaslException;
}

