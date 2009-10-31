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

package javax.activation;
public class MimeType implements java.io.Externalizable {
	public MimeType() { } 
	public MimeType(java.lang.String var0) throws javax.activation.MimeTypeParseException { } 
	public MimeType(java.lang.String var0, java.lang.String var1) throws javax.activation.MimeTypeParseException { } 
	public java.lang.String getBaseType() { return null; }
	public java.lang.String getParameter(java.lang.String var0) { return null; }
	public javax.activation.MimeTypeParameterList getParameters() { return null; }
	public java.lang.String getPrimaryType() { return null; }
	public java.lang.String getSubType() { return null; }
	public boolean match(java.lang.String var0) throws javax.activation.MimeTypeParseException { return false; }
	public boolean match(javax.activation.MimeType var0) { return false; }
	public void readExternal(java.io.ObjectInput var0) throws java.io.IOException, java.lang.ClassNotFoundException { }
	public void removeParameter(java.lang.String var0) { }
	public void setParameter(java.lang.String var0, java.lang.String var1) { }
	public void setPrimaryType(java.lang.String var0) throws javax.activation.MimeTypeParseException { }
	public void setSubType(java.lang.String var0) throws javax.activation.MimeTypeParseException { }
	public void writeExternal(java.io.ObjectOutput var0) throws java.io.IOException { }
}

