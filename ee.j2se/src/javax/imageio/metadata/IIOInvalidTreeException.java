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

package javax.imageio.metadata;
public class IIOInvalidTreeException extends javax.imageio.IIOException {
	public IIOInvalidTreeException(java.lang.String var0, java.lang.Throwable var1, org.w3c.dom.Node var2) { super((java.lang.String) null, (java.lang.Throwable) null); }
	public IIOInvalidTreeException(java.lang.String var0, org.w3c.dom.Node var1) { super((java.lang.String) null, (java.lang.Throwable) null); }
	public org.w3c.dom.Node getOffendingNode() { return null; }
	protected org.w3c.dom.Node offendingNode;
}

