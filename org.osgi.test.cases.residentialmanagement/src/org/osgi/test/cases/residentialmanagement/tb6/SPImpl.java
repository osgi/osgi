package org.osgi.test.cases.residentialmanagement.tb6;

import org.osgi.test.cases.residentialmanagement.sharedpackage.SharedPackage;

public class SPImpl implements SharedPackage{

	public void printVersion() {
		System.out.println("###VERSION:    version:  1.1.0");
	}
}
