package org.osgi.impl.service.provisioning;

import java.io.*;
import java.util.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.provisioning.ProvisioningService;

/**
 * Reference implementation of the Dictionary that holds the Provisioning
 * Information. The save() method is used to make the hashtable persistent. It
 * is called by the thread that gets start in the Provisioning class.
 * <p>
 * Most of the methods just call the corresponding hashtable methods. The only
 * exception is the put method which checks to see if
 * ProvisioningService.PROVISIONING_REFERENCE is being set. If this is the case,
 * a RequestorThread is started to get the Provisioning file from the URL that
 * corresponds to the ProvisioningService.PROVISIONING_REFERENCE key.
 * <p>
 * Note that updates to the ProvisioningDictionary are clustered into sets of
 * updates using synchronization. The logic for this is in Provisioning, so see
 * that class for more details.
 * 
 * @author Benjamin Reed
 */
public class ProvisioningDictionary extends Dictionary {
	/** The in-core version of the Provisioning Information */
	Hashtable		info;
	/** The BundleContext of the provider of the ProvisioningService */
	BundleContext	bc;
	/** The event log. */
	ProvisioningLog	log;
	int				updateCount	= 0;

	/**
	 * Instantiates a dictionary. The Provisioning Information will be loaded
	 * from the private file area of the BundleContext.
	 * 
	 * @param bc The BundleContext of the Provisioning bundle.
	 * @param log The object to which events will be logged.
	 */
	ProvisioningDictionary(BundleContext bc, ProvisioningLog log) {
		this.bc = bc;
		this.log = log;
		load();
	}

	/**
	 * @see java.util.Dictionary#elements()
	 */
	public Enumeration elements() {
		return info.elements();
	}

	/**
	 * @see java.util.Dictionary#get(Object)
	 */
	public Object get(Object key) {
		Object o = info.get(key);
		if (o instanceof byte[]) {
			byte ba[] = (byte[]) o;
			byte cba[] = new byte[ba.length];
			System.arraycopy(ba, 0, cba, 0, ba.length);
			return cba;
		}
		return o;
	}

	/**
	 * @see java.util.Dictionary#isEmpty()
	 */
	public boolean isEmpty() {
		return info.isEmpty();
	}

	/**
	 * @see java.util.Dictionary#keys()
	 */
	public Enumeration keys() {
		return info.keys();
	}

	/** Unsupported operation */
	public Object put(Object key, Object value)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException("put not supported");
	}

	/**
	 * Unsupported operation
	 */
	public Object remove(Object key) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("remove not supported");
	}

	/**
	 * Backdoor to put information
	 */
	Object internalPut(Object key, Object value) {
		return info.put(key, value);
	}

	/**
	 * Backdoor to remove information
	 */
	Object internalRemove(Object key) {
		return info.remove(key);
	}

	/**
	 * @see java.util.Dictionary#size()
	 */
	public int size() {
		return info.size();
	}

	/**
	 * Persistently save the Provisioning Information
	 */
	void save() {
		try {
			File f = bc.getDataFile("info.dat");
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			info.put(ProvisioningService.PROVISIONING_UPDATE_COUNT,
					new Integer(++updateCount));
			oos.writeObject(info);
			oos.close();
		}
		catch (IOException e) {
			log.log("Couldn't save provisioning information");
		}
	}

	/**
	 * Load the Provisioning information from the persistent store
	 */
	void load() {
		try {
			File f = bc.getDataFile("info.dat");
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			info = (Hashtable) ois.readObject();
			updateCount = ((Integer) info
					.get(ProvisioningService.PROVISIONING_UPDATE_COUNT))
					.intValue();
			ois.close();
		}
		catch (Exception e) {
			log.log("Couldn't load provisioning information");
			info = new Hashtable();
			info.put(ProvisioningService.PROVISIONING_UPDATE_COUNT,
					new Integer(0));
		}
	}
}
