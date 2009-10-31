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
public interface ElementVisitor<R,P> {
	R visit(javax.lang.model.element.Element var0);
	R visit(javax.lang.model.element.Element var0, P var1);
	R visitExecutable(javax.lang.model.element.ExecutableElement var0, P var1);
	R visitPackage(javax.lang.model.element.PackageElement var0, P var1);
	R visitType(javax.lang.model.element.TypeElement var0, P var1);
	R visitTypeParameter(javax.lang.model.element.TypeParameterElement var0, P var1);
	R visitUnknown(javax.lang.model.element.Element var0, P var1);
	R visitVariable(javax.lang.model.element.VariableElement var0, P var1);
}

