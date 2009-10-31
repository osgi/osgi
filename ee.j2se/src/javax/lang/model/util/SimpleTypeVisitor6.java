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
@javax.annotation.processing.SupportedSourceVersion(value=javax.lang.model.SourceVersion.RELEASE_6)
public class SimpleTypeVisitor6<R,P> extends javax.lang.model.util.AbstractTypeVisitor6<R,P> {
	protected final R DEFAULT_VALUE; { DEFAULT_VALUE = null; }
	protected SimpleTypeVisitor6() { } 
	protected SimpleTypeVisitor6(R var0) { } 
	protected R defaultAction(javax.lang.model.type.TypeMirror var0, P var1) { return null; }
	public R visitArray(javax.lang.model.type.ArrayType var0, P var1) { return null; }
	public R visitDeclared(javax.lang.model.type.DeclaredType var0, P var1) { return null; }
	public R visitError(javax.lang.model.type.ErrorType var0, P var1) { return null; }
	public R visitExecutable(javax.lang.model.type.ExecutableType var0, P var1) { return null; }
	public R visitNoType(javax.lang.model.type.NoType var0, P var1) { return null; }
	public R visitNull(javax.lang.model.type.NullType var0, P var1) { return null; }
	public R visitPrimitive(javax.lang.model.type.PrimitiveType var0, P var1) { return null; }
	public R visitTypeVariable(javax.lang.model.type.TypeVariable var0, P var1) { return null; }
	public R visitWildcard(javax.lang.model.type.WildcardType var0, P var1) { return null; }
}

