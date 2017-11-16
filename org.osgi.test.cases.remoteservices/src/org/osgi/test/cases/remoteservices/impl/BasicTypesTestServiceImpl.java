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
package org.osgi.test.cases.remoteservices.impl;

import static java.util.Collections.reverse;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.Version;
import org.osgi.test.cases.remoteservices.common.BasicTypes;
import org.osgi.test.cases.remoteservices.common.DTOType;

public class BasicTypesTestServiceImpl implements BasicTypes {

	@Override
	public boolean getBooleanPrimitive(boolean b) {
		return !b;
	}

	@Override
	public Boolean getBoolean(Boolean b) {
		return !b;
	}

	@Override
	public boolean[] getBooleanPrimitiveArray(boolean[] b) {
		for (int i = 0; i < b.length; i++) {
			b[i] = !b[i];
		}
		return b;
	}

	@Override
	public Boolean[] getBooleanArray(Boolean[] b) {
		for (int i = 0; i < b.length; i++) {
			b[i] = !b[i];
		}
		return b;
	}

	@Override
	public List<Boolean> getBooleanList(List<Boolean> b) {
		List<Boolean> list = new ArrayList<>();
		for (Boolean x : b) {
			list.add(!x);
		}
		return list;
	}

	@Override
	public Set<Boolean> getBooleanSet(Set<Boolean> b) {
		Set<Boolean> list = new HashSet<>();
		for (Boolean x : b) {
			list.add(!x);
		}
		return list;
	}

	@Override
	public byte getBytePrimitive(byte b) {
		return (byte) -b;
	}

	@Override
	public Byte getByte(Byte b) {
		return (byte) -b;
	}

	@Override
	public byte[] getBytePrimitiveArray(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) -b[i];
		}
		return b;
	}

	@Override
	public Byte[] getByteArray(Byte[] b) {
		flip(b);
		return b;
	}

	private <T> void flip(T[] t) {
		reverse(Arrays.asList(t));
	}

	@Override
	public List<Byte> getByteList(List<Byte> b) {
		reverse(b);
		return b;
	}

	@Override
	public Set<Byte> getByteSet(Set<Byte> b) {
		return doubleSetValues(b, Byte.class);
	}

	@SuppressWarnings("unchecked")
	private <T extends Number> Set<T> doubleSetValues(Set<T> t, Class<T> cls) {
		Set<T> set = new HashSet<>();
		try {
			Method m = cls.getMethod("valueOf", String.class);
			for (T x : t) {
				set.add((T) m.invoke(null, String.valueOf(x.longValue() * 2)));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return set;
	}

	@Override
	public short getShortPrimitive(short s) {
		return (short) -s;
	}

	@Override
	public Short getShort(Short s) {
		return (short) -s;
	}

	@Override
	public short[] getShortPrimitiveArray(short[] b) {
		for (int i = 0; i < b.length; i++) {
			b[i] = (short) -b[i];
		}
		return b;
	}

	@Override
	public Short[] getShortArray(Short[] b) {
		flip(b);
		return b;
	}

	@Override
	public List<Short> getShortList(List<Short> b) {
		reverse(b);
		return b;
	}

	@Override
	public Set<Short> getShortSet(Set<Short> b) {
		return doubleSetValues(b, Short.class);
	}

	@Override
	public char getCharPrimitive(char c) {
		return (char) (c + 1);
	}

	@Override
	public Character getChar(Character c) {
		return (char) (c + 1);
	}

	@Override
	public char[] getCharacterPrimitiveArray(char[] b) {
		for (int i = 0; i < b.length; i++) {
			b[i] = (char) (b[i] + 1);
		}
		return b;
	}

	@Override
	public Character[] getCharacterArray(Character[] b) {
		flip(b);
		return b;
	}

	@Override
	public List<Character> getCharacterList(List<Character> b) {
		reverse(b);
		return b;
	}

	@Override
	public Set<Character> getCharacterSet(Set<Character> b) {
		Set<Character> set = new HashSet<>();
		for (Character x : b) {
			set.add(Character.toUpperCase(x));
		}
		return set;
	}

	@Override
	public int getIntPrimitive(int i) {
		return -i;
	}

	@Override
	public Integer getInt(Integer i) {
		return -i;
	}

	@Override
	public int[] getIntPrimitiveArray(int[] b) {
		for (int i = 0; i < b.length; i++) {
			b[i] = -b[i];
		}
		return b;
	}

	@Override
	public Integer[] getIntegerArray(Integer[] b) {
		flip(b);
		return b;
	}

	@Override
	public List<Integer> getIntegerList(List<Integer> b) {
		reverse(b);
		return b;
	}

	@Override
	public Set<Integer> getIntegerSet(Set<Integer> b) {
		return doubleSetValues(b, Integer.class);
	}

	@Override
	public long getLongPrimitive(long l) {
		return -l;
	}

	@Override
	public Long getLong(Long l) {
		return -l;
	}

	@Override
	public long[] getLongPrimitiveArray(long[] b) {
		for (int i = 0; i < b.length; i++) {
			b[i] = -b[i];
		}
		return b;
	}

	@Override
	public Long[] getLongArray(Long[] b) {
		flip(b);
		return b;
	}

	@Override
	public List<Long> getLongList(List<Long> b) {
		reverse(b);
		return b;
	}

	@Override
	public Set<Long> getLongSet(Set<Long> b) {
		return doubleSetValues(b, Long.class);
	}

	@Override
	public float getFloatPrimitive(float f) {
		return -f;
	}

	@Override
	public Float getFloat(Float f) {
		return -f;
	}

	@Override
	public float[] getFloatPrimitiveArray(float[] b) {
		for (int i = 0; i < b.length; i++) {
			b[i] = -b[i];
		}
		return b;
	}

	@Override
	public Float[] getFloatArray(Float[] b) {
		flip(b);
		return b;
	}

	@Override
	public List<Float> getFloatList(List<Float> b) {
		reverse(b);
		return b;
	}

	@Override
	public Set<Float> getFloatSet(Set<Float> b) {
		return doubleSetValues(b, Float.class);
	}

	@Override
	public double getDoublePrimitive(double d) {
		return -d;
	}

	@Override
	public Double getDouble(Double d) {
		return -d;
	}

	@Override
	public double[] getDoublePrimitiveArray(double[] b) {
		for (int i = 0; i < b.length; i++) {
			b[i] = -b[i];
		}
		return b;
	}

	@Override
	public Double[] getDoubleArray(Double[] b) {
		flip(b);
		return b;
	}

	@Override
	public List<Double> getDoubleList(List<Double> b) {
		reverse(b);
		return b;
	}

	@Override
	public Set<Double> getDoubleSet(Set<Double> b) {
		return doubleSetValues(b, Double.class);
	}

	@Override
	public String getString(String s) {
		return s.toUpperCase();
	}

	@Override
	public String[] getStringArray(String[] b) {
		flip(b);
		return b;
	}

	@Override
	public List<String> getStringList(List<String> b) {
		reverse(b);
		return b;
	}

	@Override
	public Set<String> getStringSet(Set<String> b) {
		Set<String> set = new HashSet<>();
		for (String x : b) {
			set.add(x.toUpperCase());
		}
		return set;
	}

	@Override
	public Version getVersion(Version v) {
		return bump(v);
	}

	private Version bump(Version v) {
		return new Version(v.getMajor() + 1, v.getMinor() + 1,
				v.getMicro() + 1);
	}

	@Override
	public Version[] getVersionArray(Version[] b) {
		flip(b);
		return b;
	}

	@Override
	public List<Version> getVersionList(List<Version> b) {
		reverse(b);
		return b;
	}

	@Override
	public Set<Version> getVersionSet(Set<Version> b) {
		Set<Version> set = new HashSet<>();
		for (Version x : b) {
			set.add(bump(x));
		}
		return set;
	}

	@Override
	public TimeUnit getEnum(TimeUnit t) {
		return TimeUnit.values()[t.ordinal() + 1];
	}

	@Override
	public TimeUnit[] getEnumArray(TimeUnit[] b) {
		flip(b);
		return b;
	}

	@Override
	public List<TimeUnit> getEnumList(List<TimeUnit> b) {
		reverse(b);
		return b;
	}

	@Override
	public Set<TimeUnit> getEnumSet(Set<TimeUnit> b) {
		Set<TimeUnit> set = new HashSet<>();
		for (TimeUnit x : b) {
			set.add(getEnum(x));
		}
		return set;
	}

	@Override
	public DTOType getDTO(DTOType dto) {
		dto.value = dto.value.toUpperCase();
		return dto;
	}

	@Override
	public DTOType[] getDTOTypeArray(DTOType[] b) {
		flip(b);
		return b;
	}

	@Override
	public List<DTOType> getDTOTypeList(List<DTOType> b) {
		reverse(b);
		return b;
	}

	@Override
	public Set<DTOType> getDTOTypeSet(Set<DTOType> b) {
		for (DTOType x : b) {
			x.value = x.value.toUpperCase();
		}
		return b;
	}

	private Map<String,Integer> map;

	@Override
	public void populateMap(Map<String,Integer> map) {
		this.map = map;
	}

	@Override
	public Map<String,Integer> getMap() {
		Map<String,Integer> m = map;
		map = null;
		return m;
	}

}
