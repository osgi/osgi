
package org.osgi.impl.service.resourcemonitoring.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.service.resourcemonitoring.ResourceMonitor;

/**
 * This class persists and restores Monitors into a csv file. The CSV file
 * contains the name of the context as well as the state of the monitor. This
 * class should be used by a {@link ResourceMonitorFactory} to persist and
 * restore {@link ResourceMonitor}.
 * 
 * @author mpcy8647
 * 
 */
public class Persistence {

	/** FILE_NAME */
	public static final String	FILE_NAME	= "persist.csv";

	/**
	 * Persist a collection of monitors into the persistent data storage area of
	 * provided bundle context. The name of ResourceContext and the state of
	 * each {@link ResourceMonitor} is persisted into the fileName file using a
	 * CSV format.
	 * 
	 * @param mms collection of {@link ResourceMonitor} to persist
	 * @param bundleContext bundle context (to get access to a bundle persistent
	 *        storage
	 * @param pFileName name of the persist file. if null, use the default (
	 *        {@link #FILE_NAME})
	 * @throws PersistenceException if any exception occurs
	 */
	public static void persistMonitors(Collection<ResourceMonitor<Long>> mms,
			BundleContext bundleContext, String pFileName)
			throws PersistenceException {

		// retrieve the fileName to be used or get default one.
		String fileName = pFileName;
		if (fileName == null) {
			fileName = FILE_NAME;
		}

		// retrieves a File object and delete it if exists
		File file = bundleContext.getDataFile(fileName);
		if (file.exists()) {
			file.delete();
		}

		// create new file
		try {
			file.createNewFile();
		} catch (IOException e1) {
			throw new PersistenceException("Unable to create file " + fileName
					+ " used to persist ResourceMonitor", e1);
		}

		PrintStream ps = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			ps = new PrintStream(fos);
		} catch (FileNotFoundException e) {
			throw new PersistenceException("Persist file " + fileName
					+ "not found.", e);
		}

		for (Iterator<ResourceMonitor<Long>> it = mms.iterator(); it
				.hasNext();) {
			ResourceMonitor<Long> mm = it.next();
			ResourceMonitorInfo mmi = new ResourceMonitorInfo(mm);
			ps.println(mmi.toCsv());
		}

		ps.close();
		ps = null;
		file = null;

	}

	/**
	 * Load persisted ResourceMonitor data from the fileName file. This file is
	 * a CSV file containing the name of ResourceContext and the state of each
	 * ResourceMonitor.
	 * 
	 * @param bundleContext used to load fileName file from the bundle storage
	 *        area
	 * @param pFileName name of the file. if null, use default one (
	 *        {@link #FILE_NAME} ).
	 * @return Collection of {@link ResourceMonitorInfo}. This collection may be
	 *         null.
	 * @throws PersistenceException if any exception occurs
	 */
	public static Collection<ResourceMonitorInfo> loadMonitors(
			BundleContext bundleContext,
			String pFileName) throws PersistenceException {

		Collection<ResourceMonitorInfo> mmis = new ArrayList<>();

		String fileName = pFileName;
		if (fileName == null) {
			fileName = FILE_NAME;
		}

		File file = bundleContext.getDataFile(fileName);
		FileReader fr = null;

		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			System.out.println("catched FileNotFoundException");
			fr = null;
		} catch (Throwable t) {
			System.out.println("catched throwable ");
			fr = null;
		}
		if (fr != null) {
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					// each line is a MemoryResource monitor previously
					// persisted
					// resource context name ; status
					try {
						ResourceMonitorInfo mmi = new ResourceMonitorInfo(line);
						mmis.add(mmi);
					} catch (NullPointerException e) {
						// nothing to do
					}
				}

			} catch (IOException e) {
				throw new PersistenceException("Unable to read the file "
						+ fileName, e);
			}

			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			fr = null;

			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			br = null;
		}
		file = null;

		return mmis;
	}

}
