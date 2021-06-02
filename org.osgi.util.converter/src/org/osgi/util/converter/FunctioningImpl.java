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

package org.osgi.util.converter;

import java.lang.reflect.Type;

import org.osgi.util.function.Function;

/**
 * @author $Id$
 */
class FunctioningImpl extends AbstractSpecifying<Functioning>
		implements Functioning {
	InternalConverter initialConverter;

	FunctioningImpl(InternalConverter converter) {
		initialConverter = converter;
	}

	@Override
	public <T> Function<Object,T> to(Class<T> cls) {
		Type type = cls;
		return to(type);
	}

	@Override
	public <T> Function<Object,T> to(TypeReference<T> ref) {
		return to(ref.getType());
	}

	@Override
	public <T> Function<Object,T> to(final Type type) {
		return new Function<Object,T>() {
			@Override
			public T apply(Object t) {
				InternalConverting converter = initialConverter.convert(t);
				return applyModifiers(converter).to(type);
			}
		};
	}

	InternalConverting applyModifiers(InternalConverting converter) {
		if (hasDefault)
			converter.defaultValue(defaultValue);
		if (liveView)
			converter.view();
		if (keysIgnoreCase)
			converter.keysIgnoreCase();
		if (sourceAsClass != null)
			converter.sourceAs(sourceAsClass);
		if (sourceAsDTO)
			converter.sourceAsDTO();
		if (sourceAsJavaBean)
			converter.sourceAsBean();
		if (targetAsClass != null)
			converter.targetAs(targetAsClass);
		if (targetAsDTO)
			converter.targetAsBean();
		if (targetAsJavaBean)
			converter.targetAsBean();

		return converter;
	}
}
