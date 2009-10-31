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
public interface Types {
	javax.lang.model.element.Element asElement(javax.lang.model.type.TypeMirror var0);
	javax.lang.model.type.TypeMirror asMemberOf(javax.lang.model.type.DeclaredType var0, javax.lang.model.element.Element var1);
	javax.lang.model.element.TypeElement boxedClass(javax.lang.model.type.PrimitiveType var0);
	javax.lang.model.type.TypeMirror capture(javax.lang.model.type.TypeMirror var0);
	boolean contains(javax.lang.model.type.TypeMirror var0, javax.lang.model.type.TypeMirror var1);
	java.util.List<? extends javax.lang.model.type.TypeMirror> directSupertypes(javax.lang.model.type.TypeMirror var0);
	javax.lang.model.type.TypeMirror erasure(javax.lang.model.type.TypeMirror var0);
	javax.lang.model.type.ArrayType getArrayType(javax.lang.model.type.TypeMirror var0);
	javax.lang.model.type.DeclaredType getDeclaredType(javax.lang.model.element.TypeElement var0, javax.lang.model.type.TypeMirror... var1);
	javax.lang.model.type.DeclaredType getDeclaredType(javax.lang.model.type.DeclaredType var0, javax.lang.model.element.TypeElement var1, javax.lang.model.type.TypeMirror... var2);
	javax.lang.model.type.NoType getNoType(javax.lang.model.type.TypeKind var0);
	javax.lang.model.type.NullType getNullType();
	javax.lang.model.type.PrimitiveType getPrimitiveType(javax.lang.model.type.TypeKind var0);
	javax.lang.model.type.WildcardType getWildcardType(javax.lang.model.type.TypeMirror var0, javax.lang.model.type.TypeMirror var1);
	boolean isAssignable(javax.lang.model.type.TypeMirror var0, javax.lang.model.type.TypeMirror var1);
	boolean isSameType(javax.lang.model.type.TypeMirror var0, javax.lang.model.type.TypeMirror var1);
	boolean isSubsignature(javax.lang.model.type.ExecutableType var0, javax.lang.model.type.ExecutableType var1);
	boolean isSubtype(javax.lang.model.type.TypeMirror var0, javax.lang.model.type.TypeMirror var1);
	javax.lang.model.type.PrimitiveType unboxedType(javax.lang.model.type.TypeMirror var0);
}

