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

package javax.lang.model.util;
public interface Elements {
	java.util.List<? extends javax.lang.model.element.AnnotationMirror> getAllAnnotationMirrors(javax.lang.model.element.Element var0);
	java.util.List<? extends javax.lang.model.element.Element> getAllMembers(javax.lang.model.element.TypeElement var0);
	javax.lang.model.element.Name getBinaryName(javax.lang.model.element.TypeElement var0);
	java.lang.String getConstantExpression(java.lang.Object var0);
	java.lang.String getDocComment(javax.lang.model.element.Element var0);
	java.util.Map<? extends javax.lang.model.element.ExecutableElement,? extends javax.lang.model.element.AnnotationValue> getElementValuesWithDefaults(javax.lang.model.element.AnnotationMirror var0);
	javax.lang.model.element.Name getName(java.lang.CharSequence var0);
	javax.lang.model.element.PackageElement getPackageElement(java.lang.CharSequence var0);
	javax.lang.model.element.PackageElement getPackageOf(javax.lang.model.element.Element var0);
	javax.lang.model.element.TypeElement getTypeElement(java.lang.CharSequence var0);
	boolean hides(javax.lang.model.element.Element var0, javax.lang.model.element.Element var1);
	boolean isDeprecated(javax.lang.model.element.Element var0);
	boolean overrides(javax.lang.model.element.ExecutableElement var0, javax.lang.model.element.ExecutableElement var1, javax.lang.model.element.TypeElement var2);
	void printElements(java.io.Writer var0, javax.lang.model.element.Element... var1);
}

