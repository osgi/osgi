package org.osgi.test.cases.jaxrs.extensions;

public class StringReplacer extends AbstractStringReplacer {

	private final String	toReplace;

	private final String	replaceWith;

	public StringReplacer(String toReplace, String replaceWith) {
		this.toReplace = toReplace;
		this.replaceWith = replaceWith;
	}

	public String getToReplace() {
		return toReplace;
	}

	public String getReplaceWith() {
		return replaceWith;
	}
}
