package org.osgi.test.cases.cm.targetb2;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.test.cases.cm.shared.Constants;
import org.osgi.test.cases.cm.shared.Synchronizer;
import org.osgi.test.cases.cm.shared.Util;

/**
 * This bundle can register duplicated ManagedService with the same PID.
 * 
 * @author Ikuo YAMASAKI, NTT Corporation
 * 
 */
public class Target2Activator implements BundleActivator {

	private BundleContext context;
	private ServiceRegistration registration;
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
		this.context = context;

		final String clazz = ManagedService.class.getName();

		String filter = "(" + Constants.SERVICEPROP_KEY_SYNCID + "=sync2)";
		Synchronizer sync = (Synchronizer) Util.getService(context,
				Synchronizer.class.getName(), filter);
		Dictionary props = new Hashtable();

		final String pid1 = Util.createPid("bundlePid1");
		// final String pid2 = Util.createPid("targetB2Pid");
		int count = Integer.parseInt(System.getProperty(
				Constants.SYSTEMPROP_KEY_DUPCOUNT, "1"));
		for (int i = 0; i < count; i++) {
			Object service = new ManagedServiceImpl(sync, i);
			log("Going to register ManagedService " + i + " . pid:\n\t" + pid1);
			props.put(org.osgi.framework.Constants.SERVICE_PID, pid1);
			if (count > 1)
				props.put("DuplicatedID", new Integer(i));
			try {
				this.registration = this.context.registerService(clazz,
						service, props);
				log("Succeed in registering service " + i + ": " + clazz);
			} catch (Exception e) {
				log("Fail to register service " + i + ": " + clazz);
				// e.printStackTrace();
				throw e;
			}
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
		if (this.registration != null)
			this.registration.unregister();

	}

	class ManagedServiceImpl implements ManagedService {
//		private Dictionary props = null;
		final private Synchronizer sync;
		final private int id;

		public ManagedServiceImpl(Synchronizer sync, int id) {
			this.sync = sync;
			this.id = id;
		}

		public void updated(Dictionary props) throws ConfigurationException {
//			this.props = props;
			if (props != null) {
				String pid = (String) props
						.get(org.osgi.framework.Constants.SERVICE_PID);
				log("ManagedService[" + id
						+ "]#updated() is called back. pid: " + pid);
			} else {
				log("ManagedService[" + id
						+ "]#updated() is called back. props == null ");
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
