package org.osgi.service.impl.command;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;

import org.osgi.service.command.*;
import org.osgi.service.command.annotations.*;
import org.osgi.service.converter.*;

public class MetaScopeImpl<T> implements MetaScope {
	final List<MetaFunctionImpl>	functions	= new ArrayList<MetaFunctionImpl>();
	final T							target;
	final String					scope;
	final Class< ? >				type;
	final Description				description;
	final ResourceBundle			resources;

	public MetaScopeImpl(String scope, String[] functions, T object) {
		this.target = object;
		this.scope = scope;
		this.type = object.getClass();
		this.description = type.getAnnotation(Description.class);
		ResourceBundle rb;
		try {
			rb = ResourceBundle.getBundle("/OSGI-INF/l10n/scope."
					+ type.getCanonicalName(), Locale.getDefault(), object
					.getClass().getClassLoader());
		}
		catch (MissingResourceException mre) {
			rb = null;
		}

		this.resources = rb;

		Set<String> set = new HashSet<String>();
		if (functions != null)
			set.addAll(Arrays.asList(functions));

		Method ms[] = type.getMethods();
		for (Method m : ms) {
			if (set.isEmpty() || set.contains(m.getName())) {
				MetaFunctionImpl mfi = new MetaFunctionImpl(this, m);
				this.functions.add(mfi);
			}
		}
	}

	public String getDescription() {
		if (description == null)
			return resource("description", "");
		else
			return resource(description.value(), description.value());
	}

	public Collection< ? extends MetaFunction> getMetaFunctions() {
		return Collections.unmodifiableCollection(functions);
	}

	public String getName() {
		return scope;
	}

	public String getSummary() {
		if (description == null)
			return resource("summary", "");
		else
			return resource(description.summary(), description.summary());
	}

	String resource(String value, String dflt) {
		String translation = resources.getString(value);
		if (translation != null)
			return translation;
		else
			return dflt;
	}

	String getResource(String id, String value) {
		if (resources == null)
			return value;

		if (value != null && value.startsWith("%"))
			id = value.substring(1);

		String translation = resources.getString(id);
		if (translation == null) {
			return value;
		}
		else
			return translation;
	}

	public Object execute(String name, CommandSession session,
			Converter converter, Object... arguments) throws Exception {

		// sort on cardinality
		SortedSet<MetaFunctionImpl> functions = new TreeSet<MetaFunctionImpl>(
				new Comparator<MetaFunctionImpl>() {
					public int compare(MetaFunctionImpl a, MetaFunctionImpl b) {
						if (a.cardinality > b.cardinality)
							return 1;
						if (a.cardinality < b.cardinality)
							return -1;

						if (a.hasParameterAnnotations != b.hasParameterAnnotations)
							if (a.hasParameterAnnotations)
								return -1;
							else
								return 1;

						if (a.method.isVarArgs() != b.method.isVarArgs())
							if (a.method.isVarArgs())
								return -1;
							else
								return 1;

					
						Class<?> at[] = a.method.getParameterTypes();
						Class<?> bt[] = b.method.getParameterTypes();
						for ( int i =0; i<a.cardinality; i++) {
							int n = at[i].getCanonicalName().compareTo(bt[i].getCanonicalName());
							if ( n != 0)
								return n;
						}
						// THIS IS IMPOSSIBLE ! There must be some difference
						// in the parameter types unless it is really the same
						// method.
						return 0;
					}
				});

		for (MetaFunctionImpl mpi : this.functions) {
			if (mpi.getName().equals(name))
				functions.add(mpi);
		}

		if (functions.isEmpty()) {
			Pattern pattern = Pattern.compile(name + "|" + beanName(name)
					+ "|_" + name);

			for (MetaFunctionImpl mpi : functions) {
				if (pattern.matcher(mpi.getName()).matches())
					functions.add(mpi);
			}
		}

		for (Iterator<MetaFunctionImpl> i = functions.iterator(); i.hasNext();) {
			boolean matched = false;
			try {
				MetaFunctionImpl mpi = i.next();
				Object[] parameters = mpi.processArgumentsIntoParameters(
						arguments, Arrays.asList(session));
				if (parameters != null) {

					mpi.convert(parameters, converter);

					matched = true;
					return mpi.method.invoke(target, parameters);
				}
			}
			catch (Exception e) {
				if (matched)
					throw e;
			}
		}

		throw new IllegalArgumentException("Cannot find a suitable method for "
				+ name + " " + Arrays.toString(arguments));
	}

	static Pattern	BEAN	= Pattern.compile(
									"(is|set|has|get|add|remove)(\\p{Lu}.+)",
									Pattern.UNICODE_CASE);

	String beanName(String name) {
		Matcher m = BEAN.matcher(name);
		if (m.matches())
			return m.group(2).toLowerCase();
		return name.toLowerCase();
	}

	public String toString() {
		Formatter f = new Formatter();
		toString(f);
		return f.out().toString();
	}

	public void toString(Formatter f) {
		f.format("SCOPE %s\n", scope);
		for (MetaFunctionImpl mpi : functions) {
			mpi.inlineFunction(f);
		}
	}
}
