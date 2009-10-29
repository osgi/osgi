/*
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
public abstract interface DirStateFactory extends javax.naming.spi.StateFactory {
	public abstract javax.naming.spi.DirStateFactory.Result getStateToBind(java.lang.Object var0, javax.naming.Name var1, javax.naming.Context var2, java.util.Hashtable var3, javax.naming.directory.Attributes var4) throws javax.naming.NamingException;
	public static class Result {
		public Result(java.lang.Object var0, javax.naming.directory.Attributes var1) { }
		public javax.naming.directory.Attributes getAttributes() { return null; }
		public java.lang.Object getObject() { return null; }
	}
}

