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

package java.security;
public class KeyRep implements java.io.Serializable {
	public enum Type {
		PRIVATE,
		PUBLIC,
		SECRET;
	}
	public KeyRep(java.security.KeyRep.Type var0, java.lang.String var1, java.lang.String var2, byte[] var3) { } 
	protected java.lang.Object readResolve() throws java.io.ObjectStreamException { return null; }
}

