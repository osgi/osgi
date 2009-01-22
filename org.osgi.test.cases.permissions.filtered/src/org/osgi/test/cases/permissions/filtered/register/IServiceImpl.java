package org.osgi.test.cases.permissions.filtered.register;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import org.osgi.test.cases.permissions.filtered.util.IService;

public class IServiceImpl implements IService {

	private final BundleContext bc;

	IServiceImpl(BundleContext bc) {
		this.bc = bc;
	}

	public String getRegisteringBundleSymbolicName() {
		return bc.getBundle().getSymbolicName() + ":"
				+ bc.getBundle().getHeaders().get(Constants.BUNDLE_VERSION);
	}

}
