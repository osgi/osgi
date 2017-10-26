package org.osgi.test.cases.jaxrs.extensions;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.WriterInterceptor;

import org.osgi.test.cases.jaxrs.resources.WhiteboardResource;

public class StringReplacerDynamicFeature implements DynamicFeature {

	private final StringReplacer replacer;

	public StringReplacerDynamicFeature(String from, String to) {
		replacer = new StringReplacer(from, to);
	}

	@Override
	public void configure(ResourceInfo info, FeatureContext fc) {
		if (info.getResourceClass() == WhiteboardResource.class) {
			fc.register(replacer, WriterInterceptor.class);
		}
	}

}
