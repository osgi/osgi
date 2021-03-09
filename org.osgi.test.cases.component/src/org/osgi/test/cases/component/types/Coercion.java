/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.component.types;

public @interface Coercion {
	String stringString();

	String stringBoolean();

	String stringCharacter();

	String stringByte();

	String stringShort();

	String stringInteger();

	String stringLong();

	String stringFloat();

	String stringDouble();

	String stringCollection();

	String stringArray();

	String stringNone();

	boolean booleanString();

	boolean booleanBoolean();

	boolean booleanCharacter();

	boolean booleanByte();

	boolean booleanShort();

	boolean booleanInteger();

	boolean booleanLong();

	boolean booleanFloat();

	boolean booleanDouble();

	boolean booleanCollection();

	boolean booleanArray();

	boolean booleanNone();

	char charString();

	char charStringEmpty();

	char charBooleanTrue();

	char charBooleanFalse();

	char charCharacter();

	char charByte();

	char charShort();

	char charInteger();

	char charLong();

	char charFloat();

	char charDouble();

	char charCollection();

	char charArray();

	char charNone();

	byte byteString();

	byte byteBooleanTrue();

	byte byteBooleanFalse();

	byte byteCharacter();

	byte byteByte();

	byte byteShort();

	byte byteInteger();

	byte byteLong();

	byte byteFloat();

	byte byteDouble();

	byte byteCollection();

	byte byteArray();

	byte byteNone();

	short shortString();

	short shortBooleanTrue();

	short shortBooleanFalse();

	short shortCharacter();

	short shortByte();

	short shortShort();

	short shortInteger();

	short shortLong();

	short shortFloat();

	short shortDouble();

	short shortCollection();

	short shortArray();

	short shortNone();

	int intString();

	int intBooleanTrue();

	int intBooleanFalse();

	int intCharacter();

	int intByte();

	int intShort();

	int intInteger();

	int intLong();

	int intFloat();

	int intDouble();

	int intCollection();

	int intArray();

	int intNone();

	long longString();

	long longBooleanTrue();

	long longBooleanFalse();

	long longCharacter();

	long longByte();

	long longShort();

	long longInteger();

	long longLong();

	long longFloat();

	long longDouble();

	long longCollection();

	long longArray();

	long longNone();

	float floatString();

	float floatBooleanTrue();

	float floatBooleanFalse();

	float floatCharacter();

	float floatByte();

	float floatShort();

	float floatInteger();

	float floatLong();

	float floatFloat();

	float floatDouble();

	float floatCollection();

	float floatArray();

	float floatNone();

	double doubleString();

	double doubleBooleanTrue();

	double doubleBooleanFalse();

	double doubleCharacter();

	double doubleByte();

	double doubleShort();

	double doubleInteger();

	double doubleLong();

	double doubleFloat();

	double doubleDouble();

	double doubleCollection();

	double doubleArray();

	double doubleNone();

	Class<?>classBoolean();

	Class<?>classCharacter();

	Class<?>classByte();

	Class<?>classShort();

	Class<?>classInteger();

	Class<?>classLong();

	Class<?>classFloat();

	Class<?>classDouble();

	Class<?>classCollection();

	Class<?>classArray();

	Class<?>classNone();

	TestEnum enumBoolean();

	TestEnum enumCharacter();

	TestEnum enumByte();

	TestEnum enumShort();

	TestEnum enumInteger();

	TestEnum enumLong();

	TestEnum enumFloat();

	TestEnum enumDouble();

	TestEnum enumCollection();

	TestEnum enumArray();

	TestEnum enumNone();

	String[]string$$String();

	boolean[]boolean$$Boolean();

	char[]char$$Character();

	byte[]byte$$Byte();

	short[]short$$Short();

	int[]int$$Integer();

	long[]long$$Long();

	float[]float$$Float();

	double[]double$$Double();

	int[]int$$Collection();

	long[]long$$Array();

	String[]string$$None();
	
}
