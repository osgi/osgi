package org.osgi.impl.service.converter;

import java.util.*;
import java.util.concurrent.atomic.*;

import org.osgi.framework.*;
import org.osgi.service.component.*;
import org.osgi.service.converter.*;
import org.osgi.service.log.*;

import aQute.bnd.annotation.component.*;

/**
 * An implementation of an aggregate converter, also providing a number of the
 * basic conversions.
 */
@Component(servicefactory = true)
public class AggregateConverterImpl implements AggregateConverter {
	final Set<ServiceReference>					bad			= new HashSet<ServiceReference>();
	final Map<String, List<ServiceReference>>	converters	= new HashMap<String, List<ServiceReference>>();
	final AtomicReference<LogService>			log			= new AtomicReference<LogService>();
	final BasicConverter						basic		= new BasicConverter() {
																/**
																 * @param <Tp>
																 * @param s
																 * @param argType
																 * @return The
																 *         converted
																 *         value
																 *         or
																 *         null
																 */
																protected <Tp> Tp converters(
																		Object s,
																		ReifiedType<Tp> argType) {
																	return bundleConverters(
																			s,
																			argType);
																}

																/**
																 * @param className
																 * @return A
																 *         newly
																 *         loaded
																 *         class
																 * @throws ClassNotFoundException
																 */
																protected Class< ? > loadClass(
																		String className)
																		throws ClassNotFoundException {
																	return bundle
																			.loadClass(className);
																}

															};

	Bundle										bundle;
	BundleContext								bundleContext;

	/**
	 * Convert a source object
	 */
	public <T> T convert(Object s, ReifiedType<T> T) {
		try {
			return basic.convert(s, T);
		}
		catch (Exception e) {
			e.printStackTrace();
			log("Failed to convert: " + s + " to " + T, e);
		}
		return null;
	}

	/**
	 * @param context
	 */
	@Activate
	protected void activate(ComponentContext context) {
		bundle = context.getUsingBundle();
		bundleContext = bundle.getBundleContext();
	}

	/**
	 * Provide a map to convert an interface type to a concrete class of that
	 * interface.
	 */

	private void log(String message, Exception e) {
		if (e != null)
			log.get().log(LogService.LOG_ERROR, message, e);
		else
			log.get().log(LogService.LOG_INFO, message);
	}

	/**
	 * @param reference
	 * @throws Exception
	 */
	@Reference(service = Converter.class, type = '*')
	protected void addConverter(ServiceReference reference) throws Exception {
		Object s = reference.getProperty(Converter.OSGI_CONVERTER_TYPE);
		if (s == null) {
			// Log?
			return;
		}

		
		Collection<String> classes = basic.toCollection(s, List.class,
				new ReifiedType<String>(String.class));

		for (String className : classes) {
			put(className, reference);
		}
	}

	/**
	 * @param ref
	 * @throws Exception
	 */
	protected void removeConverter(ServiceReference ref) throws Exception {
		Object s = ref.getProperty(Converter.OSGI_CONVERTER_TYPE);
		if (s == null)
			return;

		Collection<String> classes = basic.toCollection(s, List.class,
				new ReifiedType<String>(String.class));

		for (String className : classes) {
			remove(className, ref);
		}
	}

	private synchronized void put(String name, ServiceReference ref) {
		List<ServiceReference> refs = converters.get(name);
		if (refs == null) {
			refs = new ArrayList<ServiceReference>();
			converters.put(name, refs);
		}
		refs.add(ref);
	}

	private synchronized boolean remove(String name, ServiceReference ref) {
		bad.remove(ref);
		List<ServiceReference> refs = converters.get(name);
		if (refs == null)
			return false;

		boolean result = refs.remove(ref);
		if (refs.isEmpty())
			converters.remove(name);
		return result;
	}

	/**
	 * @param log
	 */
	@Reference
	protected void setLog(LogService log) {
		this.log.set(log);
	}

	/**
	 * @param <T>
	 * @param s
	 * @param type
	 * @return a new instance
	 */
	protected <T> T bundleConverters(Object s, ReifiedType<T> type) {
		Class<T> clazz = type.getRawClass();
		String className = clazz.getName();

		List<ServiceReference> refs;
		synchronized (this) {
			refs = converters.get(className);
			if (refs == null || refs.isEmpty())
				return null;

			refs = new ArrayList<ServiceReference>(refs);
			refs.removeAll(bad);

		}

		for (ServiceReference reference : refs) {
			if (reference.isAssignableTo(bundle, className)
					&& !bad.contains(reference)) {

				Converter converter = (Converter) bundleContext
						.getService(reference);

				try {
					T value = converter.convert(s, type);
					if (value != null)
						return value;
				}
				catch (Throwable e) {
					log.get().log(LogService.LOG_ERROR,
							"Converter throws error " + converter, e);
					bad.add(reference);
				}
				finally {
					bundleContext.ungetService(reference);
				}
			}
		}
		return null;
	}

}
