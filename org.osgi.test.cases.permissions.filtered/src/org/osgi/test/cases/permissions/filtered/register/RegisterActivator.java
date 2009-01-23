package org.osgi.test.cases.permissions.filtered.register;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.osgi.framework.*;
import org.osgi.test.cases.permissions.filtered.util.IServiceRegister;

public class RegisterActivator implements BundleActivator, IServiceRegister {

	private BundleContext context;
	private String clazz;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception,
			SecurityException {

		System.out.println("REGISTER BUNDLE is going to start.");
		try {
			this.context = context;
			context
					.registerService(
							"org.osgi.test.cases.permissions.filtered.util.IServiceRegister",
							this, null);
			System.out.println("REGISTER BUNDLE STARTED !!!");
		} catch (Exception e) {
			System.out.println("FAIL TO START REGISTER BUNDLE !!!\n" + e);
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.test.cases.permissions.filtered.util.IServiceRegister#registerBundle
	 * (java.lang.String)
	 */
	public void registerIService(final String testId) throws SecurityException {
		Hashtable props = new Hashtable();
		final Properties prop = new Properties();
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				try {
					InputStream is = new FileInputStream("bnd/properties/"
							+ testId + ".properties");
					try {
						prop.load(is);
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						if (is != null)
							try {
								is.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null; // nothing to return
			}
		});

		String scount = prop.getProperty("prop.count");
		int count = Integer.parseInt(scount);
		for (int i = 0; i < count; i++) {
			String key = prop.getProperty("key." + i);
			String value = prop.getProperty("value." + i);
			props.put(key, value);
		}
		clazz = prop.getProperty("object.class");

		try {
			context.registerService(clazz, new IServiceImpl(context), props);
			System.out.println("Succeed in registering service: " + clazz);

		} catch (SecurityException e) {
			System.out.println("Fail to register service: " + clazz);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
