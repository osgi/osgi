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
public class LoginContext {
	public LoginContext(java.lang.String var0) throws javax.security.auth.login.LoginException { } 
	public LoginContext(java.lang.String var0, javax.security.auth.Subject var1) throws javax.security.auth.login.LoginException { } 
	public LoginContext(java.lang.String var0, javax.security.auth.Subject var1, javax.security.auth.callback.CallbackHandler var2) throws javax.security.auth.login.LoginException { } 
	public LoginContext(java.lang.String var0, javax.security.auth.Subject var1, javax.security.auth.callback.CallbackHandler var2, javax.security.auth.login.Configuration var3) throws javax.security.auth.login.LoginException { } 
	public LoginContext(java.lang.String var0, javax.security.auth.callback.CallbackHandler var1) throws javax.security.auth.login.LoginException { } 
	public javax.security.auth.Subject getSubject() { return null; }
	public void login() throws javax.security.auth.login.LoginException { }
	public void logout() throws javax.security.auth.login.LoginException { }
}

