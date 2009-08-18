package org.osgi.test.cases.distribution.internal;

public class DistributedServiceImpl implements DistributedService, DoNotPublishInterface {

	public String reverse(String string) {
		return new StringBuffer(string).reverse().toString();
	}

}
