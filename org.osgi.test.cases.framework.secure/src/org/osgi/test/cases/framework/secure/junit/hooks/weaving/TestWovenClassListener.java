package org.osgi.test.cases.framework.secure.junit.hooks.weaving;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.osgi.framework.PackagePermission;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.hooks.weaving.WovenClassListener;
import org.osgi.test.cases.framework.secure.junit.hooks.weaving.export.TestConstants;

public class TestWovenClassListener implements WovenClassListener {
	private static final PackagePermission permission = new PackagePermission(TestConstants.DYNAMIC_IMPORT_PACKAGE, PackagePermission.IMPORT);
	
	private volatile boolean called;
	private volatile AssertionFailedError error;
	
	public void modified(WovenClass wovenClass) {
		if (!wovenClass.getClassName().equals(TestConstants.WOVEN_CLASS))
			return;
		called = true;
		try {
			switch (wovenClass.getState()) {
				case WovenClass.TRANSFORMED:
				case WovenClass.TRANSFORMING_FAILED:
				case WovenClass.DEFINED:
				case WovenClass.DEFINE_FAILED:
					assertDynamicImport(wovenClass);
					assertHasPermission(wovenClass);
					assertClassLoad(wovenClass);
			}
		} catch (AssertionFailedError failed) {
			error = failed;
		} catch (Throwable t) {
			error = new AssertionFailedError("Unexpected error.");
			error.initCause(t);
		}
	}
	
	AssertionFailedError getError() {
		return error;
	}
	
	boolean isCalled() {
		return called;
	}
	
	private void assertClassLoad(WovenClass wovenClass) {
		if (TestConstants.isExpectSecurityExceptionAll() || 
				TestConstants.isExpectSecurityExceptionAddDynamicImport() ||
				wovenClass.getState() == WovenClass.TRANSFORMED) {
			try {
				wovenClass.getBundleWiring().getBundle().loadClass(TestConstants.DYNAMIC_IMPORT_CLASS);
				TestCase.fail("The class should not have loaded");
			}
			catch (ClassNotFoundException e) {
				// Okay.
			}
		}
		else {
			try {
				wovenClass.getBundleWiring().getBundle().loadClass(TestConstants.DYNAMIC_IMPORT_CLASS);
			}
			catch (ClassNotFoundException e) {
				TestCase.fail("The class should have loaded");
			}
		}
	}
	
	private void assertDynamicImport(WovenClass wovenClass) {
		if (TestConstants.isExpectSecurityExceptionAll() || TestConstants.isExpectSecurityExceptionAddDynamicImport()) {
			if (wovenClass.getDynamicImports().contains(TestConstants.DYNAMIC_IMPORT_PACKAGE))
				TestCase.fail("The dynamic import was added");
		}
		else {
			if (!wovenClass.getDynamicImports().contains(TestConstants.DYNAMIC_IMPORT_PACKAGE))
				TestCase.fail("The dynamic import was not added");
		}
	}
	
	private void assertHasPermission(WovenClass wovenClass) {
		if (TestConstants.isExpectSecurityExceptionAll() || 
				TestConstants.isExpectSecurityExceptionAddDynamicImport() ||
				wovenClass.getState() == WovenClass.TRANSFORMED) {
			if (wovenClass.getBundleWiring().getBundle().hasPermission(permission))
				TestCase.fail("The woven bundle should not have package permission");
		}
		else {
			if (!wovenClass.getBundleWiring().getBundle().hasPermission(permission))
				TestCase.fail("The woven bundle should have package permission");
		}
	}
}
