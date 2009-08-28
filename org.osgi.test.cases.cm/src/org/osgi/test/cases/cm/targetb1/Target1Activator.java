package org.osgi.test.cases.cm.targetb1;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.test.cases.cm.shared.Constants;
import org.osgi.test.cases.cm.shared.Synchronizer;
import org.osgi.test.cases.cm.shared.Util;

/**
 * Register ManagedService with single PID or multiple PIDs by any of array,
 * list, or set.
 * 
 * @author Ikuo YAMASAKI, NTT Corporation
 * 
 * 
 */
public class Target1Activator implements BundleActivator {
	private static final boolean DEBUG = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		log("going to start.");

		final String clazz = ManagedService.class.getName();

		String filter = "("
				+ org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID
				+ "=sync1)";
		Synchronizer sync = (Synchronizer) Util.getService(context,
				Synchronizer.class.getName(), filter);

		Object service = new ManagedServiceImpl(sync);
		Dictionary props = new Hashtable();

		final String pid1 = Util.createPid("bundlePid1");
		final String pid2 = Util.createPid("bundlePid2");
		String mode = System.getProperty(Constants.SYSTEMPROP_KEY_MODE);
		Object value = null;
		String msg = null;

		if (mode == null
				|| mode
						.equals(org.osgi.test.cases.cm.shared.Constants.MODE_UNARY)) {
			value = pid1;
			msg = "unary";
			log("Going to register ManagedService by " + msg + ". pid:\n\t"
					+ pid1);
		} else {
			if (mode.equals(org.osgi.test.cases.cm.shared.Constants.MODE_ARRAY)) {
				value = new String[] { pid1, pid2 };
				msg = "array";
			} else if (mode.equals(Constants.MODE_LIST)) {
				List pids = new ArrayList(2);
				pids.add(pid1);
				pids.add(pid2);
				value = pids;
				msg = "list";
			} else if (mode.equals(Constants.MODE_SET)) {
				Set pids = new HashSet(2);
				pids.add(pid1);
				pids.add(pid2);
				value = pids;
				msg = "set";
			} else {
				throw new IllegalStateException("System Prop of "
						+ Constants.SYSTEMPROP_KEY_MODE
						+ " is not set properly:it is set to " + mode);
			}
			log("Going to register ManagedService by " + msg + ". pid:\n\t"
					+ pid1 + "\n\t" + pid2);
		}
		props.put(org.osgi.framework.Constants.SERVICE_PID, value);

		try {
			context.registerService(clazz, service,
					props);
			log("Succeed in registering service: " + clazz);

		} catch (Exception e) {
			log("Fail to register service: " + clazz);
			// e.printStackTrace();
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
		log("going to stop.");
	}

	static class ManagedServiceImpl implements ManagedService {
		final private Synchronizer sync;

		public ManagedServiceImpl(Synchronizer sync) {
			this.sync = sync;
		}

		public void updated(Dictionary props) throws ConfigurationException {
			if (props != null) {
				String pid = (String) props
						.get(org.osgi.framework.Constants.SERVICE_PID);
				log("ManagedService#updated() is called back. pid: " + pid);
			} else {
				log("ManagedService#updated() is called back. props == null ");
			}
			if (sync != null)
				sync.signal(props);
			else
				log("sync == null.");
		}
	}

	static void log(String msg) {
		if (DEBUG)
			System.out.println("# Register Test> " + msg);
	}
}
