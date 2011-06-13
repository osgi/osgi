package org.osgi.service.impl.command;

import java.util.*;
import java.util.regex.*;

import org.osgi.service.command.*;
import org.osgi.service.command.MetaScope.MetaFunction.*;
import org.osgi.service.command.annotations.*;
import org.osgi.service.converter.*;
import org.osgi.service.converter.generic.*;

public class MetaParameterImpl implements MetaParameter {
	final static String		EMPTY[]	= new String[0];
	final Parameter			parameter;
	final Description		description;
	final ReifiedType< ? >	type;
	final int				n;
	final boolean			varargs;
	final MetaFunctionImpl	function;

	public MetaParameterImpl(MetaFunctionImpl function, int n,
			GenericType< ? > type, Description description,
			Parameter parameter, boolean varargs) {
		this.n = n;
		this.type = type;
		this.description = description;
		this.parameter = parameter;
		this.varargs = varargs;
		this.function = function;
	}

	public String[] getAliases() {
		return parameter == null ? EMPTY : parameter.alias();
	}

	public String flag() {
		return parameter == null ? Parameter.NOT_SET : parameter.flag();
	}

	public String absent() {
		return parameter == null ? Parameter.NOT_SET : parameter.absent();
	}

	public ReifiedType< ? > getType() {
		return type;
	}

	public int getIndex() {
		return n;
	}

	public String toString() {
		Formatter formatter = new Formatter();
		toString(formatter);
		return formatter.out().toString();
	}

	public void toString(Formatter formatter) {

	}

	public String getDescription() {
		return function.getResource(""+n+".description", description == null ? null : description.value());
	}

	public String getSummary() {
		return function.getResource(""+n+".summary", description == null ? null : description.summary());
	}

	public boolean isFlag() {
		return parameter != null && !parameter.flag().equals(Parameter.NOT_SET);
	}

	public boolean isOption() {
		return parameter != null && parameter.flag().equals(Parameter.NOT_SET);
	}

	public boolean isMandatory() {
		return parameter == null
				|| parameter.absent().equals(Parameter.NOT_SET);
	}

	public boolean isRepeat() {
		return varargs || (parameter != null && parameter.repeat());
	}

	public boolean isVarargs() {
		return varargs;
	}

	public boolean isSession() {
		return type.getRawClass().isAssignableFrom(CommandSession.class);
	}

	public boolean isNamed() {
		return parameter != null && parameter.alias().length != 0;
	}

	void inline(Formatter formatter) {
		if (!isMandatory())
			formatter.format("[");
		else
			formatter.format("(");

		if (getAliases().length != 0)
			formatter.format("%s", getAliases()[0]);

		if (!isFlag()) {
			String type = getType().toString();
			type = shorten(type).toLowerCase();
			formatter.format(" <%s>", type);
		}
		if (!isMandatory())
			formatter.format("]");
		else
			formatter.format(")");
		if (isRepeat() || isVarargs())
			formatter.format("…");
	}

	static Pattern	FQN	= Pattern
								.compile("([a-z]+(\\.[a-zA-Z][a-zA-Z_$0-9]*)*)\\.([A-Z][a-zA-Z_$0-9]*)");

	private String shorten(String type) {
		Matcher m = FQN.matcher(type);
		return m.replaceAll("$3");
	}

	void detailed(Formatter formatter) {
		if (!isMandatory())
			formatter.format("[");

		String del = "        ";
		for (String alias : getAliases()) {
			formatter.format("%s%s", del, alias);
			del = ", ";
		}

		if (!isFlag()) {
			String type = getType().toString();
			type = shorten(type).toLowerCase();
			formatter.format(" <%s>", type);
		}

		if (!isMandatory())
			formatter.format("]");
		if (isRepeat() || isVarargs())
			formatter.format("…");

		formatter.format(" - %s\n%s\n", getSummary(), getDescription());
	}
}
