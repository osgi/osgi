package org.osgi.test.cases.jaxrs.extensions;

import javax.ws.rs.ext.ContextResolver;

public class ExtensionConfigProvider
		implements ContextResolver<ExtensionConfig> {

	private final String	toReplace;

	private final String	replaceWith;

	public ExtensionConfigProvider(String toReplace, String replaceWith) {
		this.toReplace = toReplace;
		this.replaceWith = replaceWith;
	}

	@Override
	public ExtensionConfig getContext(Class< ? > arg0) {
		if (!ExtensionConfig.class.equals(arg0))
			return null;
		return new ExtensionConfig(toReplace, replaceWith);
	}

}
