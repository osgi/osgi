package org.osgi.test.cases.jaxrs.extensions;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Providers;

public class ConfigurableStringReplacer extends AbstractStringReplacer {

	@Context
	Providers	providers;

	@Context
	HttpHeaders	headers;

	public String getToReplace() {
		return getConfig().getToReplace();
	}

	private ExtensionConfig getConfig() {
		ExtensionConfig config = headers.getAcceptableMediaTypes()
				.stream()
				.map(m -> providers.getContextResolver(ExtensionConfig.class,
						m))
				.filter(cr -> cr != null)
				.map(cr -> cr.getContext(ExtensionConfig.class))
				.findFirst()
				.get();
		return config;
	}

	public String getReplaceWith() {
		return getConfig().getReplaceWith();
	}
}
