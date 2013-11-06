package org.osgi.impl.service.resourcemanagement.util;

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
import org.osgi.service.resourcemanagement.monitor.MemoryMonitor;

/**
 * This class persists and restores Memory Monitors into a csv file. The CSV
 * file contains the name of the context as well as the state of the monitor
 * 
 * @author mpcy8647
 * 
 */
public class Persistence {

	private static final String FILE_NAME = "persist.csv";

	public static void persistMemoryMonitors(Collection mms,
			BundleContext bundleContext) {

		File file = bundleContext.getDataFile(FILE_NAME);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PrintStream ps = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			ps = new PrintStream(fos);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Iterator it = mms.iterator(); it.hasNext();) {
			MemoryMonitor mm = (MemoryMonitor) it.next();
			ResourceMonitorInfo mmi = new ResourceMonitorInfo(mm);
			ps.println(mmi.toCsv());
		}

		ps.close();
		ps = null;
		file = null;

	}

	public static Collection loadMemoryMonitors(
			BundleContext bundleContext) {

		Collection mmis = new ArrayList();

		File file = bundleContext.getDataFile(FILE_NAME);
		FileReader fr = null;


		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			br = null;

			try {
				fr.close();
			} catch (IOException e) {
			}
			fr = null;
		}
		file = null;


		return mmis;
	}

}
