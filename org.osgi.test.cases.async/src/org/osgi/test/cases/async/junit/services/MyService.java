package org.osgi.test.cases.async.junit.services;

public interface MyService {

	void doSlowStuff() throws Exception ;
	
	int countSlowly(int times) throws Exception ;
	
}
