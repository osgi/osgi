/**
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
 */

package org.osgi.test.cases.cdi.beans;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.osgi.service.cdi.annotations.BeanPropertyType;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.service.cdi.annotations.SingleComponent;
import org.osgi.test.cases.cdi.interfaces.BeanService;
import org.osgi.test.cases.cdi.interfaces.Pojo;

@SingleComponent
@Service({ServiceWithProperties.class, BeanService.class})
@ServiceWithProperties.Props
@ServiceWithProperties.MoreProperties(glub_integer = 45, goo_string = "green")
public class ServiceWithProperties implements BeanService<Pojo> {

	@Retention(RUNTIME) @Target(TYPE)
    @BeanPropertyType
	public @interface Props {
		String test_key_b1() default "test.value.b1";
		String test_key_b2() default "test.value.b2";
		boolean p_Boolean() default true;
		boolean[] p_Boolean_array() default {true, false};
		byte p_Byte() default 2;
		byte[] p_Byte_array() default {2, 34};
		char p_Character() default 'C';
		char[] p_Character_array() default {'C', 'D'};
		double p_Double() default 2.5;
		double[] p_Double_array() default {2.5, 45.678};
		float p_Float() default 3.4f;
		float[] p_Float_array() default {3.4f, 78.9f};
		int p_Integer() default 5;
		int[] p_Integer_array() default {5, 34567};
		long p_Long() default 7l;
		long[] p_Long_array() default {7l, 7789654l};
		short p_Short() default 25;
		short[] p_Short_array() default {25, 196};
		String p_String() default "black";
		String[] p_String_array() default {"black", "green"};
	}

	@Retention(RUNTIME) @Target(TYPE )
    @BeanPropertyType
	public @interface MoreProperties {
		String goo_string();
		int glub_integer();
	}

	@Override
	public String doSomething() {
		return _pojo.foo("FIELD");
	}

	@Override
	public org.osgi.test.cases.cdi.interfaces.Pojo get() {
		return _pojo;
	}

	@Inject
	private PojoImpl _pojo;

	@PostConstruct
	private void postConstructed() {
		System.out.println("PostConstructed " + this);
	}

	@PreDestroy
	private void preDestroyed() {
		System.out.println("PreDestroyed " + this);
	}

}
