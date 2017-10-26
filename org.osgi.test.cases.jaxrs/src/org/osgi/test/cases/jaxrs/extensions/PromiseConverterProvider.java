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
