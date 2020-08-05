package org.osgi.test.cases.cm.bundleT2;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.test.cases.cm.shared.Synchronizer;
import org.osgi.test.cases.cm.shared.Util;

/**
 * Register ManagedService with pid1.
 * 
 * @author Ikuo YAMASAKI, NTT Corporation
 * 
 * 
 */
public class BundleT2Activator implements BundleActivator {

	private BundleContext context;
	private static final boolean DEBUG = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		log("going to start. BundleT2");
		this.context = context;
		final String pid1 = Util.createPid("pid1");
		final String fpid1 = Util.createPid("factoryPid1");

		String filter1 = "("
				+ org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID
				+ "=sync2-1)";
		registerService(context, pid1, ManagedService.class, filter1);

		String filterF1 = "("
				+ org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID
				+ "=syncF2-1)";
		registerService(context, fpid1, ManagedServiceFactory.class, filterF1);

	}

	private <S> ServiceRegistration<S> registerService(BundleContext context,
			final String pid, final Class<S> clazz, String filterUpdated)
			throws InvalidSyntaxException, Exception {
		return this.registerService(context, pid, clazz, filterUpdated, null);
	}

	private <S> ServiceRegistration<S> registerService(BundleContext context,
			final String pid, final Class<S> clazz, String filterUpdated,
			String filterDeleted) throws InvalidSyntaxException, Exception {
		Synchronizer syncUpdated = null;
		try {
			syncUpdated = Util.getService(context,
					Synchronizer.class, filterUpdated);
		} catch (IllegalStateException ise) {
			return null;
		}

		@SuppressWarnings("unused")
		Synchronizer syncDeleted = null;
		if (filterDeleted != null) {
			try {
				syncDeleted = Util.getService(context,
						Synchronizer.class, filterDeleted);

			} catch (IllegalStateException ise) {
				return null;
			}
		}
		final Object service;
		if (clazz == ManagedService.class) {
			service = new ManagedServiceImpl(syncUpdated);
		} else {
			service = new ManagedServiceFactoryImpl(syncUpdated);
		}
		Dictionary<String,Object> props = new Hashtable<>();

		log("Going to register " + clazz + ": pid=\n\t" + pid);

		props.put(org.osgi.framework.Constants.SERVICE_PID, pid);
		props.put(org.osgi.framework.Constants.SERVICE_RANKING, "1");

		try {
			@SuppressWarnings("unchecked")
			ServiceRegistration<S> sr = this.context.registerService(clazz,
					(S) service, props);
			log("Succeed in registering service:clazz=" + clazz + ":pid=" + pid);
			return sr;
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
		log("going to stop. BundleT2");

	}

	class ManagedServiceImpl implements ManagedService {
		// private Dictionary props = null;
		final private Synchronizer sync;

		public ManagedServiceImpl(Synchronizer sync) {
			this.sync = sync;
		}

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

	class ManagedServiceFactoryImpl implements ManagedServiceFactory {
		// private Dictionary props = null;
		final private Synchronizer sync;

		// final private Synchronizer syncDeleted;

		public ManagedServiceFactoryImpl(Synchronizer sync) {
			// public ManagedServiceFactoryImpl(Synchronizer syncUpdated,
			// Synchronizer syncDeleted) {
			this.sync = sync;
			// this.syncDeleted = syncDeleted;
		}

		public void updated(String pid, Dictionary<String, ? > props)
				throws ConfigurationException {
			// this.props = props;
			if (props != null) {
				String fpid = (String) props
						.get(ConfigurationAdmin.SERVICE_FACTORYPID);
				log("ManagedServiceFactory#updated() is called back. pid: "
						+ pid + ", fpid=" + fpid);
			} else {
				log("ManagedServiceFactory#updated() is called back. pid: "
						+ pid + ", props == null ");
			}
			if (sync != null)
				sync.signal(props);
			else
				log("sync == null.");

		}

		public void deleted(String pid) {
			log("ManagedServiceFactory#deleted() is called back. pid: " + pid);
			if (sync != null)
				sync.signalDeleted(pid);
			else
				log("sync == null.");

		}

		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	void log(String msg) {
		if (DEBUG)
			System.out.println("# Register Test> " + msg);
	}
}
