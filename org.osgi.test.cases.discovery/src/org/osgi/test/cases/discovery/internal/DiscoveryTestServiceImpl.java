package org.osgi.test.cases.discovery.internal;

public class DiscoveryTestServiceImpl implements DiscoveryTestServiceInterface {

	public String hello(String hello) {
		return "hello " + hello;
	}

}
