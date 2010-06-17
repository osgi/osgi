package org.osgi.service.obr.admin;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.osgi.service.obr.Capability;
import org.osgi.service.obr.Part;
import org.osgi.service.obr.Requirement;

/**
 * TODO implement this class...
 * 
 * @version $Id$
 * @deprecated This is proposed API. As a result, this API may never be
 *             published or the final API may change substantially by the time
 *             of final publication. You are cautioned against relying upon this
 *             API.
 */
public class URLRepository implements Repository {

	public URLRepository(URL url) {
	}
	
	public long getLastModified() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<Part> parts() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<Capability> capabilities(Requirement filter) {
		// TODO Auto-generated method stub
		return null;
	}
}
