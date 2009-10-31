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
public interface Element {
	<R,P> R accept(javax.lang.model.element.ElementVisitor<R,P> var0, P var1);
	javax.lang.model.type.TypeMirror asType();
	boolean equals(java.lang.Object var0);
	<A extends java.lang.annotation.Annotation> A getAnnotation(java.lang.Class<A> var0);
	java.util.List<? extends javax.lang.model.element.AnnotationMirror> getAnnotationMirrors();
	java.util.List<? extends javax.lang.model.element.Element> getEnclosedElements();
	javax.lang.model.element.Element getEnclosingElement();
	javax.lang.model.element.ElementKind getKind();
	java.util.Set<javax.lang.model.element.Modifier> getModifiers();
	javax.lang.model.element.Name getSimpleName();
	int hashCode();
}

