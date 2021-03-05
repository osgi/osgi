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

package org.osgi.test.cases.framework.junit.dto;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.osgi.dto.DTO;
import org.osgi.framework.Version;

import junit.framework.TestCase;

public class DTOTestCase extends TestCase {
	private TestDTO dto;

	@Override
	protected void setUp() {
		dto = new TestDTO();
		dto.stringField = "stringValue";
		dto.versionField = Version.valueOf("1.2.3.qual");
		dto.enumField = TestEnum.TWO;
	}

	public void testDTOtoString() throws Exception {
        String result = dto.toString();
		System.out.println(result);
		assertTrue("toString does not include stringField name",
				result.indexOf("stringField") >= 0);
		assertTrue("toString does not include stringField value",
				result.indexOf("\"stringValue\"") >= 0);
		assertTrue("toString does not include versionField name",
				result.indexOf("versionField") >= 0);
		assertTrue("toString does not include versionField value",
				result.indexOf("\"1.2.3.qual\"") >= 0);
		assertTrue("toString does not include enumField name",
				result.indexOf("enumField") >= 0);
		assertTrue("toString does not include enumField value",
				result.indexOf("\"TWO\"") >= 0);
	}

	public void testDTOEnum() throws Exception {
		for (Field field : dto.getClass().getFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			Class< ? > c = field.getType();
			if (!Enum.class.isAssignableFrom(c)) {
				continue;
			}
			System.out.printf("%s is an enum of type %s\n", field.getName(), c);
			Object o = field.get(dto);
			Method m = c.getMethod("valueOf", String.class);
			assertSame(o, m.invoke(null, ((Enum< ? >) o).name()));
		}
    }

	public void testDTOVersion() throws Exception {
		for (Field field : dto.getClass().getFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			Class< ? > c = field.getType();
			if (!"org.osgi.framework.Version".equals(c.getName())) {
				continue;
			}
			System.out.printf("%s is a version of type %s\n", field.getName(),
					c);
			Object o = field.get(dto);
			Method m = c.getMethod("valueOf", String.class);
			assertEquals(o, m.invoke(null, o.toString()));
		}
	}

    public enum TestEnum {
		ONE, TWO, THREE
	}
    public static class TestDTO extends DTO {
		public String	stringField;
		public Version	versionField;
		public TestEnum	enumField;
    }
}
