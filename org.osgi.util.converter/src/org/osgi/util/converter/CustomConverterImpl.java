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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A custom converter wraps another converter by adding rules and/or error
 * handlers.
 *
 * @author $Id$
 */
class CustomConverterImpl implements InternalConverter {
	final InternalConverter					delegate;
	final Map<Type,List<ConverterFunction>>	typeRules;
	final List<ConverterFunction>			allRules;
	final List<ConverterFunction>			errorHandlers;

	CustomConverterImpl(InternalConverter converter,
			Map<Type,List<ConverterFunction>> rules,
			List<ConverterFunction> catchAllRules,
			List<ConverterFunction> errHandlers) {
		delegate = converter;
		typeRules = rules;
		allRules = catchAllRules;
		errorHandlers = errHandlers;
	}

	@Override
	public InternalConverting convert(Object obj) {
		InternalConverting converting = delegate.convert(obj);
		return new ConvertingWrapper(obj, converting, this);
	}

	@Override
	public Functioning function() {
		return new FunctioningImpl(this);
	}

	@Override
	public ConverterBuilder newConverterBuilder() {
		return new ConverterBuilderImpl(this);
	}

	private class ConvertingWrapper implements InternalConverting {
		private final InternalConverter		initialConverter;
		private final InternalConverting	del;
		private final Object				object;
		private volatile Object				defaultValue;
		private volatile boolean			hasDefault;

		ConvertingWrapper(Object obj, InternalConverting delegate,
				InternalConverter converter) {
			initialConverter = converter;
			object = obj;
			del = delegate;
		}

		@Override
		public Converting view() {
			del.view();
			return this;
		}

		@Override
		public Converting defaultValue(Object defVal) {
			del.defaultValue(defVal);
			defaultValue = defVal;
			hasDefault = true;
			return this;
		}

		@Override
		public Converting keysIgnoreCase() {
			del.keysIgnoreCase();
			return this;
		}

		@Override
		public Converting sourceAs(Class< ? > type) {
			del.sourceAs(type);
			return this;
		}

		@Override
		public Converting sourceAsBean() {
			del.sourceAsBean();
			return this;
		}

		@Override
		public Converting sourceAsDTO() {
			del.sourceAsDTO();
			return this;
		}

		@Override
		public Converting targetAs(Class< ? > cls) {
			del.targetAs(cls);
			return this;
		}

		@Override
		public Converting targetAsBean() {
			del.targetAsBean();
			return this;
		}

		@Override
		public Converting targetAsDTO() {
			del.targetAsDTO();
			return this;
		}

		@Override
		public <T> T to(Class<T> cls) {
			Type type = cls;
			return to(type);
		}

		@Override
		public <T> T to(TypeReference<T> ref) {
			return to(ref.getType());
		}

		@Override
		public <T> T to(Type type) {
			return to(type, initialConverter);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T to(Type type, InternalConverter converter) {
			List<ConverterFunction> tr = typeRules.get(Util.baseType(type));
			if (tr == null)
				tr = Collections.emptyList();
			List<ConverterFunction> converters = new ArrayList<>(
					tr.size() + allRules.size());
			converters.addAll(tr);
			converters.addAll(allRules);

			try {
				if (object != null) {
					for (ConverterFunction cf : converters) {
						try {
							Object res = cf.apply(object, type);
							if (res != ConverterFunction.CANNOT_HANDLE) {
								return (T) res;
							}
						} catch (Exception ex) {
							if (hasDefault)
								return (T) defaultValue;
							else
								throw new ConversionException("Cannot convert "
										+ object + " to " + type, ex);
						}
					}
				}

				Object result = del.to(type, converter);
				if (result != null && Proxy.isProxyClass(result.getClass())
						&& getErrorHandlers(converter).size() > 0) {
					return (T) wrapErrorHandling(result, converter);
				} else {
					return (T) result;
				}
			} catch (Exception ex) {
				for (ConverterFunction eh : getErrorHandlers(converter)) {
					try {
						Object handled = eh.apply(object, type);
						if (handled != ConverterFunction.CANNOT_HANDLE)
							return (T) handled;
					} catch (RuntimeException re) {
						throw re;
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}

				// No error handler, throw the original exception
				throw ex;
			}
		}

		List<ConverterFunction> getErrorHandlers(Converter converter) {
			List<ConverterFunction> handlers = new ArrayList<>();

			if (converter instanceof CustomConverterImpl) {
				CustomConverterImpl cconverter = (CustomConverterImpl) converter;
				handlers.addAll(cconverter.errorHandlers);

				Converter nextDel = cconverter.delegate;
				handlers.addAll(getErrorHandlers(nextDel));
			}

			handlers.addAll(errorHandlers);

			return handlers;
		}

		private Object wrapErrorHandling(final Object wrapped,
				final InternalConverter converter) {
			final Class< ? > cls = wrapped.getClass();
			return Proxy.newProxyInstance(cls.getClassLoader(),
					cls.getInterfaces(), new InvocationHandler() {
						@Override
						public Object invoke(Object proxy, Method method,
								Object[] args) throws Throwable {
							Class< ? > mdDecl = method.getDeclaringClass();
							if (mdDecl.equals(Object.class)) {
								switch (method.getName()) {
									case "equals" :
										return Boolean
												.valueOf(proxy == args[0]);
									case "hashCode" :
										return Integer.valueOf(
												System.identityHashCode(proxy));
									case "toString" :
										return "Proxy for " + cls;
									default :
										throw new UnsupportedOperationException(
												"Method " + method
														+ " not supported on proxy for "
														+ cls);
								}
							}

							try {
								return method.invoke(wrapped, args);
							} catch (Exception ex) {
								for (ConverterFunction eh : getErrorHandlers(
										converter)) {
									try {
										Object handled = eh.apply(wrapped,
												method.getGenericReturnType());
										if (handled != ConverterFunction.CANNOT_HANDLE)
											return handled;
									} catch (RuntimeException re) {
										throw re;
									} catch (Exception e) {
										throw new RuntimeException(e);
									}
								}
							}
							return null;
						}
					});
		}

		@Override
		public String toString() {
			return to(String.class);
		}
	}
}
