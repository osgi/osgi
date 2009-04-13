package org.osgi.impl.service.provisioning;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.service.provisioning.ProvisioningService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The ProvisioningService to register in the service registry. Not much
 * happening here, all of the work is done in ProvisioningDictionary.
 * 
 * @author breed
 */
public class ProvisioningServiceImpl implements ProvisioningService {
	ProvisioningDictionary info;
	ServiceRegistration reg;
	ProvisioningLog log;
	PermissionAdmin permadm;
	ServiceTracker tracker;
	BundleContext bc;
	Dictionary regDict;

	/**
	 * Constructs a ProvisioningService to be registered in the service
	 * registry.
	 * 
	 * @param bc -
	 *            The BundleContext from the Provisioning activator.
	 * @param log -
	 *            The event logger.
	 */
	public ProvisioningServiceImpl(BundleContext bc, ProvisioningLog log,
			Dictionary regDict) {
		this.log = log;
		this.bc = bc;
		this.regDict = regDict;
		info = new ProvisioningDictionary(bc, log);
		tracker = new ServiceTracker(bc, PermissionAdmin.class.getName(), null);
		tracker.open();
		permadm = (PermissionAdmin) tracker.getService();
		if (permadm == null) {
			log.log("Couldn't get PermissionAdmin.  Continuing.");
		}
	}

	/**
	 * Sets the ServiceRegistration for callbacks to update the Service
	 * dictionary
	 */
	public void setServiceRegistration(ServiceRegistration reg) {
		this.reg = reg;
	}

	/**
	 * @see org.osgi.service.provisioning.ProvisioningService#getInformation()
	 */
	public Dictionary getInformation() {
		return info;
	}

	/**
	 * @see org.osgi.service.provisioning.ProvisioningService#setInformation()
	 */
	public void setInformation(Dictionary dict) {
		synchronized (info) {
			Enumeration en = info.keys();
			while (en.hasMoreElements()) {
				Object key = en.nextElement();
				info.internalRemove(key);
			}
			addInformation(dict);
		}
	}

	private static int readData(InputStream is, ByteArrayOutputStream os)
			throws IOException {
		byte buf[] = new byte[4096];
		int count = 0;
		int rc;
		while ((rc = is.read(buf, 0, buf.length)) > 0) {
			os.write(buf, 0, rc);
			count += rc;
		}
		return count;
	}

	/**
	 * @see org.osgi.service.provisioning.ProvisioningService#addInformation()
	 */
	public void addInformation(Dictionary dict) {
		addInformation(dict, 0);
	}

	/**
	 * The maximum number of provisioning iterations before stopping. This is
	 * needed to avoid infinite provisioning loops.
	 */
	final private int MAX_ITERATION = 6;

	/**
	 * @param iteration
	 *            how many times this has been called using the nextref
	 *            mechanism. If it hits MAX_ITERATIONS, the nextref will be
	 *            ignored.
	 * 
	 * @see org.osgi.service.provisioning.ProvisioningService#addInformation()
	 */
	public void addInformation(Dictionary dict, int iteration) {
		Object value;
		synchronized (info) {
			Enumeration en = dict.keys();
			while (en.hasMoreElements()) {
				Object key = en.nextElement();
				if (!(key instanceof String))
					continue;
				String name = (String) key;
				/*
				 * property names cannot start with /, so strip off any leading /
				 */
				while (name.startsWith("/"))
					name = name.substring(1);
				value = dict.get(key);
				if (!(value instanceof String) && !(value instanceof byte[]))
					continue;
				info.internalPut(name, value);
			}
			info.save();
			regDict.put(ProvisioningService.PROVISIONING_UPDATE_COUNT, info
					.get(ProvisioningService.PROVISIONING_UPDATE_COUNT));
			reg.setProperties(regDict);
		}
		if ((value = dict.get(ProvisioningService.PROVISIONING_REFERENCE)) != null
				&& value instanceof String && iteration < MAX_ITERATION) {
			Object spid;
			spid = dict.get(ProvisioningService.PROVISIONING_SPID);
			new RequestorThread(value.toString(), spid == null ? "" : spid
					.toString(), log, iteration).start();
		}
		if ((value = dict.get(ProvisioningService.PROVISIONING_START_BUNDLE)) != null
				&& value instanceof String) {
			String location = value.toString();
			Bundle bundle = getBundle(location);
			if (bundle == null) {
				log.log("Tried to start " + location
						+ " before it was installed.");
			} else {
				if (permadm != null)
					permadm.setPermissions(location, AllPermissionInfo);
				try {
					bundle.start();
					log.log(location + " started");
				} catch (BundleException e) {
					log.log(location + " couldn't be started");
				}
			}
		}
	}

	private Bundle getBundle(String location) {
		Bundle b[] = bc.getBundles();
		for (int i = 0; i < b.length; i++) {
			if (b[i].getLocation().equals(location))
				return b[i];
		}
		return null;
	}

	/**
	 * @see org.osgi.service.provisioning.ProvisioningService#addInformation()
	 */
	public void addInformation(ZipInputStream zis) throws IOException {
		addInformation(zis, 0);
	}

	/**
	 * @param iteration
	 *            how many times this has been called using the nextref
	 *            mechanism. If it hits MAX_ITERATIONS, the nextref will be
	 *            ignored.
	 * @see org.osgi.service.provisioning.ProvisioningService#addInformation()
	 */
	public void addInformation(ZipInputStream zis, int iteration)
			throws IOException {
		class BundleToInstall {
			String name;
			ByteArrayOutputStream bundle = null;
		}
		;
		Vector bundlesToInstall = new Vector();
		int count;
		Hashtable newinfo = new Hashtable();
		for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis
				.getNextEntry()) {
			byte e[] = ze.getExtra();
			if (e == null) {
				log.log("No extra information for " + ze.getName());
				continue;
			}
			String mime = new String(e);
			String key = ze.getName();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			count = readData(zis, os);
			if (mime.equalsIgnoreCase(ProvisioningService.MIME_STRING)) {
				newinfo.put(key, new String(os.toByteArray(), "UTF-8"));
			} else if (mime
					.equalsIgnoreCase(ProvisioningService.MIME_BYTE_ARRAY)) {
				newinfo.put(key, os.toByteArray());
			} else if (mime.equalsIgnoreCase(ProvisioningService.MIME_BUNDLE)
					|| mime
							.equalsIgnoreCase(ProvisioningService.MIME_BUNDLE_ALT)) {
				BundleToInstall b = new BundleToInstall();
				b.name = key;
				b.bundle = os;
				bundlesToInstall.add(b);
			} else if (mime
					.equalsIgnoreCase(ProvisioningService.MIME_BUNDLE_URL)) {
				BundleToInstall b = new BundleToInstall();
				b.name = key;
				String url = new String(os.toByteArray(), "UTF-8");
				InputStream is = new URL(url).openStream();
				b.bundle = new ByteArrayOutputStream();
				readData(is, b.bundle);
				bundlesToInstall.add(b);
			} else {
				log.log("Invalid MIME type " + mime + " for " + key);
			}
		}
		Enumeration en = bundlesToInstall.elements();
		while (en.hasMoreElements()) {
			BundleToInstall bti = (BundleToInstall) en.nextElement();
			Bundle b = getBundle(bti.name);
			try {
				if (b == null) {
					bc.installBundle(bti.name, new ByteArrayInputStream(
							bti.bundle.toByteArray()));
				} else {
					b
							.update(new ByteArrayInputStream(bti.bundle
									.toByteArray()));
				}
			} catch (Exception e) {
				log.log(e.toString() + ": when installing " + bti.name);
			}
		}
		addInformation(newinfo, iteration);
	}

	PermissionInfo AllPermissionInfo[] = { new PermissionInfo(
			"java.security.AllPermission", null, null) };

	/**
	 * This inner class is used to request a zip file containing provisioning
	 * information from a URL and add it to the dictionary.
	 */
	class RequestorThread extends Thread {
		String url;
		ProvisioningLog log;
		/* Iteration is used to prevent an infinite loop. */
		int iteration;

		RequestorThread(String url, String spid, ProvisioningLog log,
				int iteration) {
			if (!url.startsWith("rsh:") && !url.startsWith("file:")) {
				/* For everything but file: & rsh: need to tack on the spid */
				StringBuffer sb = new StringBuffer();
				sb.append(url);
				int q = url.lastIndexOf('?');
				if (q == -1) {
					sb.append('?');
				} else {
					sb.append('&');
				}
				sb.append("service_platform_id=");
				try {
					sb.append(encode(spid));
					// sb.append(URLEncoder.encode(spid, ));
				} catch (UnsupportedEncodingException e) {
					/* This really can't happen since Java requires UTF-8! */
					throw new RuntimeException("UTF-8 encoding not present");
				}
				this.url = sb.toString();
			} else {
				this.url = url;
			}
			this.log = log;
			this.iteration = iteration++;
		}

		public void run() {
			log.log("Processing:  " + url);
			try {
				URL u = new URL(url);
				// changed this from getContent because I do not
				// think that is portable - pkr
				InputStream is = u.openStream();
				if (is == null)
					throw new IOException("Couldn't get content");
				ZipInputStream zis;
				if (is instanceof ZipInputStream) {
					// Hey, we might get lucky...
					zis = (ZipInputStream) is;
				} else {
					zis = new ZipInputStream(is);
				}
				addInformation(zis, iteration);
			} catch (IOException e) {
				e.printStackTrace();
				log.log("Error retrieving information from " + url + ": " + e);
			}
			log.log("Finished processing:  " + url);
		}
	}

	String encode(String spid) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		byte[] buffer = spid.getBytes("UTF-8");
		for (int i = 0; i < buffer.length; i++) {
			int c = buffer[i] & 0xFF;
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9') || c == '.' || c == '-'
					|| c == '*' || c == '_')
				sb.append((char) c);
			else if (c == ' ')
				sb.append('+');
			else {
				sb.append('%');
				int l = (0xF0 & c) / 16;
				sb.append((char) (l >= 10 ? 'A' + (l - 10) : '0' + l));
				l = c & 0xF;
				sb.append((char) (l >= 10 ? 'A' + (l - 10) : '0' + l));
			}
		}
		return sb.toString();
	}
}
