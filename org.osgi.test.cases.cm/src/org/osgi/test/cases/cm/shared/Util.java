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
package org.osgi.test.cases.cm.shared;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class Util {

	public static String createPid() {
		return createPid(null);
	}

	public static String createPid(String suffix) {
		String root = Util.class.getName();
		if (suffix == null) {
			return root;
		}
		return root + "." + suffix;
	}

	public static <S> S getService(BundleContext context, Class<S> clazz) {
		ServiceReference<S> reference = context.getServiceReference(clazz);
		if (reference == null)
			throw new IllegalStateException("Fail to get ServiceReference of "
					+ clazz);
		S service = context.getService(reference);
		if (service == null)
			throw new IllegalStateException("Fail to get Service of " + clazz);
		return service;
	}

	public static <S> S getService(BundleContext context, Class<S> clazz,
			String filter) throws InvalidSyntaxException {
		Collection<ServiceReference<S>> references = context
				.getServiceReferences(clazz,
				filter);
		if (references.isEmpty())
			throw new IllegalStateException("Fail to get ServiceReference of "
					+ clazz);
		S service = context.getService(references.iterator().next());
		if (service == null)
			throw new IllegalStateException("Fail to get Service of " + clazz);
		return service;
	}

	public static Dictionary<String,Object> singletonDictionary(String key,
			Object val) {
		Dictionary<String,Object> dict = new Hashtable<>();
		dict.put(key, val);
		return dict;
	}

	public static final class DictionaryBuilder {
		private final Dictionary<String,Object> dict = new Hashtable<>();

		public Dictionary<String,Object> build() {
			return dict;
		}

		public DictionaryBuilder entry(String key, Object val) {
			dict.put(key, val);
			return this;
		}
	}

	public static DictionaryBuilder dictionary() {
		return new DictionaryBuilder();
	}
}
