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

package javax.xml.bind.helpers;
public class ValidationEventImpl implements javax.xml.bind.ValidationEvent {
	public ValidationEventImpl(int var0, java.lang.String var1, javax.xml.bind.ValidationEventLocator var2) { } 
	public ValidationEventImpl(int var0, java.lang.String var1, javax.xml.bind.ValidationEventLocator var2, java.lang.Throwable var3) { } 
	public java.lang.Throwable getLinkedException() { return null; }
	public javax.xml.bind.ValidationEventLocator getLocator() { return null; }
	public java.lang.String getMessage() { return null; }
	public int getSeverity() { return 0; }
	public void setLinkedException(java.lang.Throwable var0) { }
	public void setLocator(javax.xml.bind.ValidationEventLocator var0) { }
	public void setMessage(java.lang.String var0) { }
	public void setSeverity(int var0) { }
}

