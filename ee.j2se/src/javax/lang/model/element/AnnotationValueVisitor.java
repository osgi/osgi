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
public interface AnnotationValueVisitor<R,P> {
	R visit(javax.lang.model.element.AnnotationValue var0);
	R visit(javax.lang.model.element.AnnotationValue var0, P var1);
	R visitAnnotation(javax.lang.model.element.AnnotationMirror var0, P var1);
	R visitArray(java.util.List<? extends javax.lang.model.element.AnnotationValue> var0, P var1);
	R visitBoolean(boolean var0, P var1);
	R visitByte(byte var0, P var1);
	R visitChar(char var0, P var1);
	R visitDouble(double var0, P var1);
	R visitEnumConstant(javax.lang.model.element.VariableElement var0, P var1);
	R visitFloat(float var0, P var1);
	R visitInt(int var0, P var1);
	R visitLong(long var0, P var1);
	R visitShort(short var0, P var1);
	R visitString(java.lang.String var0, P var1);
	R visitType(javax.lang.model.type.TypeMirror var0, P var1);
	R visitUnknown(javax.lang.model.element.AnnotationValue var0, P var1);
}

