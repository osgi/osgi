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

package org.osgi.util.converter.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify the key name for a type member.
 * <p>
 * When mapping a member name to a key name, the value of this annotation will
 * be used, if specified, instead of the member name to key name mapping rules
 * in the <i>Converter Specification</i>.
 * 
 * <pre>
 * public &#64;interface ServiceVendor {
 * 	&#64;KeyName("service.vendor")
 * 	String value();
 * }
 * </pre>
 * <p>
 * This annotation is retained at runtime. It is for use by tools to generate
 * the key name for a member and for the converter to map a member name to the
 * key name at runtime.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
		ElementType.FIELD, ElementType.METHOD
})
public @interface KeyName {
	/**
	 * The name of the key.
	 */
	String value();
}
