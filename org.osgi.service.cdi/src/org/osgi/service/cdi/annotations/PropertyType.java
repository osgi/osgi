/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
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

package org.osgi.service.cdi.annotations;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * The possible property types available to {@link ServiceProperty} instances.
 */
public enum PropertyType {

	/**
	 * <code>java.lang.Boolean</code>
	 */
	Boolean("Boolean", new TypeRef<Boolean>() {/**/}),

	/**
	 * <code>java.lang.Byte</code>
	 */
	Byte("Byte", new TypeRef<Byte>() {/**/}),

	/**
	 * <code>java.lang.Character</code>
	 */
	Character("Character", new TypeRef<Character>() {/**/}),

	/**
	 * <code>java.lang.Double</code>
	 */
	Double("Double", new TypeRef<Double>() {/**/}),

	/**
	 * <code>java.lang.Float</code>
	 */
	Float("Float", new TypeRef<Float>() {/**/}),

	/**
	 * <code>java.lang.Integer</code>
	 */
	Integer("Integer", new TypeRef<Integer>() {/**/}),

	/**
	 * <code>java.lang.Long</code>
	 */
	Long("Long", new TypeRef<Long>() {/**/}),

	/**
	 * <code>java.lang.Short</code>
	 */
	Short("Short", new TypeRef<Short>() {/**/}),

	/**
	 * <code>java.lang.String</code>
	 */
	String("String", new TypeRef<String>() {/**/}),

	/**
	 * <code>java.lang.Boolean[]</code>
	 */
	Boolean_Array("Boolean[]", new TypeRef<Boolean[]>() {/**/}),

	/**
	 * <code>java.lang.Byte[]</code>
	 */
	Byte_Array("Byte[]", new TypeRef<Byte[]>() {/**/}),

	/**
	 * <code>java.lang.Character[]</code>
	 */
	Character_Array("Character[]", new TypeRef<Character[]>() {/**/}),

	/**
	 * <code>java.lang.Double[]</code>
	 */
	Double_Array("Double[]", new TypeRef<Double[]>() {/**/}),

	/**
	 * <code>java.lang.Float[]</code>
	 */
	Float_Array("Float[]", new TypeRef<Float[]>() {/**/}),

	/**
	 * <code>java.lang.Integer[]</code>
	 */
	Integer_Array("Integer[]", new TypeRef<Integer[]>() {/**/}),

	/**
	 * <code>java.lang.Long[]</code>
	 */
	Long_Array("Long[]", new TypeRef<Long[]>() {/**/}),

	/**
	 * <code>java.lang.Short[]</code>
	 */
	Short_Array("Short[]", new TypeRef<Short[]>() {/**/}),

	/**
	 * <code>java.lang.String[]</code>
	 */
	String_Array("String[]", new TypeRef<String[]>() {/**/}),

	/**
	 * <code>java.util.List&lt;Boolean&gt;</code>
	 */
	Boolean_List("List<Boolean>", new TypeRef<List<Boolean>>() {/**/}),

	/**
	 * <code>java.util.List&lt;Byte&gt;</code>
	 */
	Byte_List("List<Byte>", new TypeRef<List<Byte>>() {/**/}),

	/**
	 * <code>java.util.List&lt;Character&gt;</code>
	 */
	Character_List("List<Character>", new TypeRef<List<Character>>() {/**/}),

	/**
	 * <code>java.util.List&lt;Double&gt;</code>
	 */
	Double_List("List<Double>", new TypeRef<List<Double>>() {/**/}),

	/**
	 * <code>java.util.List&lt;Float&gt;</code>
	 */
	Float_List("List<Float>", new TypeRef<List<Float>>() {/**/}),

	/**
	 * <code>java.util.List&lt;Integer&gt;</code>
	 */
	Integer_List("List<Integer>", new TypeRef<List<Integer>>() {/**/}),

	/**
	 * <code>java.util.List&lt;Long&gt;</code>
	 */
	Long_List("List<Long>", new TypeRef<List<Long>>() {/**/}),

	/**
	 * <code>java.util.List&lt;Short&gt;</code>
	 */
	Short_List("List<Short>", new TypeRef<List<Short>>() {/**/}),

	/**
	 * <code>java.util.List&lt;String&gt;</code>
	 */
	String_List("List<String>", new TypeRef<List<String>>() {/**/}),

	/**
	 * <code>java.util.Set&lt;Boolean&gt;</code>
	 */
	Boolean_Set("Set<Boolean>", new TypeRef<Set<Boolean>>() {/**/}),

	/**
	 * <code>java.util.Set&lt;Byte&gt;</code>
	 */
	Byte_Set("Set<Byte>", new TypeRef<Set<Byte>>() {/**/}),

	/**
	 * <code>java.util.Set&lt;Character&gt;</code>
	 */
	Character_Set("Set<Character>", new TypeRef<Set<Character>>() {/**/}),

	/**
	 * <code>java.util.Set&lt;Double&gt;</code>
	 */
	Double_Set("Set<Double>", new TypeRef<Set<Double>>() {/**/}),

	/**
	 * <code>java.util.Set&lt;Float&gt;</code>
	 */
	Float_Set("Set<Float>", new TypeRef<Set<Float>>() {/**/}),

	/**
	 * <code>java.util.Set&lt;Integer&gt;</code>
	 */
	Integer_Set("Set<Integer>", new TypeRef<Set<Integer>>() {/**/}),

	/**
	 * <code>java.util.Set&lt;Long&gt;</code>
	 */
	Long_Set("Set<Long>", new TypeRef<Set<Long>>() {/**/}),

	/**
	 * <code>java.util.Set&lt;Short&gt;</code>
	 */
	Short_Set("Set<Short>", new TypeRef<Set<Short>>() {/**/}),

	/**
	 * <code>java.util.Set&lt;String&gt;</code>
	 */
	String_Set("Set<String>", new TypeRef<Set<String>>() {/**/});

	PropertyType(String value, TypeRef<?> typeRef) {
		this.value = value;
		this.typeRef = typeRef;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return typeRef.getType();
	}

	@Override
	public java.lang.String toString() {
		return value;
	}

	private final TypeRef<?> typeRef;
	private final String value;

	private static class TypeRef<T> {

		public TypeRef() {
		}

		Type getType() {
			return ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
		}

	}

}
