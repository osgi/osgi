package nursery.rpwa;

import java.io.File;
import java.util.*;

import org.osgi.framework.Bundle;
import org.osgi.service.deploymentadmin.*;

public class DP implements DeploymentPackage {
	long			id;
	String			name;
	String			version;
	Hashtable			resources = new Hashtable();
	
	DP(long id, String name, String version ) {
		this.id = id;
		this.name = name;
		this.version = version;
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public void uninstall() {
		Set			participants = new HashSet();
		
		for ( Iterator r = resources.keySet().iterator(); r.hasNext(); ) {
			ResourceProcessor rp = (ResourceProcessor) r.next();
			Vector			  elements = (Vector) resources.get(rp);
			for ( Iterator i = elements.iterator(); i.hasNext(); ) {
				String s = (String) i.next();
				if ( ! participants.contains(rp)) {
					participants.add(rp);
					rp.begin(this, 0);
				}
				try {
					rp.dropped(s);
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
	}
	
	void add( String resource, ResourceProcessor rp ) {
		Vector	v = (Vector) resources.get(rp);
		if ( v == null ) {
			v = new Vector();
			resources.put(rp,v);
		}
		v.add(resource);
	}

		
	public Bundle[] listBundles() {
		return null;
	}

	public boolean isNew(Bundle b) {
		return false;
	}
	public boolean isUpdated(Bundle b) {
		return false;
	}
	public boolean isPendingRemoval(Bundle b) {
		return false;
	}

	public File getDataFile(Bundle bundle) {
		return null;
	}
}
