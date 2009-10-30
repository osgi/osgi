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

package javax.security.auth.kerberos;
public final class KerberosPrincipal implements java.io.Serializable, java.security.Principal {
	public final static int KRB_NT_PRINCIPAL = 1;
	public final static int KRB_NT_SRV_HST = 3;
	public final static int KRB_NT_SRV_INST = 2;
	public final static int KRB_NT_SRV_XHST = 4;
	public final static int KRB_NT_UID = 5;
	public final static int KRB_NT_UNKNOWN = 0;
	public KerberosPrincipal(java.lang.String var0) { } 
	public KerberosPrincipal(java.lang.String var0, int var1) { } 
	public java.lang.String getName() { return null; }
	public int getNameType() { return 0; }
	public java.lang.String getRealm() { return null; }
	public int hashCode() { return 0; }
}

