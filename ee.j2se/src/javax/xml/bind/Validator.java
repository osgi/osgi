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

package javax.xml.bind;
/** @deprecated */
public interface Validator {
	/** @deprecated */
	javax.xml.bind.ValidationEventHandler getEventHandler() throws javax.xml.bind.JAXBException;
	/** @deprecated */
	java.lang.Object getProperty(java.lang.String var0) throws javax.xml.bind.PropertyException;
	/** @deprecated */
	void setEventHandler(javax.xml.bind.ValidationEventHandler var0) throws javax.xml.bind.JAXBException;
	/** @deprecated */
	void setProperty(java.lang.String var0, java.lang.Object var1) throws javax.xml.bind.PropertyException;
	/** @deprecated */
	boolean validate(java.lang.Object var0) throws javax.xml.bind.JAXBException;
	/** @deprecated */
	boolean validateRoot(java.lang.Object var0) throws javax.xml.bind.JAXBException;
}

