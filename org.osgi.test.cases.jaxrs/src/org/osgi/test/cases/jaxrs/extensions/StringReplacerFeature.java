package org.osgi.test.cases.jaxrs.extensions;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.WriterInterceptor;

public class StringReplacerFeature implements Feature {

	private final StringReplacer replacer;

	public StringReplacerFeature(String from, String to) {
		replacer = new StringReplacer(from, to);
	}

	@Override
	public boolean configure(FeatureContext fc) {
		fc.register(replacer, WriterInterceptor.class);
		return true;
	}

}
