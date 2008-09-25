/*
 * $Revision$
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

package javax.naming.spi;
public class NamingManager {
	public static javax.naming.Context getContinuationContext(javax.naming.CannotProceedException var0) throws javax.naming.NamingException { return null; }
	public static javax.naming.Context getInitialContext(java.util.Hashtable var0) throws javax.naming.NamingException { return null; }
	public static java.lang.Object getObjectInstance(java.lang.Object var0, javax.naming.Name var1, javax.naming.Context var2, java.util.Hashtable var3) throws java.lang.Exception { return null; }
	public static java.lang.Object getStateToBind(java.lang.Object var0, javax.naming.Name var1, javax.naming.Context var2, java.util.Hashtable var3) throws javax.naming.NamingException { return null; }
	public static javax.naming.Context getURLContext(java.lang.String var0, java.util.Hashtable var1) throws javax.naming.NamingException { return null; }
	public static boolean hasInitialContextFactoryBuilder() { return false; }
	public static void setInitialContextFactoryBuilder(javax.naming.spi.InitialContextFactoryBuilder var0) throws javax.naming.NamingException { }
	public static void setObjectFactoryBuilder(javax.naming.spi.ObjectFactoryBuilder var0) throws javax.naming.NamingException { }
	public final static java.lang.String CPE = "java.naming.spi.CannotProceedException";
	NamingManager() { } /* generated constructor to prevent compiler adding default public constructor */
}

