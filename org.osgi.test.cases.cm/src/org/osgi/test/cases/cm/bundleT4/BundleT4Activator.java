package org.osgi.test.cases.cm.bundleT4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.test.cases.cm.shared.Synchronizer;
import org.osgi.test.cases.cm.shared.Util;

/**
 * Register two ManagedServices with same PID.
 *
 * @author Ikuo YAMASAKI, NTT Corporation
 *
 *
 */
public class BundleT4Activator implements BundleActivator {

	private BundleContext context;
	private static final boolean DEBUG = true;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		log("going to start. BundleT4");
		this.context = context;
		final String pid1 = Util.createPid("pid1");
		final String pid2 = Util.createPid("pid2");
		final String pid3 = Util.createPid("pid3");

		String filter1 = "("
				+ org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID
				+ "=sync4-1)";
		registerService(context, new String[] {
				pid2, pid1
		}, ManagedService.class, filter1);
		String filter2 = "("
				+ org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID
				+ "=sync4-2)";
		registerService(context, new String[] {
				pid2, pid3
		}, ManagedService.class, filter2);
	}

	@SuppressWarnings("unchecked")
	private <S> void registerService(BundleContext context, final String[] pids,
			final Class<S> clazz, String filter) throws InvalidSyntaxException,
			Exception {
		Synchronizer sync1 = Util.getService(context,
				Synchronizer.class, filter);

		Object service = new ManagedServiceImpl(sync1);
		Dictionary<String,Object> props = new Hashtable<>();

		log("Going to register ManagedService. pid:\n\t"
				+ getStringOfArray(pids));

		String mode = System
				.getProperty("org.osgi.test.cases.cm.bundleT4.mode");
		Object value = pids;
		if (mode != null) {
			Collection<String> tmp = null;
			if (mode.equals("Vector")) {
				tmp = new Vector<>();
			} else if (mode.equals("List")) {
				tmp = new ArrayList<>(pids.length);
			} else if (!mode.equals("Array")) {
				String errmsg = "Fail to register service: " + clazz + ":pid="
						+ getStringOfArray(pids)
						+ ", because of inproper system property.";
				log(errmsg);
				// e.printStackTrace();
				throw new RuntimeException(errmsg);
			}

            if (tmp != null) {
                for (int i = 0; i < pids.length; i++) {
                    tmp.add(pids[i]);
                }
                value = tmp;
            }
		}

		props.put(org.osgi.framework.Constants.SERVICE_PID, value);

		try {
			this.context.registerService(clazz, (S) service, props);
			log("Succeed in registering service:clazz=" + clazz + ":pid="
					+ getStringOfArray(pids));

		} catch (Exception e) {
			log("Fail to register service: " + clazz + ":pid="
					+ getStringOfArray(pids));
			// e.printStackTrace();
			throw e;
		}
	}

	private String getStringOfArray(String[] array) {
		String msg = "[";
		for (int i = 0; i < array.length; i++) {
			if (i != 0)
				msg += ",";
			msg += array[i];
		}
		msg += "]";

		return msg;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		log("going to stop. BundleT4");

	}

	class ManagedServiceImpl implements ManagedService {
		// private Dictionary props = null;
		final private Synchronizer sync;

		public ManagedServiceImpl(Synchronizer sync) {
			this.sync = sync;
		}

		@Override
		public void updated(Dictionary<String, ? > props)
				throws ConfigurationException {
			// this.props = props;
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

	void log(String msg) {
		if (DEBUG)
			System.out.println("# Register Test> " + msg);
	}
}
