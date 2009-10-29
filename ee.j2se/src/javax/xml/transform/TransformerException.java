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

package javax.xml.transform;
public class TransformerException extends java.lang.Exception {
	public TransformerException(java.lang.String var0) { }
	public TransformerException(java.lang.String var0, java.lang.Throwable var1) { }
	public TransformerException(java.lang.String var0, javax.xml.transform.SourceLocator var1) { }
	public TransformerException(java.lang.String var0, javax.xml.transform.SourceLocator var1, java.lang.Throwable var2) { }
	public TransformerException(java.lang.Throwable var0) { }
	public java.lang.Throwable getException() { return null; }
	public java.lang.String getLocationAsString() { return null; }
	public javax.xml.transform.SourceLocator getLocator() { return null; }
	public java.lang.String getMessageAndLocation() { return null; }
	public void setLocator(javax.xml.transform.SourceLocator var0) { }
}

