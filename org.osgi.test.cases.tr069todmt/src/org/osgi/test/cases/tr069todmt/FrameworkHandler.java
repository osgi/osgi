package org.osgi.test.cases.tr069todmt;

import org.osgi.framework.*;

public class FrameworkHandler {
	private BundleContext context;
	int startLevel =8;
	
	
//	class Record extends NODE {
//		String location;
//		Bundle bundle;
//		int requestedState;
//		String url;
//		boolean autoStart;
//		public Record(String location) {
//			this.location = location;
//		}
//	}
//	MAP<String,Record> bundle = new MAP<String,Record>() {
//		{
//			final Map<String, Record> map = new TreeMap<String, Record>();
//			org.osgi.framework.Bundle[] bundles = context.getBundles();
//			for (org.osgi.framework.Bundle bundle : bundles) {
//				Record record = new Record(bundle.getLocation());
//				record.bundle = bundle;
//				map.put(bundle.getLocation(), record);
//			}
//
//		}
//		public Record create(String name) {
//			return new Record(name);
//		}
//	};

	
	
	
	public int getX() {
		return 3;
	}
	
	
	public void commit() {
		
	}
}
