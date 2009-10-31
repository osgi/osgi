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

package javax.xml.bind.annotation;
public interface DomHandler<ElementT,ResultT extends javax.xml.transform.Result> {
	ResultT createUnmarshaller(javax.xml.bind.ValidationEventHandler var0);
	ElementT getElement(ResultT var0);
	javax.xml.transform.Source marshal(ElementT var0, javax.xml.bind.ValidationEventHandler var1);
}

