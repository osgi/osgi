package org.osgi.test.cases.jaxrs.extensions;

public class ExtensionConfig {

	private final String	toReplace;

	private final String	replaceWith;

	public ExtensionConfig(String toReplace, String replaceWith) {
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
