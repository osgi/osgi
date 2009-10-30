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

package javax.naming.event;
public interface EventContext extends javax.naming.Context {
	public final static int OBJECT_SCOPE = 0;
	public final static int ONELEVEL_SCOPE = 1;
	public final static int SUBTREE_SCOPE = 2;
	void addNamingListener(java.lang.String var0, int var1, javax.naming.event.NamingListener var2) throws javax.naming.NamingException;
	void addNamingListener(javax.naming.Name var0, int var1, javax.naming.event.NamingListener var2) throws javax.naming.NamingException;
	void removeNamingListener(javax.naming.event.NamingListener var0) throws javax.naming.NamingException;
	boolean targetMustExist() throws javax.naming.NamingException;
}

