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

package javax.security.auth.login;
public abstract class Configuration {
	public interface Parameters {
	}
	protected Configuration() { } 
	public abstract javax.security.auth.login.AppConfigurationEntry[] getAppConfigurationEntry(java.lang.String var0);
	public static javax.security.auth.login.Configuration getConfiguration() { return null; }
	public static javax.security.auth.login.Configuration getInstance(java.lang.String var0, javax.security.auth.login.Configuration.Parameters var1) throws java.security.NoSuchAlgorithmException { return null; }
	public static javax.security.auth.login.Configuration getInstance(java.lang.String var0, javax.security.auth.login.Configuration.Parameters var1, java.lang.String var2) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
	public static javax.security.auth.login.Configuration getInstance(java.lang.String var0, javax.security.auth.login.Configuration.Parameters var1, java.security.Provider var2) throws java.security.NoSuchAlgorithmException { return null; }
	public javax.security.auth.login.Configuration.Parameters getParameters() { return null; }
	public java.security.Provider getProvider() { return null; }
	public java.lang.String getType() { return null; }
	public void refresh() { }
	public static void setConfiguration(javax.security.auth.login.Configuration var0) { }
}

