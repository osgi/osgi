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
public abstract class JAXBContext {
	public final static java.lang.String JAXB_CONTEXT_FACTORY = "javax.xml.bind.context.factory";
	protected JAXBContext() { } 
	public javax.xml.bind.Binder<org.w3c.dom.Node> createBinder() { return null; }
	public <T> javax.xml.bind.Binder<T> createBinder(java.lang.Class<T> var0) { return null; }
	public javax.xml.bind.JAXBIntrospector createJAXBIntrospector() { return null; }
	public abstract javax.xml.bind.Marshaller createMarshaller() throws javax.xml.bind.JAXBException;
	public abstract javax.xml.bind.Unmarshaller createUnmarshaller() throws javax.xml.bind.JAXBException;
	/** @deprecated */
	public abstract javax.xml.bind.Validator createValidator() throws javax.xml.bind.JAXBException;
	public void generateSchema(javax.xml.bind.SchemaOutputResolver var0) throws java.io.IOException { }
	public static javax.xml.bind.JAXBContext newInstance(java.lang.String var0) throws javax.xml.bind.JAXBException { return null; }
	public static javax.xml.bind.JAXBContext newInstance(java.lang.String var0, java.lang.ClassLoader var1) throws javax.xml.bind.JAXBException { return null; }
	public static javax.xml.bind.JAXBContext newInstance(java.lang.String var0, java.lang.ClassLoader var1, java.util.Map<java.lang.String,?> var2) throws javax.xml.bind.JAXBException { return null; }
	public static javax.xml.bind.JAXBContext newInstance(java.lang.Class... var0) throws javax.xml.bind.JAXBException { return null; }
	public static javax.xml.bind.JAXBContext newInstance(java.lang.Class[] var0, java.util.Map<java.lang.String,?> var1) throws javax.xml.bind.JAXBException { return null; }
}

