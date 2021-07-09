package org.osgi.test.cases.typedevent.junit;

import org.osgi.dto.DTO;

public class TestEvent extends DTO {

	public TestEvent(String test, String a, String A) {
		this.test = test;
		this.a = a;
		this.A = A;
	}

	public String	a;
	public String	A;
	public String	test;
}
