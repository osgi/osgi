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

package javax.xml.soap;
public interface SOAPHeaderElement extends javax.xml.soap.SOAPElement {
	java.lang.String getActor();
	boolean getMustUnderstand();
	boolean getRelay();
	java.lang.String getRole();
	void setActor(java.lang.String var0);
	void setMustUnderstand(boolean var0);
	void setRelay(boolean var0) throws javax.xml.soap.SOAPException;
	void setRole(java.lang.String var0) throws javax.xml.soap.SOAPException;
}

