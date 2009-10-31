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

package javax.lang.model.element;
public interface TypeElement extends javax.lang.model.element.Element {
	java.util.List<? extends javax.lang.model.type.TypeMirror> getInterfaces();
	javax.lang.model.element.NestingKind getNestingKind();
	javax.lang.model.element.Name getQualifiedName();
	javax.lang.model.type.TypeMirror getSuperclass();
	java.util.List<? extends javax.lang.model.element.TypeParameterElement> getTypeParameters();
}

