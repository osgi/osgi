package org.osgi.test.cases.cm.bundleT6;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.test.cases.cm.shared.Synchronizer;
import org.osgi.test.cases.cm.shared.Util;

/**
 */
public class BundleT6Activator implements BundleActivator {

	private BundleContext context;

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
		log("Starting bundle T6");
		this.context = context;

		final String filter = "("
				+ org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID
				+ "=syncT6-1)";
		final Synchronizer sync = Util.getService(context,
				Synchronizer.class, filter);

		registerService(context, Util.createPid("pid_targeted1"), sync);
	}

	private void registerService(final BundleContext context, 
			final String pid,
			final Synchronizer sync) throws Exception {

		final Object service = new ManagedServiceImpl(sync);
		final Dictionary<String,Object> props = new Hashtable<>();

		log("Going to register ManagedService. pid:\n\t"
				+pid);
		props.put(org.osgi.framework.Constants.SERVICE_PID, pid);

		try {
			this.context.registerService(ManagedService.class.getName(), service, props);
			log("Succeed in registering service:clazz=" + ManagedService.class.getName() + ":pid="
					+ pid);

		} catch (Exception e) {
			log("Fail to register service: " + ManagedService.class.getName() + ":pid="
					+ pid);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext context) throws Exception {
		log("Stopping bundle T6");

	}

	class ManagedServiceImpl implements ManagedService {

		final private Synchronizer sync;

		public ManagedServiceImpl(final Synchronizer sync) {
			this.sync = sync;
		}

		public void updated(final Dictionary<String, ? > props)
				throws ConfigurationException {
			if (props != null) {
				String pid = (String) props
						.get(org.osgi.framework.Constants.SERVICE_PID);
				log("ManagedService#updated() is called back. pid: " + pid);
			} else {
				log("ManagedService#updated() is called back. props == null ");
			}
			sync.signal(props);
		}

	}

	void log(final String msg) {
		System.out.println("# T6> " + msg);
	}
}
