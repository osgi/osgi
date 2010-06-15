package org.osgi.service.obr;

import java.net.URL;
import java.util.Map;

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

	public Part[] getParts() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addRepositoryListener(RepositoryListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void removeRepositoryListener(RepositoryListener listener) {
		// TODO Auto-generated method stub
		
	}
}
