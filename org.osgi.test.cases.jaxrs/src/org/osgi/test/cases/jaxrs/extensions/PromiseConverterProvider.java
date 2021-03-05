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
package org.osgi.test.cases.jaxrs.extensions;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

public class PromiseConverterProvider implements ParamConverterProvider {

	@SuppressWarnings("unchecked")
	@Override
	public <T> ParamConverter<T> getConverter(Class<T> cls, Type t,
			Annotation[] arg2) {

		if (cls != Promise.class)
			return null;

		if (t instanceof ParameterizedType && ((ParameterizedType) t)
				.getActualTypeArguments()[0] == String.class) {
			return (ParamConverter<T>) new ParamConverter<Promise<String>>() {

				@Override
				public Promise<String> fromString(String arg0) {
					return Promises.resolved(arg0).delay(2000);
				}

				@Override
				public String toString(Promise<String> arg0) {
					try {
						return arg0.getValue();
					} catch (Exception e) {
						throw new WebApplicationException(e);
					}
				}

			};
		}

		return null;
	}

}
