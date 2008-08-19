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

package javax.security.auth;
/** @deprecated */ public abstract class Policy {
	protected Policy() { }
	public abstract java.security.PermissionCollection getPermissions(javax.security.auth.Subject var0, java.security.CodeSource var1);
	public static javax.security.auth.Policy getPolicy() { return null; }
	public abstract void refresh();
	public static void setPolicy(javax.security.auth.Policy var0) { }
}

