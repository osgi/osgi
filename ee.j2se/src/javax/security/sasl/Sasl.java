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
public class Sasl {
	public final static java.lang.String CREDENTIALS = "javax.security.sasl.credentials";
	public final static java.lang.String MAX_BUFFER = "javax.security.sasl.maxbuffer";
	public final static java.lang.String POLICY_FORWARD_SECRECY = "javax.security.sasl.policy.forward";
	public final static java.lang.String POLICY_NOACTIVE = "javax.security.sasl.policy.noactive";
	public final static java.lang.String POLICY_NOANONYMOUS = "javax.security.sasl.policy.noanonymous";
	public final static java.lang.String POLICY_NODICTIONARY = "javax.security.sasl.policy.nodictionary";
	public final static java.lang.String POLICY_NOPLAINTEXT = "javax.security.sasl.policy.noplaintext";
	public final static java.lang.String POLICY_PASS_CREDENTIALS = "javax.security.sasl.policy.credentials";
	public final static java.lang.String QOP = "javax.security.sasl.qop";
	public final static java.lang.String RAW_SEND_SIZE = "javax.security.sasl.rawsendsize";
	public final static java.lang.String REUSE = "javax.security.sasl.reuse";
	public final static java.lang.String SERVER_AUTH = "javax.security.sasl.server.authentication";
	public final static java.lang.String STRENGTH = "javax.security.sasl.strength";
	public static javax.security.sasl.SaslClient createSaslClient(java.lang.String[] var0, java.lang.String var1, java.lang.String var2, java.lang.String var3, java.util.Map<java.lang.String,?> var4, javax.security.auth.callback.CallbackHandler var5) throws javax.security.sasl.SaslException { return null; }
	public static javax.security.sasl.SaslServer createSaslServer(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.util.Map<java.lang.String,?> var3, javax.security.auth.callback.CallbackHandler var4) throws javax.security.sasl.SaslException { return null; }
	public static java.util.Enumeration<javax.security.sasl.SaslClientFactory> getSaslClientFactories() { return null; }
	public static java.util.Enumeration<javax.security.sasl.SaslServerFactory> getSaslServerFactories() { return null; }
	private Sasl() { } /* generated constructor to prevent compiler adding default public constructor */
}

