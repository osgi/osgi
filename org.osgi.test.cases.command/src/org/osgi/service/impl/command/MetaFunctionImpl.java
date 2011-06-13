package org.osgi.service.impl.command;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.osgi.service.command.MetaScope.*;
import org.osgi.service.command.annotations.*;
import org.osgi.service.converter.*;
import org.osgi.service.converter.generic.*;

public class MetaFunctionImpl implements MetaFunction {
	final MetaScopeImpl< ? >				scope;
	final Description						description;
	final Map<String, MetaParameterImpl>	index		= new HashMap<String, MetaParameterImpl>();
	final List<MetaParameterImpl>			parameters	= new ArrayList<MetaParameterImpl>();
	final String							name;
	final Method							method;
	final int								cardinality;
	protected boolean	hasParameterAnnotations;

	/**
	 * 
	 * @param scope
	 * @param m
	 */
	MetaFunctionImpl(MetaScopeImpl< ? > scope, Method m) {
		this.method = m;
		this.name = m.getName();
		this.description = m.getAnnotation(Description.class);
		this.scope = scope;

		Type[] parameterTypes = m.getGenericParameterTypes();
		this.cardinality = parameterTypes.length;

		Annotation[][] annss = m.getParameterAnnotations();
		for (int n = 0; n < annss.length; n++) {
			Description description = null;
			Parameter parameter = null;

			Annotation[] anns = annss[n];
			for (Annotation a : anns) {
				if (a instanceof Description)
					description = (Description) a;
				else
					if (a instanceof Parameter) {
						parameter = (Parameter) a;
						hasParameterAnnotations=true;
					}
			}
			MetaParameterImpl mpi = new MetaParameterImpl(this, n,
					new GenericType<Object>(parameterTypes[n]), description,
					parameter, isVarargs() && n == annss.length - 1);
			parameters.add(mpi);
			for (String alias : mpi.getAliases())
				index.put(alias, mpi);
		}
	}

	public String getDescription() {
		return getResource("description", description == null ? null
				: description.value());
	}

	public String getSummary() {
		return getResource("summary", description == null ? null : description
				.summary());
	}

	public Collection< ? extends MetaParameter> getMetaParameters() {
		return Collections.unmodifiableCollection(parameters);
	}

	public String getName() {
		return name;
	}

	public boolean isVarargs() {
		return method.isVarArgs();
	}

	Object[] processArgumentsIntoParameters(Object[] arguments,
			List< ? > sessionVariables) {
		Object[] invoke = new Object[method.getParameterTypes().length];

		List< ? > args = doNamedOptions(arguments, invoke);
		if (args == null)
			return null;

		doDefaultsAndRemaining(sessionVariables, invoke, args);

		if (!args.isEmpty())
			return null;

		return invoke;
	}

	private void doDefaultsAndRemaining(List< ? > sessionVariables,
			Object[] invoke, List< ? > args) {
		cleanup: for (MetaParameterImpl p : parameters) {

			if (p.isSession()) {
				for (Object sessionVariable : sessionVariables) {
					if (p.getType().getRawClass().isAssignableFrom(
							sessionVariable.getClass())) {
						invoke[p.getIndex()] = sessionVariable;
						continue cleanup;
					}
				}
				throw new IllegalArgumentException(
						"Could not find a session variable");
			}
			else
				if (p.isNamed()) {
					if (invoke[p.getIndex()] == null) {
						if (p.isMandatory())
							throw new IllegalArgumentException(
									"Mandatory parameter " + p.toString()
											+ " not set");

						invoke[p.getIndex()] = p.absent();
					}
				}
				else
					if (p.isVarargs()) {
						List<Object> list = new ArrayList<Object>();
						invoke[p.getIndex()] = list;
						list.addAll(args);
						args.clear();
					}
					else
						if (args.isEmpty()) {
							if (!p.isMandatory())
								invoke[p.getIndex()] = p.absent();
							else
								throw new IllegalArgumentException(
										"No value in command line and is mandatory");
						}
						else {
							invoke[p.getIndex()] = args.remove(0);
						}
		}
	}

	private List< ? > doNamedOptions(Object[] arguments, Object[] invoke) {
		List< ? > args = new ArrayList<Object>(Arrays.asList(arguments));

		int n = 0;
		reorder: while (args.size() > n) {
			Object arg = args.get(n);
			if (arg instanceof String) {
				String namedParameter = (String) arg;

				if (namedParameter.startsWith("-")) {

					//
					// Stop checking for named parameters?
					//
					if (namedParameter.equals("--")) {
						args.remove(n);
						break reorder;
					}

					//
					// Check if we can match the
					boolean matchedParameter = doParameter(namedParameter,
							args, n, invoke);

					if (!matchedParameter) {

						//
						// Try the sub option/flags. Suboptions/flags are single
						// character
						// parameters that are grouped in a single list.
						//

						args.remove(n);
						for (int i = 1; i < namedParameter.length(); i++) {
							String ss = "-" + namedParameter.charAt(i);
							MetaParameter pp = index.get(ss);
							if (pp == null)
								throw new IllegalArgumentException(
										"Not a known sub option/flag "
												+ namedParameter.charAt(i)
												+ " in " + namedParameter);

							if (pp.isOption()
									&& i != namedParameter.length() - 1)
								throw new IllegalArgumentException(
										"Sub option "
												+ namedParameter.charAt(i)
												+ " in " + namedParameter
												+ " is not the last letter");

							if (!doParameter(ss, args, n, invoke))
								throw new IllegalArgumentException(
										"Not a valid option: " + namedParameter);
						}
					}
				}
				else
					n++;
			}
		}
		return args;
	}

	@SuppressWarnings("unchecked")
	boolean doParameter(String s, List< ? > args, int n, Object[] modified) {
		Collection<Object> repeat = null;
		MetaParameter p = index.get(s);

		if (p == null)
			return false;

		assert p.isOption() || p.isFlag();

		if (p.isRepeat()) {
			if (modified[p.getIndex()] == null)
				modified[n] = new ArrayList<Object>();
			repeat = (Collection<Object>) modified[n];
		}
		else {
			if (modified[p.getIndex()] != null)
				throw new IllegalArgumentException("The flag/option: " + s
						+ " cannot be repeated");
		}

		Object value;

		if (p.isOption()) {
			if (n < args.size())
				throw new IllegalArgumentException("Option " + s
						+ " must be follwed by a value (" + p.getSummary()
						+ ")");

			value = args.remove(n);
		}
		else {
			value = p.flag();
		}

		if (repeat != null)
			repeat.add(value);
		else
			modified[p.getIndex()] = value;

		return true;
	}

	public String toString() {
		Formatter formatter = new Formatter();
		toString(formatter);
		return formatter.out().toString();
	}

	public void toString(Formatter formatter) {
		formatter.format("NAME\n");
		formatter.format("        %-12s - %s\n", name, getSummary());

		formatter.format("\n");
		formatter.format("SYNOPSIS\n");
		boolean hasNamed = inlineFunction(formatter);

		if (getDescription() != null && getDescription().length() != 0) {
			formatter.format("\n");
			formatter.format("DESCRIPTION\n");
			formatter.format(getDescription());
		}

		if (hasNamed) {
			formatter.format("\n");
			formatter.format("OPTIONS\n");
			for (MetaParameterImpl mp : parameters) {
				mp.detailed(formatter);
			}
		}
	}

	boolean inlineFunction(Formatter formatter) {
		boolean hasNamed = false;
		String bean = scope.beanName(name);
		formatter.format("  %-12s ", bean);
		for (MetaParameterImpl mp : parameters) {
			formatter.format(" ");
			mp.inline(formatter);
			hasNamed |= mp.isOption() || mp.isFlag();
		}
		String summary = getSummary();
		if (summary == null)
			formatter.format("\n");
		else
			if (!bean.equals(name))
				formatter.format(" - (%s) %s\n", name, getSummary());
			else
				formatter.format(" -  %s\n", getSummary());
		return hasNamed;
	}

	public String getResource(String id, String value) {
		return scope.getResource(getName() + "." + id, value);
	}

	public void convert(Object[] parameters, Converter c) {
		
	}

}
