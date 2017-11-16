/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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
package org.osgi.test.cases.remoteservices.common;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.Version;

public interface BasicTypes {

	boolean getBooleanPrimitive(boolean b);

	Boolean getBoolean(Boolean b);

	boolean[] getBooleanPrimitiveArray(boolean[] b);

	Boolean[] getBooleanArray(Boolean[] b);

	List<Boolean> getBooleanList(List<Boolean> b);

	Set<Boolean> getBooleanSet(Set<Boolean> b);

	byte getBytePrimitive(byte b);

	Byte getByte(Byte b);

	byte[] getBytePrimitiveArray(byte[] b);

	Byte[] getByteArray(Byte[] b);

	List<Byte> getByteList(List<Byte> b);

	Set<Byte> getByteSet(Set<Byte> b);

	short getShortPrimitive(short s);

	Short getShort(Short s);

	short[] getShortPrimitiveArray(short[] b);

	Short[] getShortArray(Short[] b);

	List<Short> getShortList(List<Short> b);

	Set<Short> getShortSet(Set<Short> b);

	char getCharPrimitive(char c);

	Character getChar(Character c);

	char[] getCharacterPrimitiveArray(char[] b);

	Character[] getCharacterArray(Character[] b);

	List<Character> getCharacterList(List<Character> b);

	Set<Character> getCharacterSet(Set<Character> b);

	int getIntPrimitive(int i);

	Integer getInt(Integer i);

	int[] getIntPrimitiveArray(int[] b);

	Integer[] getIntegerArray(Integer[] b);

	List<Integer> getIntegerList(List<Integer> b);

	Set<Integer> getIntegerSet(Set<Integer> b);

	long getLongPrimitive(long l);

	Long getLong(Long l);

	long[] getLongPrimitiveArray(long[] b);

	Long[] getLongArray(Long[] b);

	List<Long> getLongList(List<Long> b);

	Set<Long> getLongSet(Set<Long> b);

	float getFloatPrimitive(float f);

	Float getFloat(Float f);

	float[] getFloatPrimitiveArray(float[] b);

	Float[] getFloatArray(Float[] b);

	List<Float> getFloatList(List<Float> b);

	Set<Float> getFloatSet(Set<Float> b);

	double getDoublePrimitive(double d);

	Double getDouble(Double d);

	double[] getDoublePrimitiveArray(double[] b);

	Double[] getDoubleArray(Double[] b);

	List<Double> getDoubleList(List<Double> b);

	Set<Double> getDoubleSet(Set<Double> b);

	String getString(String s);

	String[] getStringArray(String[] b);

	List<String> getStringList(List<String> b);

	Set<String> getStringSet(Set<String> b);

	Version getVersion(Version v);

	Version[] getVersionArray(Version[] b);

	List<Version> getVersionList(List<Version> b);

	Set<Version> getVersionSet(Set<Version> b);

	TimeUnit getEnum(TimeUnit unit);

	TimeUnit[] getEnumArray(TimeUnit[] b);

	List<TimeUnit> getEnumList(List<TimeUnit> b);

	Set<TimeUnit> getEnumSet(Set<TimeUnit> b);

	DTOType getDTO(DTOType type);

	DTOType[] getDTOTypeArray(DTOType[] b);

	List<DTOType> getDTOTypeList(List<DTOType> b);

	Set<DTOType> getDTOTypeSet(Set<DTOType> b);

	void populateMap(Map<String,Integer> map);

	Map<String,Integer> getMap();
}
