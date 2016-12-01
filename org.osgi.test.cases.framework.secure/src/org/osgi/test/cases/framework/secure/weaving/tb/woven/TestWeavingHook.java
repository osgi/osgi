package org.osgi.test.cases.framework.secure.weaving.tb.woven;

import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.test.cases.framework.secure.junit.hooks.weaving.export.TestConstants;

public class TestWeavingHook implements WeavingHook {
	public void weave(WovenClass wovenClass) {
		if (!wovenClass.getClassName().equals(TestConstants.WOVEN_CLASS))
			return;
		// Add a dynamic import before the other weaving hook gets called so
		// that List.set can be tested.
		wovenClass.getDynamicImports().add("org.osgi.resolver");
	}
}
